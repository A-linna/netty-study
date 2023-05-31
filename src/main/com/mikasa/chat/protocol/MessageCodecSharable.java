package com.mikasa.chat.protocol;

import com.mikasa.chat.mes.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author aiLun
 * @date 2023/5/30-16:56
 */
@Slf4j
@ChannelHandler.Sharable
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf,Message> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, List<Object> out) throws Exception {
        ByteBuf buf = ctx.alloc().buffer();
        //1  4个字节的魔数
        buf.writeBytes(new byte[]{'a', 'b', 'c', 'd'});
        // 2  1字节的协议版本号
        buf.writeByte(1);
        //3  1字节的序列化算法  0 jdk序列化 1 json  2
        buf.writeByte(0);
        //4  1字节的消息指令类型
        buf.writeByte(msg.getMessageType());
        //5  4个字节的请求序号
        buf.writeInt(msg.getSequenceId());

        //无意义的字节填充，保持2的整数倍
        buf.writeByte(0xff);

        //6 将msg转换为byte[]
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] msgByte = bos.toByteArray();
        //7 4字节的 消息长度
        buf.writeInt(msgByte.length);
        //8 写入内容

        buf.writeBytes(msgByte);
        out.add(buf);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int magicNum = msg.readInt();
        byte version = msg.readByte();
        byte serializableType = msg.readByte();
        byte messageType = msg.readByte();
        int sequenceId = msg.readInt();
        byte b = msg.readByte();//无意义的填充字节
        int length = msg.readInt(); //字节长度
        byte[] bytes = new byte[length];
        msg .readBytes(bytes,0,length);
        log.info("magicNum:{},version:{},serializableType:{},messageType:{}," +
                "sequenceId:{},length:{}", magicNum, version, serializableType, messageType, sequenceId, length);

        //jdk序列化
        Message message =null;
        if (0 == serializableType) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            message = (Message) ois.readObject();
        }
        log.info("message:{}",message);
        out.add(message);
    }
}
