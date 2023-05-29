package com.mikasa.netty.byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

/**
 * @author aiLun
 * @date 2023/5/29-17:08
 */
public class ByteBufTest {
    public static void main(String[] args) {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10,100);
        System.out.println(buffer);
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.directBuffer();
    }
}
