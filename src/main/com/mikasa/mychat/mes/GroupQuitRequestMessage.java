package com.mikasa.mychat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author aiLun
 * @date 2023/5/30-20:52
 */
@Data
@AllArgsConstructor
public class GroupQuitRequestMessage extends Message{
    //用户名
    private String username;

    private String groupName;
    @Override
    public int getMessageType() {
        return 0;
    }
}
