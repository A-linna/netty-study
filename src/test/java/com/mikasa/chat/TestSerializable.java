package com.mikasa.chat;

import com.mikasa.chat.config.PropertiesConfig;
import com.mikasa.chat.mes.LoginRequestMessage;
import com.mikasa.chat.mes.Message;
import com.mikasa.chat.protocol.MessageCodecSharable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import org.junit.Test;

/**
 * @author aiLun
 * @date 2023/5/31-15:08
 */
public class TestSerializable {

    /**
     * 测试序列化
     */
    @Test
    public void testSerialize() {
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(loggingHandler, messageCodec, loggingHandler);

        LoginRequestMessage loginRequest = new LoginRequestMessage("zhangSan", "123456");
        channel.writeOutbound(loginRequest);
    }

    /**
     * 测试 反序列化
     */
    @Test
    public void testDeserialize() {
        MessageCodecSharable messageCodec = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(loggingHandler, messageCodec, loggingHandler);
        LoginRequestMessage loginRequest = new LoginRequestMessage("zhangSan", "123456");
        ByteBuf byteBuf = messageToByteBuf(loginRequest);
        channel.writeInbound(byteBuf);
    }

    private ByteBuf messageToByteBuf(Message msg) {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
        //1  4个字节的魔数
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd'});
        // 2  1字节的协议版本号
        buf.writeByte(1);
        //3  1字节的序列化算法
        buf.writeByte(PropertiesConfig.getAlgorithm().ordinal());
        //4  1字节的消息指令类型
        buf.writeByte(msg.getMessageType());
        //5  4个字节的请求序号
        buf.writeInt(msg.getSequenceId());

        //无意义的字节填充，保持2的整数倍
        buf.writeByte(0xff);

        byte[] msgByte = PropertiesConfig.getAlgorithm().serialize(msg);
        //7 4字节的 消息长度
        buf.writeInt(msgByte.length);
        //8 写入内容
        buf.writeBytes(msgByte);
        return buf;
    }
}
