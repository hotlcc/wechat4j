package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONObject;

public final class RecommendInfo {
    public RecommendInfo(JSONObject info) {
        if (info == null) {
            return;
        }
        this.Ticket = info.getString("Ticket");
        this.UserName = info.getString("UserName");
        this.Sex = info.getInteger("Sex");
        this.AttrStatus = info.getInteger("AttrStatus");
        this.City = info.getString("City");
        this.NickName = info.getString("NickName");
        this.Scene = info.getInteger("Scene");
        this.Province = info.getString("Province");
        this.Content = info.getString("Content");
        this.Alias = info.getString("Alias");
        this.Signature = info.getString("Signature");
        this.OpCode = info.getInteger("OpCode");
        this.QQNum = info.getLong("QQNum");
        this.VerifyFlag = info.getInteger("VerifyFlag");
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
}
