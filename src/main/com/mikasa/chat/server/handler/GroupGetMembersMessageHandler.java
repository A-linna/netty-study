package com.mikasa.chat.server.handler;

import com.mikasa.chat.mes.GroupGetMembersRequestMessage;
import com.mikasa.chat.mes.GroupGetMembersResponseMessage;
import com.mikasa.chat.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Set;

/**
 * @author aiLun
 * @date 2023/5/31-11:30
 */
@ChannelHandler.Sharable
public class GroupGetMembersMessageHandler extends SimpleChannelInboundHandler<GroupGetMembersRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupGetMembersRequestMessage msg) throws Exception {
        Set<String> members = GroupSessionFactory.getGroupSession("memory").getMembers(msg.getName());
        ctx.writeAndFlush(new GroupGetMembersResponseMessage().setMembers(members));
    }
}
