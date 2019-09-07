package com.hotlcc.wechat4j.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 等待确认登录的tip
 *
 * @author Allen
 */
@Getter
@AllArgsConstructor
public enum LoginTip {
    /** 扫码登录 */
    TIP_0(0),
    /** 确认登录 */
    TIP_1(1);

    private int code;
}
