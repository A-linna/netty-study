package com.mikasa;

import java.nio.ByteBuffer;

/**
 * @author aiLun
 * @date 2023/5/23-09:43
 */
public class BufferTest {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 1);
        buffer.put((byte) 2);
        buffer.put((byte) 3);
        System.out.println(buffer);
    }
}
