package com.mikasa.mychat.server.handler;

import com.mikasa.mychat.mes.GroupCreateRequstMessage;
import com.mikasa.mychat.mes.GroupCreateResponseMessage;
import com.mikasa.mychat.server.session.Group;
import com.mikasa.mychat.server.session.GroupSessionFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;
import java.util.Objects;

/**
 * @author aiLun
 * @date 2023/5/31-10:22
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequstMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequstMessage msg) throws Exception {
        String name = msg.getName();
        Group group = GroupSessionFactory.getGroupSession("memory").createGroup(name, msg.getMembers());
        if (Objects.nonNull(group)) {
            List<Channel> channelList = GroupSessionFactory.getGroupSession("memory").getMembersChannels(name);
            GroupCreateResponseMessage groupResponse = new GroupCreateResponseMessage().setSuccess(true).setReason("您已被拉入["+name+"]群");
            channelList.forEach(channel->{
                channel.writeAndFlush(groupResponse);
            });
            //发送建群成功消息
            GroupCreateResponseMessage response = new GroupCreateResponseMessage().setSuccess(false).setReason(name + " 创建成功");
            ctx.writeAndFlush(response);
        }else {
            GroupCreateResponseMessage response = new GroupCreateResponseMessage().setSuccess(true).setReason(name + " 已存在");
            ctx.writeAndFlush(response);
        }
    }
}
