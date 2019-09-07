package com.hotlcc.wechat4j.enums;

import lombok.AllArgsConstructor;

/**
 * Selector代码
 *
 * @author Allen
 */
@AllArgsConstructor
public enum Selector {
    /** 正常 */
    SELECTOR_0(0),
    /** 有新消息 */
    SELECTOR_2(2),
    /** 目前发现修改了联系人备注会出现 */
    SELECTOR_4(4),
    /** 目前不知道代表什么 */
    SELECTOR_6(6),
    /** 手机操作了微信 */
    SELECTOR_7(7);

    private int code;

    public static Selector valueOf(int code) {
        Selector[] es = values();
        for (Selector e : es) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}
