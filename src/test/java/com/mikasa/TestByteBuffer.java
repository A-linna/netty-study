package com.mikasa;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author aiLun
 * @date 2023/5/22-21:24
 */
@Slf4j
public class TestByteBuffer {



    public static void main(String[] args) {
        //fileChannel 获取通过 输入输出流 或者 RandomAccessFile
        try (FileChannel channel= new FileInputStream("data.txt").getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate(10);
            while (true) {
                //从channel读取数据向buffer写入
                int len = channel.read(byteBuffer);
                log.info("读取到的字节数:{}",len);
                if (-1==len) {
                    break;
                }
                //切换为读模式
                byteBuffer.flip();
                //是否还有剩余未读数据
                while (byteBuffer.hasRemaining()) {
                    byte b = byteBuffer.get();
                    log.info("读取到的字节为：{}",(char)b);
                }
                //切换为写模式
                byteBuffer.clear();
            }
        } catch (IOException e) {

        }
    }
}
