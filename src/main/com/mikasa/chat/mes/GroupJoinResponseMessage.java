package com.mikasa.chat.mes;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author aiLun
 * @date 2023/5/31-11:45
 */
@Data
@Accessors(chain = true)
public class GroupJoinResponseMessage extends Message{

    private Boolean success;
    private String reason;
    @Override
    public int getMessageType() {
        return JOIN_GROUP_RESPONSE;
    }
}
