package com.mikasa.mychat.server;

import com.mikasa.mychat.protocol.MessageCodecSharable;
import com.mikasa.mychat.protocol.ProtocolFrameCodec;
import com.mikasa.mychat.server.handler.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
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
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        LoginRequestMessageHandler LOGIN_HANDLER = new LoginRequestMessageHandler();
        ChatRequestMessageHandler CHAT_HANDLER = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler GROUP_CREATE = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler GROUP_CHAT = new GroupChatRequestMessageHandler();
        GroupGetMembersMessageHandler GROUP_GET_MEMBERS = new GroupGetMembersMessageHandler();
        GroupQuitRequestMessageHandler GROUP_QUIT = new GroupQuitRequestMessageHandler();
        GroupJoinRequestMessageHandler GROUP_JOIN = new GroupJoinRequestMessageHandler();
        QuitHandler QUIT_HANDLER = new QuitHandler();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(boos, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameCodec());
                            ch.pipeline().addLast(LOGGING_HANDLER);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            //6秒内没收到channel的数据 会触发一个IdleState#READER_IDLE 事件
                            ch.pipeline().addLast(new IdleStateHandler(6, 0, 0));
                            ch.pipeline().addLast(new ChannelDuplexHandler() {
                                //用来触发特殊事件
                                @Override
                                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                    IdleStateEvent event = (IdleStateEvent) evt;
                                    if (IdleState.READER_IDLE == event.state()) {
                                        ctx.channel().close();
                                    }
                                }
                            });
                            ch.pipeline().addLast(LOGIN_HANDLER);
                            ch.pipeline().addLast(CHAT_HANDLER);
                            ch.pipeline().addLast(GROUP_CREATE);
                            ch.pipeline().addLast(GROUP_CHAT);
                            ch.pipeline().addLast(QUIT_HANDLER);
                            ch.pipeline().addLast(GROUP_GET_MEMBERS);
                            ch.pipeline().addLast(GROUP_JOIN);
                            ch.pipeline().addLast(GROUP_QUIT);
                        }
                    });

            ChannelFuture future = bootstrap.bind(8888).sync();
            future.channel().closeFuture();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
