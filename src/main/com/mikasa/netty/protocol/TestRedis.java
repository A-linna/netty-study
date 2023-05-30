package com.mikasa.netty.protocol;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * 规范格式
 * 1、间隔符号，在Linux下是\r\n，在Windows下是\n;
 * 2、简单字符串 Simple Strings, 以 "+"加号 开头;
 * 3、错误 Errors, 以"-"减号 开头;
 * 4、整数型 Integer， 以 ":" 冒号开头;
 * 5、大字符串类型 Bulk Strings, 以 "$"美元符号开头，长度限制512M;
 * 6、数组类型 Arrays，以 "*"星号开头。
 * @author aiLun
 * @date 2023/5/30-10:05
 */
public class TestRedis {
    /**
     *  set key value
     *  传输数组类型 先传数组个数 然后发送每个命令以及键值的长度,每个之间要用间隔符号分割
     *  *3 数组的个数
     *  $3
     *  set
     *  $key的长度
     *  key
     *  $value的长度
     *  value
     */

    public static void main(String[] args)  {
        final byte[] LINE = {13, 10}; //回车 + 换行
        NioEventLoopGroup work = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap()
                    .channel(NioSocketChannel.class)
                    .group(work)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    // 发送 set name zhangsan
                                    ByteBuf buffer = ctx.alloc().buffer();
                                    buffer.writeBytes("*3".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$3".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("set".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$4".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("name".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("$8".getBytes());
                                    buffer.writeBytes(LINE);
                                    buffer.writeBytes("zhangsan".getBytes());
                                    buffer.writeBytes(LINE);
                                    ctx.writeAndFlush(buffer);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    ByteBuf buf = (ByteBuf)msg;
                                    System.out.println(buf.toString(StandardCharsets.UTF_8));
                                }
                            });
                        }
                    });
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("localhost", 6379)).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {

        }
    }
}
