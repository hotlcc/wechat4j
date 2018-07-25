package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class ReceivedMsg {
    private ReceivedMsg() {
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

    public AppInfo getAppInfo() {
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

    public static ReceivedMsg valueOf(JSONObject msg) {
        if (msg == null) {
            return null;
        }

        ReceivedMsg receivedMsg = new ReceivedMsg();

        receivedMsg.SubMsgType = msg.getInteger("SubMsgType");
        receivedMsg.VoiceLength = msg.getLong("VoiceLength");
        receivedMsg.FileName = msg.getString("FileName");
        receivedMsg.ImgHeight = msg.getLong("ImgHeight");
        receivedMsg.ToUserName = msg.getString("ToUserName");
        receivedMsg.HasProductId = msg.getLong("HasProductId");
        receivedMsg.ImgStatus = msg.getInteger("ImgStatus");
        receivedMsg.Url = msg.getString("Url");
        receivedMsg.ImgWidth = msg.getInteger("ImgWidth");
        receivedMsg.ForwardFlag = msg.getInteger("ForwardFlag");
        receivedMsg.Status = msg.getInteger("Status");
        receivedMsg.Ticket = msg.getString("Ticket");
        receivedMsg.RecommendInfo = com.hotlcc.wechat4j.model.RecommendInfo.valueOf(msg.getJSONObject("RecommendInfo"));
        receivedMsg.CreateTime = msg.getLong("CreateTime");
        receivedMsg.NewMsgId = msg.getLong("NewMsgId");
        receivedMsg.MsgType = msg.getInteger("MsgType");
        receivedMsg.EncryFileName = msg.getString("EncryFileName");
        receivedMsg.MsgId = msg.getString("MsgId");
        receivedMsg.StatusNotifyCode = msg.getInteger("StatusNotifyCode");
        receivedMsg.AppInfo = com.hotlcc.wechat4j.model.AppInfo.valueOf(msg.getJSONObject("AppInfo"));
        receivedMsg.PlayLength = msg.getLong("PlayLength");
        receivedMsg.MediaId = msg.getString("MediaId");
        receivedMsg.Content = msg.getString("Content");
        receivedMsg.StatusNotifyUserName = msg.getString("StatusNotifyUserName");
        receivedMsg.FromUserName = msg.getString("FromUserName");
        receivedMsg.OriContent = msg.getString("OriContent");
        receivedMsg.FileSize = msg.getString("FileSize");

        return receivedMsg;
    }

    public static List<ReceivedMsg> valueOf(JSONArray msgs) {
        if (msgs == null) {
            return null;
        }

        List<ReceivedMsg> receivedMsgList = new ArrayList<>();
        for (int i = 0, len = msgs.size(); i < len; i++) {
            JSONObject info = msgs.getJSONObject(i);
            receivedMsgList.add(ReceivedMsg.valueOf(info));
        }
        return receivedMsgList;
    }
}
