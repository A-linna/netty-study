package com.mikasa.chat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author aiLun
 * @date 2023/5/30-20:35
 */
@Data
@AllArgsConstructor
public class ChatRequestMessage extends Message {
    //发送人
    private String from;
    //接收人
    private String to;
    //内容
    private String context;

    @Override
    public int getMessageType() {
        return CHAT_REQUEST_MESSAGE;
    }
}
