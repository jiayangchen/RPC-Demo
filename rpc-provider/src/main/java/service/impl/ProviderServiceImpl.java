package service.impl;

import service.ProviderService;
import service.builder.ProviderBuilder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * create by chenjiayang on 2018/9/24
 */
public class ProviderServiceImpl implements ProviderService {

    //线程池相关
    private static AtomicInteger NUM = new AtomicInteger(1);
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(8, 16,
            0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), r -> {
                String name = "Register-thread-" + NUM.getAndIncrement();
                Thread t = new Thread(r, name);
                t.setDaemon(true);
                return t;
            });
    //服务注册中心
    private static final Map<String, Class> REGISTERY_CENTER = new ConcurrentHashMap<>();
    private static volatile Boolean IS_RUNNING = false;
    private static final Logger logger = Logger.getLogger(ProviderServiceImpl.class.getName());
    //监听端口
    private static int LISTENING_PORT = 0;
    private static String LISTENING_IP = "";

    public ProviderServiceImpl(ProviderBuilder builder) {
        LISTENING_PORT = builder.getPort();
        LISTENING_IP = builder.getIp();
        for(Map.Entry<Class, Class> entry : builder.getRegisterMethods().entrySet()) {
            REGISTERY_CENTER.put(entry.getKey().getName(), entry.getValue());
        }
    }

    @Override
    public Boolean stop() {
        IS_RUNNING = false;
        EXECUTOR.shutdown();
        return true;
    }

    @Override
    public Boolean interrupt() {
        IS_RUNNING = false;
        EXECUTOR.shutdownNow();
        return true;
    }

    @Override
    public Boolean isRunning() {
        return IS_RUNNING;
    }

    @Override
    public Integer fetchPort() {
        return LISTENING_PORT;
    }

    @Override
    public void start() throws IOException {
        ServerSocket server = new ServerSocket();
        server.bind(new InetSocketAddress(LISTENING_IP, LISTENING_PORT));
        logger.info("----- Server start listening at " + LISTENING_PORT + "-------");
        try {
            while (true) {
                EXECUTOR.submit(new InvokeTask(server.accept()));
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if(!server.isClosed()) {
                server.close();
            }
        }
    }

    private class InvokeTask implements Runnable {
        Socket client = null;

        InvokeTask(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            ObjectInputStream inputStream = null;
            ObjectOutputStream outputStream = null;
            try {
                inputStream = new ObjectInputStream(client.getInputStream());
                //类名
                String className = inputStream.readUTF();
                //方法名
                String methodName = inputStream.readUTF();
                //参数类型列表
                Class<?>[] paramTypeList = (Class<?>[]) inputStream.readObject();
                //参数列表
                Object[] paramList = (Object[]) inputStream.readObject();
                Class serviceClassInstance = REGISTERY_CENTER.get(className);
                if(serviceClassInstance == null) {
                    throw new ClassNotFoundException("implement class " + className + " not found");
                }
                //反射根据方法名和参数类型列表选择调用的方法
                Method method = serviceClassInstance.getMethod(methodName, paramTypeList);
                //JDK动态代理生成代理对象
                Object result = method.invoke(serviceClassInstance.newInstance(), paramList);
                //写入返回结果
                outputStream = new ObjectOutputStream(client.getOutputStream());
                outputStream.writeObject(result);
            } catch (IOException e) {
                logger.warning("IOException " + e.getMessage());
            } catch (ClassNotFoundException e) {
                logger.warning("ClassNotFoundException " + e.getMessage());
            } catch (NoSuchMethodException e) {
                logger.warning("NoSuchMethodException " + e.getMessage());
            } catch (IllegalAccessException e) {
                logger.warning("IllegalAccessException " + e.getMessage());
            } catch (InstantiationException e) {
                logger.warning("InstantiationException " + e.getMessage());
            } catch (InvocationTargetException e) {
                logger.warning("InvocationTargetException " + e.getMessage());
            } finally {
                if(outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if(client != null) {
                    try {
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
