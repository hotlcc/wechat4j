package com.hotlcc.wechat4j.model;

import com.hotlcc.wechat4j.util.WechatUtil;

/**
 * 基本请求模型
 */
public class BaseRequest {
    public BaseRequest() {
    }

    public BaseRequest(String DeviceID, String Sid, String Skey, String Uin) {
        this.DeviceID = DeviceID;
        this.Sid = Sid;
        this.Skey = Skey;
        this.Uin = Uin;
    }

    public BaseRequest(String Sid, String Skey, String Uin) {
        this(WechatUtil.createDeviceID(), Sid, Skey, Uin);
    }

    private String DeviceID;
    private String Sid;
    private String Skey;
    private String Uin;

    public String getDeviceID() {
        return DeviceID;
    }

    public void setDeviceID(String deviceID) {
        DeviceID = deviceID;
    }

    public String getSid() {
        return Sid;
    }

    public void setSid(String sid) {
        Sid = sid;
    }

    public String getSkey() {
        return Skey;
    }

    public void setSkey(String skey) {
        Skey = skey;
    }

    public String getUin() {
        return Uin;
    }

    public void setUin(String uin) {
        Uin = uin;
    }
}
