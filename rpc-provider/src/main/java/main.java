import service.HelloService;
import service.builder.ProviderBuilder;
import service.impl.HelloServiceImpl;
import service.impl.RegisterServiceImpl;

import java.io.IOException;

/**
 * create by chenjiayang on 2018/9/24
 */
public class main {
    public static void main(String[] args) {
        RegisterServiceImpl registerService = ProviderBuilder.newBuilder()
                .listeningIP("localhost")
                .listeningPort(8088)
                .registerMethod(HelloService.class, HelloServiceImpl.class)
                .build();
        try {
            registerService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
