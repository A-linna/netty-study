package com.mikasa.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @author aiLun
 * @date 2023/5/25-15:43
 */
public class HelloClient {
    public static void main(String[] args) throws InterruptedException {

        ChannelFuture channelFuture = new Bootstrap()
                //添加eventLoop
                .group(new NioEventLoopGroup())
                //选择客户端channel实现
                .channel(NioSocketChannel.class)
                //添加处理器
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    //在连接建立后调用
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new StringEncoder());
                    }
                })
                //connect 是一个异步非阻塞的方法
                //main线程发起调用，真正执行连接connect是NioEventGroup中的线程
                .connect(new InetSocketAddress("localhost", 9999));
        channelFuture.sync()//阻塞方法 直到连接建立
                .channel()//代表连接对象
                .writeAndFlush("hello");
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel channel = future.channel();
                channel.writeAndFlush("hello");
            }
        });

    }
}
