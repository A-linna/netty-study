package com.mikasa.mychat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author aiLun
 * @date 2023/5/30-20:50
 */
@AllArgsConstructor
@Data
public class GroupJoinRequestMessage extends Message{
    //用户名
    private String username;
    //群名称
    private String groupName;
    @Override
    public int getMessageType() {

        return JOIN_GROUP_REQUEST;
    }
}
