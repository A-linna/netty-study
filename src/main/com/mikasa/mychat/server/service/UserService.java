package com.mikasa.mychat.server.service;

/**
 * 用户管理接口
 * @author aiLun
 * @date 2023/5/30-16:34
 */
public interface UserService {

    boolean login(String userName, String password);
}
