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
public class RpcRequestMessage extends Message {

    /**
     * 调用的接口全限定名，服务端根据它找到实现
     */
    private String interfaceName;

    /**
     * 调用的方法名
     */
    private String methodName;

    /**
     * 返回值类型
     */
    private Class<?> returnType;

    /**
     * 方法参数类型数组
     */
    private Class<?>[] parameterTypes;

    /**
     * 方法参数值 数组
     */
    private Object[] parameterValues;

    public RpcRequestMessage(int sequenceId, String interfaceName,
                             String methodName, Class<?> returnType,
                             Class<?>[] parameterTypes, Object[] parameterValues) {
        super.sequenceId = sequenceId;
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.parameterValues = parameterValues;
    }

    @Override
    public int getMessageType() {
        return RPC_MESSAGE_TYPE_REQUEST;
    }
}
