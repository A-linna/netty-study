package com.mikasa.mychat.mes;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author aiLun
 * @date 2023/5/30-16:54
 */
@Data
@AllArgsConstructor
public class LoginResponseMessage extends Message {
    private boolean success;
    private String info;

    @Override
    public int getMessageType() {
        return LOGIN_RESPONSE_MESSAGE;
    }
}
