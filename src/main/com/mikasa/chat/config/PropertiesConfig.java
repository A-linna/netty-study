package com.mikasa.chat.config;

import com.mikasa.chat.protocol.Serializable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author aiLun
 * @date 2023/5/31-14:38
 */
public abstract class PropertiesConfig {
    static Properties properties;

    static {
        InputStream in = PropertiesConfig.class.getResourceAsStream("/application.properties");
        properties = new Properties();
        try {
            properties.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static int getPort() {
        String port = properties.getProperty("server.port");
        if (port == null) {
            return 8888;
        }
        return Integer.parseInt(port);
    }

    public static Serializable.Algorithm getAlgorithm() {
        String algorithm = properties.getProperty("serialize.algorithm");
        if (algorithm == null) {
            return Serializable.Algorithm.JAVA;
        }
       return Serializable.Algorithm.valueOf(algorithm);
    }
}
