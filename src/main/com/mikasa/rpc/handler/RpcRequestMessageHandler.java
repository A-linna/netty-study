package com.mikasa.rpc.handler;

import com.mikasa.rpc.msg.RpcRequestMessage;
import com.mikasa.rpc.msg.RpcResponseMessage;
import com.mikasa.rpc.service.ServiceFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author aiLun
 * @date 2023/5/31-20:16
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        int sequenceId = msg.getSequenceId();
        RpcResponseMessage response = new RpcResponseMessage();
        response.setSequenceId(sequenceId);
        Class<?> aClass = Class.forName(msg.getInterfaceName());
        try {
            Object service = ServiceFactory.getService(aClass);
            Method method = aClass.getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(service, msg.getParameterValues());
            response.setReturnValue(invoke);
        } catch (Exception e) {
            response.setExceptionValue(new Exception("远程调用异常:" + e.getCause().getMessage()));
            log.error("RpcRequestHandler",e);
        }
        ctx.writeAndFlush(response);
    }

}
