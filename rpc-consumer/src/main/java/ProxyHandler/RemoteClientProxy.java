package ProxyHandler;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * create by chenjiayang on 2018/9/24
 */
public class RemoteClientProxy {
    public static Object mockRemoteProxyInstance(final Class<?> serviceInterface, final InetSocketAddress address) {
        Object proxyInstance = Proxy.newProxyInstance(
                serviceInterface.getClassLoader(),
                new Class<?>[]{serviceInterface},
                new RPCInvocationHandler(address, serviceInterface));
        return proxyInstance;
    }
}
