package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class AppInfo {
    private AppInfo() {
    }

    private Integer Type;
    private String AppID;

    public Integer getType() {
        return Type;
    }

    public String getAppID() {
        return AppID;
    }

    public static AppInfo valueOf(JSONObject info) {
        if (info == null) {
            return null;
        }
        AppInfo appInfo = new AppInfo();
        appInfo.Type = info.getInteger("Type");
        appInfo.AppID = info.getString("AppID");
        return appInfo;
    }

    public static List<AppInfo> valueOf(JSONArray infos) {
        if (infos == null) {
            return null;
        }

        List<AppInfo> appInfos = new ArrayList<>();
        for (int i = 0, len = infos.size(); i < len; i++) {
            JSONObject info = infos.getJSONObject(i);
            appInfos.add(AppInfo.valueOf(info));
        }
        return appInfos;
    }
}
