package com.mikasa.rpc.utils;

import io.netty.util.concurrent.Promise;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aiLun
 * @date 2023/6/1-13:32
 */
public class PromiseUtil {

    private static final Map<Integer, Promise<Object>> promiseMap= new ConcurrentHashMap<>();


    public static Promise<Object> getPromise(Integer sequenceId) {
        return promiseMap.remove(sequenceId);
    }

    public static void addPromise(Integer sequenceId,Promise<Object>promise) {
        promiseMap.put(sequenceId, promise);
    }
}
