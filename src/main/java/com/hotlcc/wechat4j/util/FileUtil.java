package com.hotlcc.wechat4j.util;

import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 文件工具类
 *
 * @author Allen
 */
public final class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    private FileUtil() {
    }

    /**
     * 从文件中获取二进制数据
     *
     * @param file 文件
     * @return 二进制数据
     */
    public static byte[] getBytes(File file) {
        if (file == null) {
            throw new IllegalArgumentException("参数file不能为null");
        }

        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;

            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            baos.flush();
            baos.close();
            fis.close();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取文件的ContentType
     *
     * @param file 文件
     * @return ContentType
     */
    public static ContentType getContentBody(File file) {
        String mimeType = new MimetypesFileTypeMap().getContentType(file);
        ContentType contentType = ContentType.parse(mimeType);
        return contentType;
    }
}
