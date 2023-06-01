package com.mikasa.chat.client;

import com.mikasa.chat.config.PropertiesConfig;
import com.mikasa.chat.mes.*;
import com.mikasa.chat.protocol.MessageCodecSharable;
import com.mikasa.chat.protocol.ProtocolFrameCodec;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 学习demo  不考虑 错误场景
 *
 * @author aiLun
 * @date 2023/5/30-16:24
 */
@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup work = new NioEventLoopGroup();
        LoggingHandler LOGIN_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        CountDownLatch WAIT_FOR_LOGIN = new CountDownLatch(1);
        AtomicBoolean LOGIN_RESULT = new AtomicBoolean(false);
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(work)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameCodec());
                            ch.pipeline().addLast(LOGIN_HANDLER);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            //3秒内channel没有写数据 会触发一个IdleState#WRITER_IDLE 事件
                            ch.pipeline().addLast(new IdleStateHandler(0, 3, 0));
                            ch.pipeline().addLast(new ChannelDuplexHandler() {
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    if (IdleState.WRITER_IDLE == event.state()) {
                                        PingMessage pingMessage = new PingMessage();
                                        ChannelFuture channelFuture = ctx.writeAndFlush(pingMessage);
                                        channelFuture.addListener((ChannelFutureListener) listener -> {
                                            if (!listener.isSuccess()) {
                                                log.error("event", listener.cause());
                                            }
                                        });
                                    }
                                }
                            });
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    log.info("{}",msg);
                                    if (msg instanceof LoginResponseMessage) {
                                        LoginResponseMessage loginResponse = (LoginResponseMessage) msg;
                                        if (loginResponse.isSuccess()) {
                                            LOGIN_RESULT.set(true);
                                        }
                                        WAIT_FOR_LOGIN.countDown();
                                    }
                                }

                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    new Thread(() -> {
                                        Scanner scanner = new Scanner(System.in);
                                        System.out.println("请输入用户名");
                                        String username = scanner.nextLine();
                                        System.out.println("请输入密码");
                                        String password = scanner.nextLine();
                                        LoginRequestMessage loginRequest = new LoginRequestMessage(username, password);
                                        ChannelFuture channelFuture = ctx.channel().writeAndFlush(loginRequest);
                                        channelFuture.addListener((ChannelFutureListener) listener -> {
                                            if (!listener.isSuccess()) {
                                                log.error("writeError", listener.cause());
                                            }
                                        });
                                        try {
                                            WAIT_FOR_LOGIN.await();
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                        if (!LOGIN_RESULT.get()) {
                                            ctx.channel().close();
                                            return;
                                        }
                                        while (true) {
                                            System.out.println("===============");
                                            System.out.println("send [username] [content]");
                                            System.out.println("gSend [group name] [content]");
                                            System.out.println("gCreate [group name] [m1,m2,m3...]");
                                            System.out.println("gMembers [group name]");
                                            System.out.println("gJoin [group name]");
                                            System.out.println("gQuit [group name]");
                                            System.out.println("quit");
                                            String command = scanner.nextLine();
                                            String[] commandArr = command.split(" ");
                                            switch (commandArr[0]) {
                                                case "send":
                                                    ctx.writeAndFlush(new ChatRequestMessage(username, commandArr[1], commandArr[2]));
                                                    break;
                                                case "gSend":
                                                    ctx.writeAndFlush(new GroupChatRequestMessage(username, commandArr[1], commandArr[2]));
                                                    break;
                                                case "gCreate":
                                                    Set<String> members = Arrays.stream(commandArr[2].split(",")).collect(Collectors.toSet());
                                                    members.add(username);
                                                    ctx.writeAndFlush(new GroupCreateRequstMessage(commandArr[1], members));
                                                    break;
                                                case "gMembers":
                                                    ctx.writeAndFlush(new GroupGetMembersRequestMessage(commandArr[1]));
                                                    break;
                                                case "gJoin":
                                                    ctx.writeAndFlush(new GroupJoinRequestMessage(username, commandArr[1]));
                                                    break;
                                                case "gQuit":
                                                    ctx.writeAndFlush(new GroupQuitRequestMessage(username, commandArr[1]));
                                                    break;
                                                case "quit":
                                                    ctx.channel().close();
                                                    break;
                                            }
                                        }
                                    }, "systemIn").start();
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", PropertiesConfig.getPort())).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            work.shutdownGracefully();
        }
    }
}
