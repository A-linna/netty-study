package com.mikasa.mychat.mes;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * @author aiLun
 * @date 2023/5/31-11:31
 */
@Data
@Accessors(chain = true)
public class GroupGetMembersResponseMessage extends Message{
    private Set<String> members;

    @Override
    public int getMessageType() {
        return GET_GROUP_MEMBERS_RESPONSE;
    }
}
