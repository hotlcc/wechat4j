package com.hotlcc.wechat4j.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.RandomStringUtils;

public final class WechatUtil {
    private WechatUtil() {
    }

    private static String STRING_CHARS_1 = "123456789";
    private static String STRING_CHARS_2 = "1234567890";

    /**
     * 创建一个设备ID
     *
     * @return
     */
    public static String createDeviceID() {
        return "e" + RandomStringUtils.random(15, STRING_CHARS_1);
    }

    /**
     * 创建一个消息ID
     *
     * @return
     */
    public static String createMsgId() {
        return System.currentTimeMillis() + RandomStringUtils.random(4, STRING_CHARS_2);
    }

    /**
     * 创建BaseRequest
     *
     * @return
     */
    public static JSONObject createBaseRequest(String DeviceID, String wxsid, String skey, String wxuin) {
        JSONObject BaseRequest = new JSONObject();
        BaseRequest.put("DeviceID", DeviceID);
        BaseRequest.put("Sid", wxsid);
        BaseRequest.put("Skey", skey);
        BaseRequest.put("Uin", wxuin);
        return BaseRequest;
    }

    /**
     * 创建BaseRequest
     *
     * @return
     */
    public static JSONObject createBaseRequest(String wxsid, String skey, String wxuin) {
        return createBaseRequest(createDeviceID(), wxsid, skey, wxuin);
    }

    /**
     * 把SyncKeyList转为字符串格式
     *
     * @param SyncKeyList
     * @return
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
     * 创建要发送的Msg
     *
     * @return
     */
    public static JSONObject createSendMsg(String Content, int Type, String FromUserName, String ToUserName) {
        JSONObject Msg = new JSONObject();
        String msgId = WechatUtil.createMsgId();
        Msg.put("ClientMsgId", msgId);
        Msg.put("Content", Content);
        Msg.put("FromUserName", FromUserName);
        Msg.put("LocalID", msgId);
        Msg.put("ToUserName", ToUserName);
        Msg.put("Type", Type);
        return Msg;
    }
}
