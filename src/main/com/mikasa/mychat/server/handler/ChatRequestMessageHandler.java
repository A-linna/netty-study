package com.mikasa.mychat.server.handler;

import com.mikasa.mychat.mes.ChatRequestMessage;
import com.mikasa.mychat.mes.ChatResponseMessage;
import com.mikasa.mychat.mes.Message;
import com.mikasa.mychat.server.session.SessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * @author aiLun
 * @date 2023/5/31-09:23
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        String to = msg.getTo();
        Channel channel = SessionFactory.getSession("memory").getChannel(to);
        //拿到接收方绑定的channel
        if (Objects.nonNull(channel)) {
            ChatResponseMessage chatResponseMessage = new ChatResponseMessage().setFrom(msg.getFrom()).setContext(msg.getContext());
            channel.writeAndFlush(chatResponseMessage);
        }else {
            Message response = new ChatResponseMessage().setSuccess(false).setReason("对方用户不存在or不在线");
            ctx.channel().writeAndFlush(response);
        }
    }
}
