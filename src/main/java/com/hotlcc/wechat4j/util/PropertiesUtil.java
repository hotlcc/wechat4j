package com.hotlcc.wechat4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private PropertiesUtil() {
    }

    private static final Properties prop = new Properties();

    static {
        loadProperties(new String[]{
                "config/app.properties",
                "config/webwx-url.properties"
        });
    }

    private static void loadProperties(String[] paths) {
        if (paths == null) {
            return;
        }

        for (String path : paths) {
            InputStream is = null;
            try {
                is = PropertiesUtil.class.getClassLoader().getResourceAsStream(path);
                prop.load(is);
            } catch (Exception e) {
                logger.error("Loading properties file \"" + path + "\" error.", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static String getProperty(String key) {
        return prop.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }
}
