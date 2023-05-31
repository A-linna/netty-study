package com.mikasa.mychat.mes;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author aiLun
 * @date 2023/5/31-09:33
 */
@Data
@Accessors(chain = true)
public class ChatResponseMessage extends Message{
    private String from;
    private String context;

    private Boolean success;
    private String reason;

    @Override
    public int getMessageType() {
        return CHAT_RESPONSE_MESSAGE;
    }
}
