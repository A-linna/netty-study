package com.mikasa.chat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * @author aiLun
 * @date 2023/5/30-20:46
 */
@Data
@AllArgsConstructor
public class GroupCreateRequstMessage extends Message{
    //群名称
    private String name;
    //群成员
    private Set<String> members;

    @Override
    public int getMessageType() {
        return CREATE_GROUP_REQUEST;
    }
}
