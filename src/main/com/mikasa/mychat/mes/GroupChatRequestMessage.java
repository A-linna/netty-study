package com.mikasa.mychat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author aiLun
 * @date 2023/5/30-20:41
 */
@Data
@AllArgsConstructor
public class GroupChatRequestMessage extends Message {
    //用户名称
    private String username;
    //群名称
    private String groupName;

    //消息内容
    private String context;

    @Override
    public int getMessageType() {
        return GROUP_CHAR_REQUEST_MESSAGE;
    }
}
