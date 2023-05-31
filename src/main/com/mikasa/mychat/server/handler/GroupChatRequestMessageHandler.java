package com.mikasa.mychat.server.handler;

import com.mikasa.mychat.mes.GroupChatRequestMessage;
import com.mikasa.mychat.mes.GroupChatResponseMessage;
import com.mikasa.mychat.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * @author aiLun
 * @date 2023/5/31-11:03
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> channelList = GroupSessionFactory.getGroupSession("memory").getMembersChannels(msg.getGroupName());
        GroupChatResponseMessage response = new GroupChatResponseMessage().setFrom(msg.getUsername()).setContext(msg.getContext());
        channelList.forEach(channel -> channel.writeAndFlush(response));

    }
}
