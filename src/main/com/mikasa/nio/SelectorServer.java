package com.mikasa.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * 使用selector管理channel
 *
 * @author aiLun
 * @date 2023/5/24-09:54
 */
@Slf4j
public class SelectorServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress("localhost", 8888));
        //注册selector 需要设置为非阻塞
        ssc.configureBlocking(false);
        Selector selector = Selector.open();
        SelectionKey sscKey = ssc.register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        while (true) {
            //在没有事件时，该方法会阻塞，
            //select 在事件未处理时，它不会阻塞，事件发生后 要么处理 要么取消
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                log.info("key:{}", selectionKey);
                //连接事件
                if (selectionKey.isAcceptable()) {
                    //读事件的channel只会是ServerSocketChannel
                    ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel sc = channel.accept();
                    //注册selector 需要设置为非阻塞
                    sc.configureBlocking(false);
                    SelectionKey scKey = sc.register(selector, 0, null);
                    scKey.interestOps(SelectionKey.OP_READ);
                } else if (selectionKey.isReadable()) {
                    try {
                        SocketChannel sc = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(124);
                        //如果为-1 表示客户端断开连接
                        int read = sc.read(buffer);
                        if (read == -1) {
                            selectionKey.cancel();
                        }
                        buffer.flip();
                        log.info("readMessage:{}", StandardCharsets.UTF_8.decode(buffer));
                    } catch (IOException e) {
                       log.error("e:",e);
                        selectionKey.cancel();
                    }
                }
                iterator.remove();
            }
        }
    }
}
