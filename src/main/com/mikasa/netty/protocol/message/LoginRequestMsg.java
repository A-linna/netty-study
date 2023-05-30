package com.mikasa.netty.protocol.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author aiLun
 * @date 2023/5/30-15:35
 */
@Data
@AllArgsConstructor
public class LoginRequestMsg extends Msg {
    private String userName;
    private String password;


    @Override
    public int getMessageType() {
        return LOGIN_REQUEST_MESSAGE;
    }
}
