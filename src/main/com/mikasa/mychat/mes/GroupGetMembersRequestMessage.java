package com.mikasa.mychat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author aiLun
 * @date 2023/5/30-20:51
 */
@Data
@AllArgsConstructor
public class GroupGetMembersRequestMessage extends Message {
    //组名
    private String name;

    @Override
    public int getMessageType() {
        return GET_GROUP_MEMBERS_REQUEST;
    }
}
