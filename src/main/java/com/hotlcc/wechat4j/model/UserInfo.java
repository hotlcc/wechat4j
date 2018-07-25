package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信用户信息
 */
public final class UserInfo {
    private UserInfo() {
    }

    private Long Uin;
    private String NickName;
    private String HeadImgUrl;
    private Integer ContactFlag;
    private Integer MemberCount;
    private List<UserInfo> MemberList;
    private String RemarkName;
    private Integer HideInputBarFlag;
    private Integer Sex;
    private String Signature;
    private Integer VerifyFlag;
    private Long OwnerUin;
    private String PYInitial;
    private String PYQuanPin;
    private String RemarkPYInitial;
    private String RemarkPYQuanPin;
    private Integer StarFriend;
    private Integer AppAccountFlag;
    private Integer Statues;
    private Integer AttrStatus;
    private String Province;
    private String City;
    private String Alias;
    private Integer SnsFlag;
    private Integer UniFriend;
    private String DisplayName;
    private Long ChatRoomId;
    private String KeyWord;
    private String EncryChatRoomId;
    private Integer IsOwner;
    private String UserName;

    public Long getUin() {
        return Uin;
    }

    public String getNickName() {
        return NickName;
    }

    public String getHeadImgUrl() {
        return HeadImgUrl;
    }

    public Integer getContactFlag() {
        return ContactFlag;
    }

    public Integer getMemberCount() {
        return MemberCount;
    }

    public List<UserInfo> getMemberList() {
        return MemberList;
    }

    public String getRemarkName() {
        return RemarkName;
    }

    public Integer getHideInputBarFlag() {
        return HideInputBarFlag;
    }

    public Integer getSex() {
        return Sex;
    }

    public String getSignature() {
        return Signature;
    }

    public Integer getVerifyFlag() {
        return VerifyFlag;
    }

    public Long getOwnerUin() {
        return OwnerUin;
    }

    public String getPYInitial() {
        return PYInitial;
    }

    public String getPYQuanPin() {
        return PYQuanPin;
    }

    public String getRemarkPYInitial() {
        return RemarkPYInitial;
    }

    public String getRemarkPYQuanPin() {
        return RemarkPYQuanPin;
    }

    public Integer getStarFriend() {
        return StarFriend;
    }

    public Integer getAppAccountFlag() {
        return AppAccountFlag;
    }

    public Integer getStatues() {
        return Statues;
    }

    public Integer getAttrStatus() {
        return AttrStatus;
    }

    public String getProvince() {
        return Province;
    }

    public String getCity() {
        return City;
    }

    public String getAlias() {
        return Alias;
    }

    public Integer getSnsFlag() {
        return SnsFlag;
    }

    public Integer getUniFriend() {
        return UniFriend;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public Long getChatRoomId() {
        return ChatRoomId;
    }

    public String getKeyWord() {
        return KeyWord;
    }

    public String getEncryChatRoomId() {
        return EncryChatRoomId;
    }

    public Integer getIsOwner() {
        return IsOwner;
    }

    public String getUserName() {
        return UserName;
    }

    public static UserInfo valueOf(JSONObject info) {
        if (info == null) {
            return null;
        }

        UserInfo userInfo = new UserInfo();

        userInfo.Uin = info.getLong("Uin");
        userInfo.NickName = info.getString("NickName");
        userInfo.HeadImgUrl = info.getString("HeadImgUrl");
        userInfo.ContactFlag = info.getInteger("ContactFlag");
        userInfo.MemberCount = info.getInteger("MemberCount");
        userInfo.MemberList = valueOf(info.getJSONArray("MemberList"));
        userInfo.RemarkName = info.getString("RemarkName");
        userInfo.HideInputBarFlag = info.getInteger("HideInputBarFlag");
        userInfo.Sex = info.getInteger("Sex");
        userInfo.Signature = info.getString("Signature");
        userInfo.VerifyFlag = info.getInteger("VerifyFlag");
        userInfo.OwnerUin = info.getLong("OwnerUin");
        userInfo.PYInitial = info.getString("PYInitial");
        userInfo.PYQuanPin = info.getString("PYQuanPin");
        userInfo.RemarkPYInitial = info.getString("RemarkPYInitial");
        userInfo.RemarkPYQuanPin = info.getString("RemarkPYQuanPin");
        userInfo.StarFriend = info.getInteger("StarFriend");
        userInfo.AppAccountFlag = info.getInteger("AppAccountFlag");
        userInfo.Statues = info.getInteger("Statues");
        userInfo.AttrStatus = info.getInteger("AttrStatus");
        userInfo.Province = info.getString("Province");
        userInfo.City = info.getString("City");
        userInfo.Alias = info.getString("Alias");
        userInfo.SnsFlag = info.getInteger("SnsFlag");
        userInfo.UniFriend = info.getInteger("UniFriend");
        userInfo.DisplayName = info.getString("DisplayName");
        userInfo.ChatRoomId = info.getLong("ChatRoomId");
        userInfo.KeyWord = info.getString("KeyWord");
        userInfo.EncryChatRoomId = info.getString("EncryChatRoomId");
        userInfo.IsOwner = info.getInteger("IsOwner");
        userInfo.UserName = info.getString("UserName");

        return userInfo;
    }

    public static List<UserInfo> valueOf(JSONArray infos) {
        if (infos == null) {
            return null;
        }

        List<UserInfo> userList = new ArrayList<>();
        for (int i = 0, len = infos.size(); i < len; i++) {
            JSONObject info = infos.getJSONObject(i);
            userList.add(UserInfo.valueOf(info));
        }
        return userList;
    }
}
