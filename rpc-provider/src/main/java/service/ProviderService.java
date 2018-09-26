package service;

import java.io.IOException;

/**
 * create by chenjiayang on 2018/9/24
 */
public interface ProviderService {
    Boolean stop();
    Boolean interrupt();
    void start() throws IOException;
    Boolean isRunning();
    Integer fetchPort();
}
