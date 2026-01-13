package edu.univ.erp.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final Properties props = new Properties();

    static {
        try (InputStream in = ConfigLoader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (in == null) {
                throw new RuntimeException("config.properties not found in resources folder!");
            }

            props.load(in);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load config.properties: " + e.getMessage(), e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
