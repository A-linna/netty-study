package com.mikasa.mychat.server.service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aiLun
 * @date 2023/5/30-16:35
 */
public class UserServiceMemoryImpl implements UserService{

    private ConcurrentHashMap<String, String> userMap = new ConcurrentHashMap<>();

    {
        userMap.put("zhangSan", "123456");
        userMap.put("lisi", "123456");
        userMap.put("wangWu", "123456");
        userMap.put("zhaoLiu", "123456");
    }
    @Override
    public boolean login(String userName, String password) {
        String userPassword = userMap.get(userName);
        return userPassword != null && userPassword.equals(password);
    }
}
