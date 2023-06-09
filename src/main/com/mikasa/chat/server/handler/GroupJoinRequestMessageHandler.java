package com.mikasa.chat.server.handler;

import com.mikasa.chat.mes.GroupJoinRequestMessage;
import com.mikasa.chat.mes.GroupJoinResponseMessage;
import com.mikasa.chat.server.session.Group;
import com.mikasa.chat.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * @author aiLun
 * @date 2023/5/31-11:43
 */
@ChannelHandler.Sharable
public class GroupJoinRequestMessageHandler extends SimpleChannelInboundHandler<GroupJoinRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupJoinRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession("memory").joinMember(msg.getGroupName(), msg.getUsername());
        if (Objects.isNull(group)) {
            ctx.writeAndFlush(new GroupJoinResponseMessage().setSuccess(false).setReason("群不存在"));
        }else {
            ctx.writeAndFlush(new GroupJoinResponseMessage().setSuccess(true).setReason("加入成功"));
        }
    }
}
