package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.hotlcc.wechat4j.util.WechatUtil;

/**
 * 基本请求模型
 */
public class BaseRequest {
    public BaseRequest() {
    }

    public BaseRequest(String deviceID, String sid, String skey, String uin) {
        this.deviceID = deviceID;
        this.sid = sid;
        this.skey = skey;
        this.uin = uin;
    }

    public BaseRequest(String Sid, String Skey, String Uin) {
        this(WechatUtil.createDeviceID(), Sid, Skey, Uin);
    }

    @JSONField(name = "DeviceID")
    private String deviceID;
    @JSONField(name = "Sid")
    private String sid;
    @JSONField(name = "Skey")
    private String skey;
    @JSONField(name = "Uin")
    private String uin;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        deviceID = deviceID;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        sid = sid;
    }

    public String getSkey() {
        return skey;
    }

    public void setSkey(String skey) {
        skey = skey;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        uin = uin;
    }
}
