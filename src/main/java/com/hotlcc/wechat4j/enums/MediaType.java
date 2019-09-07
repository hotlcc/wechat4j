package com.hotlcc.wechat4j.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Allen
 * @version 1.0
 * @date 2019/4/17 9:51
 */
@Getter
@AllArgsConstructor
public enum MediaType {
    /** pic */
    PICTURE(4, "pic"),
    /** video */
    VIDEO(4, "video");

    public static final String REQUEST_KEY = "mediatype";
    public static final String REQUEST_JSON_KEY = "MediaType";

    private Integer code;
    private String value;
}
