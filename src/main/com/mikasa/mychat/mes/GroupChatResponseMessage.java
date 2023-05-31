package com.mikasa.mychat.mes;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author aiLun
 * @date 2023/5/31-11:06
 */
@Data
@Accessors(chain = true)
public class GroupChatResponseMessage extends Message{
    private String from;
    private String context;

    @Override
    public int getMessageType() {
        return GROUP_CHAR_RESPONSE_MESSAGE;
    }
}
