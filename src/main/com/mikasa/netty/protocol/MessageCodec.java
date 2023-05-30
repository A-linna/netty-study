package com.mikasa.netty.protocol;

import com.mikasa.netty.protocol.message.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author aiLun
 * @date 2023/5/30-15:25
 */
@Slf4j
public class MessageCodec extends ByteToMessageCodec<Message> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
        //1  4个字节的魔数
        out.writeBytes(new byte[]{'a', 'b', 'c', 'd'});
        // 2  1字节的协议版本号
        out.writeByte(1);
        //3  1字节的序列化算法  0 jdk序列化 1 json  2
        out.writeByte(0);
        //4  1字节的消息指令类型
        out.writeByte(msg.getMessageType());
        //5  4个字节的请求序号
        out.writeInt(msg.getSequenceId());

        //无意义的字节填充，保持2的整数倍
        out.writeByte(0xff);

        //6 将msg转换为byte[]
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        byte[] msgByte = bos.toByteArray();
        //7 4字节的 消息长度
        out.writeInt(msgByte.length);
        //8 写入内容

        out.writeBytes(msgByte);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializableType = in.readByte();
        byte messageType = in.readByte();
        int sequenceId = in.readInt();
        byte b = in.readByte();//无意义的填充字节
        int length = in.readInt(); //字节长度
        byte[] bytes = new byte[length];
         in.readBytes(bytes,0,length);
        //jdk序列化
        Message msg =null;
        if (0 == serializableType) {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
             msg = (Message) ois.readObject();
        }
        log.info("magicNum:{},version:{},serializableType:{},messageType:{}," +
                "sequenceId:{},length:{}", magicNum, version, serializableType, messageType, sequenceId, length);
        log.info("message:{}",msg);
        out.add(msg);
    }
}
