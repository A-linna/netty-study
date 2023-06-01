package com.mikasa.rpc.handler;

import com.mikasa.rpc.msg.RpcResponseMessage;
import com.mikasa.rpc.utils.PromiseUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author aiLun
 * @date 2023/6/1-12:43
 */
@Slf4j
@ChannelHandler.Sharable
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        int sequenceId = msg.getSequenceId();
        Promise<Object> promise = PromiseUtil.getPromise(sequenceId);
        if (Objects.nonNull(promise)) {
            Exception exceptionValue = msg.getExceptionValue();
            Object returnValue = msg.getReturnValue();
            if (Objects.nonNull(exceptionValue)) {
                promise.setFailure(exceptionValue);
            } else {
                promise.setSuccess(returnValue);
            }
        }
    }
}
