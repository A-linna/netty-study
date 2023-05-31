package com.mikasa.mychat.mes;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author aiLun
 * @date 2023/5/31-11:39
 */
@Data
@Accessors(chain = true)
public class GroupQuitResponseMessage extends Message{
    private Boolean success;
    private String retain;

    @Override
    public int getMessageType() {
        return QUIT_GROUP_RESPONSE;
    }
}
