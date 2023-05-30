package com.mikasa.netty.protocol;

import com.mikasa.netty.protocol.message.LoginRequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author aiLun
 * @date 2023/5/30-16:03
 */
public class TestMessageCodec {
    public static void main(String[] args) throws Exception {
        EmbeddedChannel channel = new EmbeddedChannel(
                //最大长度
                //长度字段的偏移量
                //长度字段本身字节
                new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0),
                new LoggingHandler(LogLevel.DEBUG),

                new MessageCodec());
        //encode
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123345");
        channel.writeOutbound(message);

        //decode
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        new MessageCodec().encode(null, message, buffer);
        ByteBuf buf1 = buffer.slice(0, 100);
        buf1.retain();
        ByteBuf buf2 = buffer.slice(100, buffer.readableBytes() - 100);
        //测试 拆包情况
        channel.writeInbound(buf1);
        channel.writeInbound(buf2);
    }
}
