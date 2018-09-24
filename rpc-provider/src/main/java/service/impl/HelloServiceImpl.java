package service.impl;

import service.HelloService;

/**
 * create by chenjiayang on 2018/9/24
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "Hi, " + name;
    }
}
