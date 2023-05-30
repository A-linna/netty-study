package com.mikasa.netty.protocol.message;

import lombok.Data;

import java.io.Serializable;

/**
 * @author aiLun
 * @date 2023/5/30-15:25
 */
@Data
public abstract class Msg implements Serializable {
    private int sequenceId;
    private int messageType;


    public  abstract int getMessageType();

    //登录请求
    protected static final int LOGIN_REQUEST_MESSAGE = 0;
    //登录响应
    protected static final int LOGIN_RESPONSE_MESSAGE = 1;


    static {

    }

}
