package com.hotlcc.wechat4j.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.hotlcc.wechat4j.util.WechatUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * 基本请求模型
 *
 * @author Allen
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequest implements Serializable {
    private static final long serialVersionUID = 9141083671818346541L;

    public BaseRequest(String Sid, String Skey, String Uin) {
        this(WechatUtil.createDeviceID(), Sid, Skey, Uin);
    }

    @JSONField(name = "DeviceID")
    private String deviceID;
    @JSONField(name = "Sid")
    private String sid;
    @JSONField(name = "Skey")
    private String skey;
    @JSONField(name = "Uin")
    private String uin;
}
