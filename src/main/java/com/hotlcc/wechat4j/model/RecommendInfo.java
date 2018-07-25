package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class RecommendInfo {
    private RecommendInfo() {
    }

    private String Ticket;
    private String UserName;
    private Integer Sex;
    private Integer AttrStatus;
    private String City;
    private String NickName;
    private Integer Scene;
    private String Province;
    private String Content;
    private String Alias;
    private String Signature;
    private Integer OpCode;
    private Long QQNum;
    private Integer VerifyFlag;

    public String getTicket() {
        return Ticket;
    }

    public String getUserName() {
        return UserName;
    }

    public Integer getSex() {
        return Sex;
    }

    public Integer getAttrStatus() {
        return AttrStatus;
    }

    public String getCity() {
        return City;
    }

    public String getNickName() {
        return NickName;
    }

    public Integer getScene() {
        return Scene;
    }

    public String getProvince() {
        return Province;
    }

    public String getContent() {
        return Content;
    }

    public String getAlias() {
        return Alias;
    }

    public String getSignature() {
        return Signature;
    }

    public Integer getOpCode() {
        return OpCode;
    }

    public Long getQQNum() {
        return QQNum;
    }

    public Integer getVerifyFlag() {
        return VerifyFlag;
    }

    public static RecommendInfo valueOf(JSONObject info) {
        if (info == null) {
            return null;
        }

        RecommendInfo recommendInfo = new RecommendInfo();

        recommendInfo.Ticket = info.getString("Ticket");
        recommendInfo.UserName = info.getString("UserName");
        recommendInfo.Sex = info.getInteger("Sex");
        recommendInfo.AttrStatus = info.getInteger("AttrStatus");
        recommendInfo.City = info.getString("City");
        recommendInfo.NickName = info.getString("NickName");
        recommendInfo.Scene = info.getInteger("Scene");
        recommendInfo.Province = info.getString("Province");
        recommendInfo.Content = info.getString("Content");
        recommendInfo.Alias = info.getString("Alias");
        recommendInfo.Signature = info.getString("Signature");
        recommendInfo.OpCode = info.getInteger("OpCode");
        recommendInfo.QQNum = info.getLong("QQNum");
        recommendInfo.VerifyFlag = info.getInteger("VerifyFlag");

        return recommendInfo;
    }

    public static List<RecommendInfo> valueOf(JSONArray infos) {
        if (infos == null) {
            return null;
        }

        List<RecommendInfo> recommendInfos = new ArrayList<>();
        for (int i = 0, len = infos.size(); i < len; i++) {
            JSONObject info = infos.getJSONObject(i);
            recommendInfos.add(RecommendInfo.valueOf(info));
        }
        return recommendInfos;
    }
}
