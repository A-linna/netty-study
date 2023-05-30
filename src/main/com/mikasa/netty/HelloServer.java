package com.mikasa.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author aiLun
 * @date 2023/5/25-13:55
 */
@Slf4j
public class HelloServer {
    public static void main(String[] args) {
        //1.启动器，负责组装netty组件，启动服务器
        new ServerBootstrap()
                //2.BoosEventLoop,WorkEventLoop(selector,thread) group 组
                .group(new NioEventLoopGroup())
                //3.选择ServerSocketChannel的实现
                .channel(NioServerSocketChannel.class)
                //4.boos 负责连接，work(child)负责读写，决定了word(child)能执行哪些操作(handler)
                .childHandler(
                        //5.channel代表和客户端进行数据读写的通道，Initializer初始化，负责添加别的handler
                        //连接建立后会执行initChannel方法
                        new ChannelInitializer<NioSocketChannel>() {
                            @Override
                            protected void initChannel(NioSocketChannel socketChannel) throws Exception {
                                socketChannel.pipeline().addLast(new StringDecoder());//将byteBuf转换为字符串
                                socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() {//自定义handler
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        System.out.println(msg);
                                    }
                                });
                            }
                            //6 绑定端口
                        }).bind(9999);
        log.info("server start");
    }
}
