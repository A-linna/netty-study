package com.mikasa.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @author aiLun
 * @date 2023/5/30-09:36
 */
@Slf4j
public class TestLengthFieldDecoder {
    public static void main(String[] args) {
        LengthFieldBasedFrameDecoder decoder = new LengthFieldBasedFrameDecoder(1024, 0, 4, 0, 4);
        LoggingHandler loggingHandler = new LoggingHandler(LogLevel.DEBUG);

        EmbeddedChannel channel = new EmbeddedChannel(decoder, loggingHandler);

        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        fillBuf("hello,word", buffer);
        fillBuf("ni ma mei l", buffer);
        channel.writeInbound(buffer);
    }

    private static void fillBuf(String mes, ByteBuf buf) {
        byte[] bytes = mes.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

}
