package com.hotlcc.wechat4j.enums;

/**
 * 等待确认登录的tip
 *
 * @author Allen
 */
public enum LoginTipEnum {
    /**
     * 扫码登录
     */
    TIP_0(0),
    /**
     * 确认登录
     */
    TIP_1(1);

    private int code;

    LoginTipEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code + "";
    }
}
