package com.mikasa.chat.server.session;

/**
 * @author aiLun
 * @date 2023/5/30-23:02
 */
public class SessionFactory {

    private static final SessionMemoryImpl sessionMemory = new SessionMemoryImpl();

    public static Session getSession(String type) {
        if ("memory".equals(type)) {
            return sessionMemory;
        }
        throw new IllegalArgumentException();
    }
}
