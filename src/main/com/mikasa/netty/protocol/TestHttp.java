package com.mikasa.netty.protocol;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author aiLun
 * @date 2023/5/30-10:36
 */
@Slf4j
public class TestHttp {
    public static void main(String[] args) {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup word = new NioEventLoopGroup();
        try {
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boos, word)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                        //添加http协议 编解码器
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new SimpleChannelInboundHandler<DefaultHttpRequest>() {
                            @Override
                            protected void channelRead0(ChannelHandlerContext ctx, DefaultHttpRequest msg) throws Exception {
                                String uri = msg.uri();
                                log.info("uri:{}", uri);
                                DefaultFullHttpResponse response = new DefaultFullHttpResponse(msg.protocolVersion(), HttpResponseStatus.OK);
                                byte[] bytes = "<h1>hello word</h1>".getBytes();
                                response.content().writeBytes(bytes);
                                response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, bytes.length);
                                ctx.writeAndFlush(response);
                            }
                        });
                    }
                });

            ChannelFuture future = bootstrap.bind(8080).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            boos.shutdownGracefully();
            word.shutdownGracefully();
        }
    }
}
