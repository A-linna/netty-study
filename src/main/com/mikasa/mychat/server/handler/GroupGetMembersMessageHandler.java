package com.mikasa.mychat.server.handler;

import com.mikasa.mychat.mes.GroupGetMembersRequestMessage;
import com.mikasa.mychat.mes.GroupGetMembersResponseMessage;
import com.mikasa.mychat.server.session.GroupSessionFactory;
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
