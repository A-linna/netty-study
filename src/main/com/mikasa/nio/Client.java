package com.mikasa.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author aiLun
 * @date 2023/5/23-20:05
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();

        sc.connect(new InetSocketAddress("localhost", 8888));
        System.out.println("sssss");
    }
}
