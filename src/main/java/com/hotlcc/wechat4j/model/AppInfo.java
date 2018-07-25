package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONObject;

public final class AppInfo {
    private Integer Type;
    private String AppID;

    public AppInfo(JSONObject info) {
        if (info == null) {
            return;
        }
        this.Type = info.getInteger("Type");
        this.AppID = info.getString("AppID");
    }

    public Integer getType() {
        return Type;
    }

    public String getAppID() {
        return AppID;
    }
}
