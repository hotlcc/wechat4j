package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 要发送的消息
 */
public class WxMessage {
    @JSONField(name = "ClientMsgId")
    private String clientMsgId;
    @JSONField(name = "Content")
    private String content;
    @JSONField(name = "FromUserName")
    private String fromUserName;
    @JSONField(name = "LocalID")
    private String localID;
    @JSONField(name = "ToUserName")
    private String toUserName;
    @JSONField(name = "Type")
    private Integer type;

    public String getClientMsgId() {
        return clientMsgId;
    }

    public void setClientMsgId(String clientMsgId) {
        this.clientMsgId = clientMsgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getLocalID() {
        return localID;
    }

    public void setLocalID(String localID) {
        this.localID = localID;
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
