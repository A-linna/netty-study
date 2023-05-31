package com.mikasa.mychat.server.handler;

import com.mikasa.mychat.mes.LoginRequestMessage;
import com.mikasa.mychat.mes.LoginResponseMessage;
import com.mikasa.mychat.server.service.UserService;
import com.mikasa.mychat.server.service.UserServiceFactory;
import com.mikasa.mychat.server.session.SessionFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author aiLun
 * @date 2023/5/31-09:21
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        String username = msg.getUserName();
        String password = msg.getPassword();
        UserService userService = UserServiceFactory.getUserService("memory");
        boolean login = userService.login(username, password);
        LoginResponseMessage loginResponse;
        if (login) {
            SessionFactory.getSession("memory").bind(ctx.channel(), username);
            loginResponse = new LoginResponseMessage(true, "登录成功");
        } else {
            loginResponse = new LoginResponseMessage(false, "账号或密码错误");
        }
        ctx.writeAndFlush(loginResponse);
    }
}
