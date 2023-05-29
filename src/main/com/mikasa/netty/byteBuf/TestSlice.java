package com.mikasa.netty.byteBuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author aiLun
 * @date 2023/5/29-20:14
 */
@Slf4j
public class TestSlice {
    public static void main(String[] args) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j'});
        log.info("buf:{}",buf.toString(StandardCharsets.UTF_8));

        //在切片过程中，没有发生数据复制
        ByteBuf buf1 = buf.slice(0, 5);
        ByteBuf buf2 = buf.slice(5, 5);
        log.info("buf1:{}",buf1.toString(StandardCharsets.UTF_8));
        log.info("buf2:{}",buf2.toString(StandardCharsets.UTF_8));
        log.info("释放原有buf");
        buf1.setByte(0, 'c');
        log.info("buf1:{}",buf1.toString(StandardCharsets.UTF_8));
        log.info("buf:{}",buf.toString(StandardCharsets.UTF_8));

    }
}
