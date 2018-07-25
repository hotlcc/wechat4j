package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.ArrayList;
import java.util.List;

public final class RecommendInfo {
    private RecommendInfo() {
    }

    @JSONField(name = "Ticket")
    private String ticket;
    @JSONField(name = "UserName")
    private String userName;
    @JSONField(name = "Sex")
    private Integer sex;
    @JSONField(name = "AttrStatus")
    private Integer attrStatus;
    @JSONField(name = "City")
    private String city;
    @JSONField(name = "NickName")
    private String nickName;
    @JSONField(name = "Scene")
    private Integer scene;
    @JSONField(name = "Province")
    private String province;
    @JSONField(name = "Content")
    private String content;
    @JSONField(name = "Alias")
    private String alias;
    @JSONField(name = "Signature")
    private String signature;
    @JSONField(name = "OpCode")
    private Integer opCode;
    @JSONField(name = "QQNum")
    private Long qqNum;
    @JSONField(name = "VerifyFlag")
    private Integer verifyFlag;

    public String getTicket() {
        return ticket;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getSex() {
        return sex;
    }

    public Integer getAttrStatus() {
        return attrStatus;
    }

    public String getCity() {
        return city;
    }

    public String getNickName() {
        return nickName;
    }

    public Integer getScene() {
        return scene;
    }

    public String getProvince() {
        return province;
    }

    public String getContent() {
        return content;
    }

    public String getAlias() {
        return alias;
    }

    public String getSignature() {
        return signature;
    }

    public Integer getOpCode() {
        return opCode;
    }

    public Long getQQNum() {
        return qqNum;
    }

    public Integer getVerifyFlag() {
        return verifyFlag;
    }

    public static RecommendInfo valueOf(JSONObject info) {
        if (info == null) {
            return null;
        }

        RecommendInfo recommendInfo = new RecommendInfo();

        recommendInfo.ticket = info.getString("Ticket");
        recommendInfo.userName = info.getString("UserName");
        recommendInfo.sex = info.getInteger("Sex");
        recommendInfo.attrStatus = info.getInteger("AttrStatus");
        recommendInfo.city = info.getString("City");
        recommendInfo.nickName = info.getString("NickName");
        recommendInfo.scene = info.getInteger("Scene");
        recommendInfo.province = info.getString("Province");
        recommendInfo.content = info.getString("Content");
        recommendInfo.alias = info.getString("Alias");
        recommendInfo.signature = info.getString("Signature");
        recommendInfo.opCode = info.getInteger("OpCode");
        recommendInfo.qqNum = info.getLong("QQNum");
        recommendInfo.verifyFlag = info.getInteger("VerifyFlag");

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
