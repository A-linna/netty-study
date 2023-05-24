package com.mikasa.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * nio阻塞模式下的服务
 *
 * @author aiLun
 * @date 2023/5/23-19:58
 */
@Slf4j
public class Server {

    public static void main(String[] args) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        //1 获取一个ServerSocketChannel
        ServerSocketChannel ssc = ServerSocketChannel.open();
        //2 绑定本机端口
        ssc.bind(new InetSocketAddress("localhost", 8888));
        //设置非阻塞
        ssc.configureBlocking(false);
        List<SocketChannel> scList = new ArrayList<>();
        while (true) {
            //3监听客户端连接，若没有连接则会阻塞
            log.info("connecting--------");
            SocketChannel sc = ssc.accept();
            if (Objects.nonNull(sc)) {
                sc.configureBlocking(false);
                log.info("connected---------:{}",sc);
                scList.add(sc);
            }
            for (SocketChannel socketChannel : scList) {
                int read = socketChannel.read(buffer);
                if (read != 0) {
                    buffer.flip();
                    log.info("read data:{}", StandardCharsets.UTF_8.decode(buffer));
                    buffer.clear();
                    log.info("read after=======");
                }

            }
        }
    }
}
