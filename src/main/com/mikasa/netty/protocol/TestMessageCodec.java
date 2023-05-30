package com.mikasa.netty.protocol;

import com.mikasa.netty.protocol.message.LoginRequestMessage;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author aiLun
 * @date 2023/5/30-16:03
 */
public class TestMessageCodec {
    public static void main(String[] args) {
        EmbeddedChannel channel = new EmbeddedChannel(new LoggingHandler(LogLevel.DEBUG),new MessageCodec());
        LoginRequestMessage message = new LoginRequestMessage("zhangsan", "123345");
        channel.writeOutbound(message);
    }
}
