package com.mikasa.chat.server.handler;

import com.mikasa.chat.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author aiLun
 * @date 2023/5/31-11:23
 */
@ChannelHandler.Sharable
@Slf4j
public class QuitHandler extends ChannelInboundHandlerAdapter {
    /**
     * 连接断开会触发此事件
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        SessionFactory.getSession("memory").unbind(channel);
        log.info("断开连接：{}",channel);
    }

    /**
     * 异常事件
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        SessionFactory.getSession("memory").unbind(channel);
        log.error("异常退出：{}",channel,cause);
    }
}
