package com.mikasa.rpc.service;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author aiLun
 * @date 2023/6/1-09:27
 */
public class ServiceFactory {

    private static Map<String, Object> map = new ConcurrentHashMap<>();


    /**
     * @return
     */
    public static Object getService(Class<?> aClass) {
        String simpleName = aClass.getSimpleName();
        Object object = map.get(simpleName);
        if (Objects.nonNull(object)) {
            return object;
        } else {
            synchronized (aClass) {
                Object obj = map.get(simpleName);
                if (Objects.nonNull(obj)) {
                    return obj;
                }
                String packageName = aClass.getPackage().getName();
                ClassLoader classLoader = aClass.getClassLoader();
                try {
                    Enumeration<URL> resources = classLoader.getResources(packageName.replace(".", "/"));
                    while (resources.hasMoreElements()) {
                        URL resource = resources.nextElement();
                        File directory = new File(resource.getFile());
                        for (File file : Objects.requireNonNull(directory.listFiles())) {
                            String fileName = file.getName();
                            if (fileName.endsWith(".class")) {
                                Class<?> clazz = Class.forName(packageName + "." + fileName.substring(0, fileName.length() - 6));
                                if (aClass.isAssignableFrom(clazz) && !aClass.equals(clazz)) {
                                    Object o = clazz.newInstance();
                                    map.put(simpleName, o);
                                    return o;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }

    }


}
