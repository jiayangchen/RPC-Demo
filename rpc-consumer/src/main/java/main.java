import ProxyHandler.RemoteClientProxy;
import api.HelloService;

import java.net.InetSocketAddress;

/**
 * create by chenjiayang on 2018/9/24
 */
public class main {
    public static void main(String[] args) {
        HelloService service = (HelloService) RemoteClientProxy.mockRemoteProxyInstance(HelloService.class, new InetSocketAddress("localhost", 8088));
        System.out.println(service.sayHello("chenjiayang"));
    }
}
