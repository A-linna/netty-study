package com.mikasa.mychat.server.service;

/**
 * @author aiLun
 * @date 2023/5/30-17:23
 */
public class UserServiceFactory {

    public static UserService getUserService(String type) {
        switch (type) {
            case "memory":
                return new UserServiceMemoryImpl();
            default:
                return new UserServiceMemoryImpl();
        }
    }
}
