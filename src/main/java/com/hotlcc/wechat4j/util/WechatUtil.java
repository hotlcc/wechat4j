package com.hotlcc.wechat4j.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.entity.ContentType;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;

/**
 * 微信工具类
 *
 * @author Allen
 */
public final class WechatUtil {
    private WechatUtil() {
    }

    private static String STRING_CHARS_1 = "123456789";
    private static String STRING_CHARS_2 = "1234567890";

    /**
     * 创建一个设备ID
     *
     * @return 设备ID
     */
    public static String createDeviceID() {
        return "e" + RandomStringUtils.random(15, STRING_CHARS_1);
    }

    /**
     * 创建一个消息ID
     *
     * @return 消息ID
     */
    public static String createMsgId() {
        return System.currentTimeMillis() + RandomStringUtils.random(4, STRING_CHARS_2);
    }

    /**
     * 把SyncKeyList转为字符串格式
     *
     * @param SyncKeyList SyncKeyList
     * @return 字符串
     */
    public static String syncKeyListToString(JSONArray SyncKeyList) {
        if (SyncKeyList == null) {
            return null;
        }
        StringBuffer synckey = new StringBuffer();
        for (int i = 0, len = SyncKeyList.size(); i < len; i++) {
            JSONObject json = SyncKeyList.getJSONObject(i);
            if (i > 0) {
                synckey.append("|");
            }
            synckey.append(json.getString("Key"))
                    .append("_")
                    .append(json.getString("Val"));
        }
        return synckey.toString();
    }

    /**
     * 获取文件的ContentType
     *
     * @param file 文件
     * @return ContentType
     */
    public static ContentType getContentType(File file) {
        String mimeType = new MimetypesFileTypeMap().getContentType(file);
        ContentType contentType = ContentType.parse(mimeType);
        return contentType;
    }
}
