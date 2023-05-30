package com.mikasa.mychat.server;

import com.mikasa.mychat.mes.LoginRequestMessage;
import com.mikasa.mychat.mes.LoginResponseMessage;
import com.mikasa.mychat.protocol.MessageCodecSharable;
import com.mikasa.mychat.protocol.ProtocolFrameCodec;
import com.mikasa.mychat.server.service.UserService;
import com.mikasa.mychat.server.service.UserServiceFactory;
import com.mikasa.mychat.server.session.SessionFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author aiLun
 * @date 2023/5/30-16:34
 */
@Slf4j
public class CharServer {
    public static void main(String[] args) {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        LoggingHandler LOGIN_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(boos, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameCodec());
                            ch.pipeline().addLast(LOGIN_HANDLER);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            ch.pipeline().addLast(new SimpleChannelInboundHandler<LoginRequestMessage>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
                                    log.info("read:{}",msg.toString());
                                    String userName = msg.getUserName();
                                    String password = msg.getPassword();
                                    UserService userService = UserServiceFactory.getUserService("memory");
                                    boolean login = userService.login(userName, password);
                                    LoginResponseMessage loginResponse;
                                    if (login) {
                                        SessionFactory.getSession("memory").bind(ctx.channel(), userName);
                                        loginResponse = new LoginResponseMessage(true, "登录成功");
                                    }else {
                                        loginResponse = new LoginResponseMessage(false, "账号或密码错误");
                                    }
                                    ctx.writeAndFlush(loginResponse);
                                }
                            });
                        }
                    });

            ChannelFuture future = bootstrap.bind(8888).sync();
            future.channel().closeFuture();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
