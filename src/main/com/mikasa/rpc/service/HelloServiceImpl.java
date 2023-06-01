package com.mikasa.rpc.service;

/**
 * @author aiLun
 * @date 2023/6/1-09:22
 */
public class HelloServiceImpl implements HelloService{
    @Override
    public String say(String name) {
        int i = 1 / 0;
        return "你好"+name;
    }
}
