package com.mikasa.mychat.server.session;

/**
 * @author aiLun
 * @date 2023/5/31-10:24
 */
public class GroupSessionFactory {
    private static final GroupSession groupSessionMemory = new GroupSessionMemoryImpl();


    public static GroupSession getGroupSession(String type) {
        if ("memory".equals(type)) {
            return groupSessionMemory;
        }
        throw new IllegalArgumentException();
    }

}
