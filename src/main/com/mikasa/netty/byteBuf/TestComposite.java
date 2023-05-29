package com.mikasa.netty.byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;

/**
 * @author aiLun
 * @date 2023/5/29-20:38
 */
public class TestComposite {
    public static void main(String[] args) {
        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer(5);
        ByteBuf buf2= ByteBufAllocator.DEFAULT.buffer(5);
        buf1.writeBytes(new byte[]{1,2, 3, 4, 5});
        buf2.writeBytes(new byte[]{6, 7, 8, 9, 10});

        CompositeByteBuf byteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        byteBuf.addComponents(true, buf1, buf2);
        System.out.println(byteBuf);
    }
}
