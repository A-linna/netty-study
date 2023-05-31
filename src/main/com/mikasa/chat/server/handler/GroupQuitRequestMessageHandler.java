package com.mikasa.chat.server.handler;

import com.mikasa.chat.mes.GroupQuitRequestMessage;
import com.mikasa.chat.mes.GroupQuitResponseMessage;
import com.mikasa.chat.server.session.Group;
import com.mikasa.chat.server.session.GroupSessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Objects;

/**
 * @author aiLun
 * @date 2023/5/31-11:34
 */
@ChannelHandler.Sharable
public class GroupQuitRequestMessageHandler extends SimpleChannelInboundHandler<GroupQuitRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupQuitRequestMessage msg) throws Exception {
        Group group = GroupSessionFactory.getGroupSession("memory").removeGroup(msg.getGroupName(), msg.getUsername());
        if (Objects.isNull(group)) {
            ctx.writeAndFlush(new GroupQuitResponseMessage().setSuccess(false).setRetain("退出群聊失败"));
        }else {
            ctx.writeAndFlush(new GroupQuitResponseMessage().setSuccess(true).setRetain("已退出群聊"));
        }
    }
}
