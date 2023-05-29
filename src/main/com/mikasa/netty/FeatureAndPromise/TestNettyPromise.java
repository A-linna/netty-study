package com.mikasa.netty.FeatureAndPromise;

import io.netty.channel.EventLoop;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;

/**
 * @author aiLun
 * @date 2023/5/29-15:52
 */
@Slf4j
public class TestNettyPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //1 准备eventLoop对象
        EventLoop eventLoop = new NioEventLoopGroup().next();

        //2 主动创建Promise，存储结果对象
        DefaultPromise<Integer> promise = new DefaultPromise<>(eventLoop);

        new Thread(() -> {
            //3 任意线程开始计算，计算完毕后向promise中填充结果
            log.info("计算结果");
            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            promise.setSuccess(3333);
        }).start();

        //4 获取结果
        //因为promise 是netty Feature的子接口，所以可以用JDK的api 也可以用netty Feature 的api
        log.info("等待结果");
        log.info("结果为：{}",promise.get());
    }
}
