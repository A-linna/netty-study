package com.mikasa.netty.FeatureAndPromise;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author aiLun
 * @date 2023/5/29-15:29
 */
@Slf4j
public class TestJdkFeature {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        Future<Integer> feature = executor.submit(() -> {
            log.info("执行计算");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return 50;
        });
        log.info("等待结果");
        log.info("结果为：{}", feature.get());

    }
}
