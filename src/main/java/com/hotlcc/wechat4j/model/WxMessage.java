package com.hotlcc.wechat4j.model;

/**
 * 要发送的消息
 */
public class WxMessage {
    private String ClientMsgId;
    private String Content;
    private String FromUserName;
    private String LocalID;
    private String ToUserName;
    private Integer type;

    public String getClientMsgId() {
        return ClientMsgId;
    }

    public void setClientMsgId(String clientMsgId) {
        ClientMsgId = clientMsgId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public String getLocalID() {
        return LocalID;
    }

    public void setLocalID(String localID) {
        LocalID = localID;
    }

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
