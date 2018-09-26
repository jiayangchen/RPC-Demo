package service.builder;

import service.impl.ProviderServiceImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * create by chenjiayang on 2018/9/24
 */
public class ProviderBuilder {
    private String ip;
    private Integer port;
    private Map<Class, Class> registerMethods = new HashMap<>();

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public Map<Class, Class> getRegisterMethods() {
        return registerMethods;
    }

    public static ProviderBuilder newBuilder() {
        return new ProviderBuilder();
    }

    public ProviderBuilder listeningIP(String listeningIP) {
        if(listeningIP == null || listeningIP.equals("")) {
            throw new IllegalArgumentException("listeningIP is invalid");
        }
        this.ip = listeningIP;
        return this;
    }

    public ProviderBuilder listeningPort(Integer listeningPort) {
        if(listeningPort < 0) {
            throw new IllegalArgumentException("port is invalid");
        }
        this.port = listeningPort;
        return this;
    }

    public ProviderBuilder registerMethod(Class method, Class impl) {
        if(method == null || impl == null) {
            throw new IllegalArgumentException("register Class is invalid");
        }
        this.registerMethods.put(method, impl);
        return this;
    }

    public ProviderServiceImpl build() {
        return new ProviderServiceImpl(this);
    }
}
