package com.mikasa.rpc;

import com.mikasa.chat.protocol.MessageCodecSharable;
import com.mikasa.chat.protocol.ProtocolFrameCodec;
import com.mikasa.rpc.handler.RpcResponseMessageHandler;
import com.mikasa.rpc.msg.RpcRequestMessage;
import com.mikasa.rpc.utils.PromiseUtil;
import com.mikasa.rpc.utils.Sequence;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author aiLun
 * @date 2023/6/1-13:04
 */
@Slf4j
public class RpcClientManager {
    private static Channel channel = null;
    private static final Object LOCK = new Object();


    public static <T> T getService(Class<T> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        Class<?>[] interfaces = new Class[]{clazz};
        Integer sequenceId = Sequence.getSequenceId();
        Object o = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            RpcRequestMessage rpcRequestMessage = new RpcRequestMessage(sequenceId,
                    clazz.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            getChannel().writeAndFlush(rpcRequestMessage);
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            PromiseUtil.addPromise(sequenceId, promise);
            //等待任务结束
            promise.await();
            if (!promise.isSuccess()) {
                throw new RuntimeException(promise.cause());
            }
            return promise.getNow();
        });

        return (T) o;
    }

    public static Channel getChannel() {
        if (Objects.isNull(channel)) {
            synchronized (LOCK) {
                if (Objects.isNull(channel)) {
                    initChannel();
                }
            }
        }
        return channel;
    }

    private static void initChannel() {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        LoggingHandler LOGGING = new LoggingHandler();
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        Bootstrap bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ProtocolFrameCodec());
                        ch.pipeline().addLast(LOGGING);
                        ch.pipeline().addLast(MESSAGE_CODEC);
                        ch.pipeline().addLast(RPC_HANDLER);
                    }
                });
        try {
            channel = bootstrap.connect(new InetSocketAddress("localhost", 8200)).sync().channel();
            channel.closeFuture().addListener((ChannelFutureListener) future -> {
                eventLoopGroup.shutdownGracefully();
            });
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
