package com.mikasa.buffer.nio;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @author aiLun
 * @date 2023/5/23-11:30
 */

@Slf4j
public class ByteBufferBaseApi {

    @Test
    public void allocate() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        log.info("byteBuffer:{}",byteBuffer);
    }

    @Test
    public void readAndWrite() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'1', '2', '3', '4', '5'});
        log.info("bufferInfo:{}", buffer);
        buffer.flip();
        log.info("buffer get:{}",(char)buffer.get()); ;
        log.info("buffer get:{}",(char)buffer.get()); ;
        log.info("buffer read after info :{}", buffer);
    }


    @Test
    public void rewind() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd', 'e'});
        log.info("bufferInfo:{}", buffer);
        buffer.flip();
        for (int i = 0; i < 4; i++) {
            log.info("buffer get:{}", (char)buffer.get());
        }
        log.info("bufferGetAfterInfo:{}", buffer);
        buffer.rewind();
        log.info("bufferRewindAfterInfo:{}", buffer);
        for (int i = 0; i < 4; i++) {
            log.info("buffer get:{}", (char)buffer.get());
        }
        log.info("bufferGetAfterInfo:{}", buffer);
    }

    @Test
    public void markAndReset() {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put(new byte[]{'a', 'c', 'A', 'E', 'r'});
        buffer.flip();
        log.info("buffer get:{}",(char)buffer.get());
        log.info("buffer get:{}",(char)buffer.get());
        buffer.mark();
        log.info("buffer get:{}",(char)buffer.get());
        log.info("buffer get:{}",(char)buffer.get());
        buffer.reset();
        log.info("buffer get:{}",(char)buffer.get());
        log.info("buffer get:{}",(char)buffer.get());
    }

    /**
     * 字符串转byteBuffer
     */
    @Test
    public void stringConversionBytebuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        //方式一 还是写模式 需要调用flip
        buffer.put("hello".getBytes());
        log.info("buffer info:{}",buffer);

        //读模式状态 实际是调用 CharBuffer.wrap(str)
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("hello");
        log.info("buffer1:{}", buffer1);

    }

    /**
     * 读到多个buffer中
     */
    @Test
    public void ScatteringReads() {
        try (FileChannel channel= new RandomAccessFile("data.txt","r").getChannel()){
            ByteBuffer b1 = ByteBuffer.allocate(3);
            ByteBuffer b2 = ByteBuffer.allocate(3);
            ByteBuffer b3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{b1, b2, b3});
            b1.flip();
            b2.flip();
            b3.flip();
            log.info("b1:{}", StandardCharsets.UTF_8.decode(b1));
            log.info("b2:{}", StandardCharsets.UTF_8.decode(b2));
            log.info("b3:{}", StandardCharsets.UTF_8.decode(b3));

        } catch (IOException e) {
        }
    }

    /**
     * 批量写 多个buffer
     */
    @Test
    public void gatheringWrite() {
        ByteBuffer b1 = StandardCharsets.UTF_8.encode("hello");
        ByteBuffer b2 = StandardCharsets.UTF_8.encode("world");
        ByteBuffer b3 = StandardCharsets.UTF_8.encode("屎壳郎");

        try (FileChannel channel = new RandomAccessFile("data1.txt", "rw").getChannel()) {
            channel.write(new ByteBuffer[]{b1, b2, b3});

        } catch (IOException e) {
        }
    }
}
