package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONObject;

public final class ReceivedMsg {
    public ReceivedMsg(JSONObject msg) {
        if (msg == null) {
            return;
        }
        this.SubMsgType = msg.getInteger("SubMsgType");
        this.VoiceLength = msg.getLong("VoiceLength");
        this.FileName = msg.getString("FileName");
        this.ImgHeight = msg.getLong("ImgHeight");
        this.ToUserName = msg.getString("ToUserName");
        this.HasProductId = msg.getLong("HasProductId");
        this.ImgStatus = msg.getInteger("ImgStatus");
        this.Url = msg.getString("Url");
        this.ImgWidth = msg.getInteger("ImgWidth");
        this.ForwardFlag = msg.getInteger("ForwardFlag");
        this.Status = msg.getInteger("Status");
        this.Ticket = msg.getString("Ticket");
        this.RecommendInfo = new RecommendInfo(msg.getJSONObject("RecommendInfo"));
        this.CreateTime = msg.getLong("CreateTime");
        this.NewMsgId = msg.getLong("NewMsgId");
        this.MsgType = msg.getInteger("MsgType");
        this.EncryFileName = msg.getString("EncryFileName");
        this.MsgId = msg.getString("MsgId");
        this.StatusNotifyCode = msg.getInteger("StatusNotifyCode");
        this.AppInfo = new AppInfo(msg.getJSONObject("AppInfo"));
        this.PlayLength = msg.getLong("PlayLength");
        this.MediaId = msg.getString("MediaId");
        this.Content = msg.getString("Content");
        this.StatusNotifyUserName = msg.getString("StatusNotifyUserName");
        this.FromUserName = msg.getString("FromUserName");
        this.OriContent = msg.getString("OriContent");
        this.FileSize = msg.getString("FileSize");
    }

    private Integer SubMsgType;
    private Long VoiceLength;
    private String FileName;
    private Long ImgHeight;
    private String ToUserName;
    private Long HasProductId;
    private Integer ImgStatus;
    private String Url;
    private Integer ImgWidth;
    private Integer ForwardFlag;
    private Integer Status;
    private String Ticket;
    private RecommendInfo RecommendInfo;
    private Long CreateTime;
    private Long NewMsgId;
    private Integer MsgType;
    private String EncryFileName;
    private String MsgId;
    private Integer StatusNotifyCode;
    private AppInfo AppInfo;
    private Integer AppMsgType;
    private Long PlayLength;
    private String MediaId;
    private String Content;
    private String StatusNotifyUserName;
    private String FromUserName;
    private String OriContent;
    private String FileSize;

    public Integer getSubMsgType() {
        return SubMsgType;
    }

    public Long getVoiceLength() {
        return VoiceLength;
    }

    public String getFileName() {
        return FileName;
    }

    public Long getImgHeight() {
        return ImgHeight;
    }

    public String getToUserName() {
        return ToUserName;
    }

    public Long getHasProductId() {
        return HasProductId;
    }

    public Integer getImgStatus() {
        return ImgStatus;
    }

    public String getUrl() {
        return Url;
    }

    public Integer getImgWidth() {
        return ImgWidth;
    }

    public Integer getForwardFlag() {
        return ForwardFlag;
    }

    public Integer getStatus() {
        return Status;
    }

    public String getTicket() {
        return Ticket;
    }

    public com.hotlcc.wechat4j.model.RecommendInfo getRecommendInfo() {
        return RecommendInfo;
    }

    public Long getCreateTime() {
        return CreateTime;
    }

    public Long getNewMsgId() {
        return NewMsgId;
    }

    public Integer getMsgType() {
        return MsgType;
    }

    public String getEncryFileName() {
        return EncryFileName;
    }

    public String getMsgId() {
        return MsgId;
    }

    public Integer getStatusNotifyCode() {
        return StatusNotifyCode;
    }

    public com.hotlcc.wechat4j.model.AppInfo getAppInfo() {
        return AppInfo;
    }

    public Integer getAppMsgType() {
        return AppMsgType;
    }

    public Long getPlayLength() {
        return PlayLength;
    }

    public String getMediaId() {
        return MediaId;
    }

    public String getContent() {
        return Content;
    }

    public String getStatusNotifyUserName() {
        return StatusNotifyUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public String getOriContent() {
        return OriContent;
    }

    public String getFileSize() {
        return FileSize;
    }
}
