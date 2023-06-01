package com.mikasa.rpc.msg;

import com.mikasa.chat.mes.Message;
import lombok.Data;
import lombok.ToString;

/**
 * @author aiLun
 * @date 2023/5/31-19:55
 */
@Data
@ToString(callSuper = true)
public class RpcResponseMessage extends Message {

    /**
     * 返回值
     */
    private Object returnValue;
    /**
     * 异常值
     */
    private Exception exceptionValue;
    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }
}
