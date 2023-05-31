package com.mikasa.chat.protocol;

/**
 * @author aiLun
 * @date 2023/5/31-14:25
 */
public interface Serializable {

    /**
     * 反序列化
     * @param clazz
     * @param bytes
     * @return
     * @param <T>
     */
    <T> T deserialize(Class<T> clazz, byte[] bytes);

    /**
     * 序列化
     * @param object
     * @return
     * @param <T>
     */
    <T> byte[] serialize(T object);
}
