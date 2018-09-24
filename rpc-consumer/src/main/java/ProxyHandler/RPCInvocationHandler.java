package ProxyHandler;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * create by chenjiayang on 2018/9/24
 */
public class RPCInvocationHandler implements InvocationHandler {
    private InetSocketAddress address;
    private Class<?> serviceInterface;

    RPCInvocationHandler(InetSocketAddress address, Class<?> serviceInterface) {
        this.address = address;
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Socket socket = null;
            ObjectOutputStream outputStream = null;
            ObjectInputStream inputStream = null;
            try {
                socket = new Socket();
                socket.connect(address);
                outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeUTF(serviceInterface.getName());
                outputStream.writeUTF(method.getName());
                outputStream.writeObject(method.getParameterTypes());
                outputStream.writeObject(args);

                inputStream = new ObjectInputStream(socket.getInputStream());
                Object result = inputStream.readObject();
                if(result instanceof  Throwable){
                    throw (Throwable) result;
                }
                return result;
            } finally {
                if(socket != null) {
                    socket.close();
                }
                if(inputStream != null) {
                    inputStream.close();
                }
                if(outputStream != null) {
                    outputStream.close();
                }
            }
    }
}
