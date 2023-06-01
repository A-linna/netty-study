package com.mikasa.rpc;

import com.mikasa.chat.protocol.MessageCodecSharable;
import com.mikasa.chat.protocol.ProtocolFrameCodec;
import com.mikasa.rpc.handler.RpcRequestMessageHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author aiLun
 * @date 2023/5/31-20:04
 */
public class RpcServer {
    public static void main(String[] args) {
        NioEventLoopGroup boos = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();
        LoggingHandler LOGGING = new LoggingHandler();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcRequestMessageHandler RPC_REQUEST = new RpcRequestMessageHandler();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(boos, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolFrameCodec());
                            ch.pipeline().addLast(LOGGING);
                            ch.pipeline().addLast(MESSAGE_CODEC);
                            ch.pipeline().addLast(RPC_REQUEST);
                        }
                    });
            ChannelFuture future = bootstrap.bind(8200).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boos.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
