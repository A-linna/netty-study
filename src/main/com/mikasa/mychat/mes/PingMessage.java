package com.mikasa.mychat.mes;

/**
 * @author aiLun
 * @date 2023/5/31-14:05
 */
public class PingMessage extends Message{
    @Override
    public int getMessageType() {
        return PING;
    }
}
