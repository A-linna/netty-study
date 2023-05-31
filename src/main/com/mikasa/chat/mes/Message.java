package com.mikasa.chat.mes;

import lombok.Data;

import java.io.Serializable;

/**
 * @author aiLun
 * @date 2023/5/30-16:23
 */
@Data
public abstract class Message implements Serializable {

    private int sequenceId;
    private int messageType;


    public abstract int getMessageType();

    //登录请求
    protected static final int LOGIN_REQUEST_MESSAGE = 0;
    //登录响应
    protected static final int LOGIN_RESPONSE_MESSAGE = 1;

    //单聊请求
    protected static final int CHAT_REQUEST_MESSAGE = 2;
    //单聊响应
    protected static final int CHAT_RESPONSE_MESSAGE = 3;

    //群聊请求
    protected static final int GROUP_CHAR_REQUEST_MESSAGE = 4;
    //群聊响应
    protected static final int GROUP_CHAR_RESPONSE_MESSAGE = 5;

    //创建群聊请求
    protected static final int CREATE_GROUP_REQUEST = 6;
    //创建群聊响应
    protected static final int CREATE_GROUP_RESPONSE = 7;

    //获取群成员请求
    protected static final int GET_GROUP_MEMBERS_REQUEST = 8;
    //获取群成员响应
    protected static final int GET_GROUP_MEMBERS_RESPONSE = 9;

    //加入群聊请求
    protected static final int JOIN_GROUP_REQUEST = 10;
    //加入群聊响应
    protected static final int JOIN_GROUP_RESPONSE = 11;

    //退出群聊请求
    protected static final int QUIT_GROUP_REQUEST = 12;
    //退出群聊响应
    protected static final int QUIT_GROUP_RESPONSE = 13;

    protected static final int PING = 14;



}
