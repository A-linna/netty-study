package com.mikasa.mychat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author aiLun
 * @date 2023/5/30-16:53
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginRequestMessage extends Message{
    private String userName;
    private String password;
    @Override
    public int getMessageType() {
        return LOGIN_REQUEST_MESSAGE;
    }

}
