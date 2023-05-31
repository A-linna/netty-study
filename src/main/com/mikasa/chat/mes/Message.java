package com.mikasa.chat.mes;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author aiLun
 * @date 2023/5/30-16:23
 */
@Data
public abstract class Message implements Serializable {

    public static Map<Integer, Class<? extends Message>> map = new HashMap<>();


    private int sequenceId;
    private int messageType;

    public static Class<?extends Message>getMessageClass(int messageType) {
        return map.get(messageType);
    }

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

    static {
        map.put(LOGIN_REQUEST_MESSAGE, LoginRequestMessage.class);
        map.put(LOGIN_RESPONSE_MESSAGE, LoginResponseMessage.class);
        map.put(CHAT_REQUEST_MESSAGE, ChatRequestMessage.class);
        map.put(CHAT_RESPONSE_MESSAGE, ChatResponseMessage.class);
        map.put(GROUP_CHAR_REQUEST_MESSAGE, GroupChatRequestMessage.class);
        map.put(GROUP_CHAR_RESPONSE_MESSAGE, GroupChatResponseMessage.class);
        map.put(CREATE_GROUP_REQUEST, GroupCreateRequstMessage.class);
        map.put(CREATE_GROUP_RESPONSE, GroupCreateResponseMessage.class);
        map.put(GET_GROUP_MEMBERS_REQUEST, GroupGetMembersRequestMessage.class);
        map.put(GET_GROUP_MEMBERS_RESPONSE, GroupGetMembersResponseMessage.class);
        map.put(JOIN_GROUP_REQUEST, GroupJoinRequestMessage.class);
        map.put(JOIN_GROUP_RESPONSE, GroupJoinResponseMessage.class);
        map.put(QUIT_GROUP_REQUEST, GroupQuitRequestMessage.class);
        map.put(QUIT_GROUP_RESPONSE, GroupQuitResponseMessage.class);
        map.put(PING, PingMessage.class);
    }
}
