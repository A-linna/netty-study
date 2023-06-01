package com.mikasa.chat.protocol;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * @author aiLun
 * @date 2023/5/31-14:25
 */
public interface Serializable {

    Gson gson = new GsonBuilder().registerTypeAdapter(Class.class, new ClassCodec()).create();
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

    enum Algorithm implements Serializable{
        JAVA {
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {

                try ( ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                      ObjectInputStream ois = new ObjectInputStream(bis); ){
                    return (T)ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException("反序列化失败");
                }
            }
            @Override
            public <T> byte[] serialize(T object) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(object);
                    return bos.toByteArray();
                } catch (IOException e) {
                    throw new RuntimeException("序列化失败");
                }
            }
        },


        JSON{
            @Override
            public <T> T deserialize(Class<T> clazz, byte[] bytes) {

                String json = new String(bytes,StandardCharsets.UTF_8);
                return gson.fromJson(json, clazz);
            }

            @Override
            public <T> byte[] serialize(T object) {
                String json = gson.toJson(object);
                return json.getBytes(StandardCharsets.UTF_8);
            }
        };
    }

    static class ClassCodec implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>>{

        @Override
        public Class<?> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            String str = jsonElement.getAsString();
            try {
                return Class.forName(str);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Class<?> aClass, Type type, JsonSerializationContext jsonSerializationContext) {

            return new JsonPrimitive(aClass.getName());
        }
    }
}
