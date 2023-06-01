package com.mikasa.rpc.utils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author aiLun
 * @date 2023/6/1-13:17
 */
public class Sequence {
    private static AtomicInteger atomicInteger = new AtomicInteger();

    public static Integer getSequenceId() {
        return atomicInteger.incrementAndGet();
    }

}
