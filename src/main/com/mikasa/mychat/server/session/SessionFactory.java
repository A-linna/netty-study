package com.mikasa.mychat.server.session;

/**
 * @author aiLun
 * @date 2023/5/30-23:02
 */
public class SessionFactory {

    public static Session getSession(String type) {
        if ("memory".equals(type)) {
            return new SessionMemoryImpl();
        }
        throw new IllegalArgumentException();
    }
}
