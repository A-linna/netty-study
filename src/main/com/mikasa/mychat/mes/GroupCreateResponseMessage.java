package com.mikasa.mychat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author aiLun
 * @date 2023/5/31-10:40
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class GroupCreateResponseMessage extends Message{

    private Boolean success;
    private String reason;
    @Override
    public int getMessageType() {
        return CREATE_GROUP_RESPONSE;
    }
}
