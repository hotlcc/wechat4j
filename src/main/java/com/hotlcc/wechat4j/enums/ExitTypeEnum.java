package com.hotlcc.wechat4j.enums;

/**
 * 微信退出类型
 *
 * @author Allen
 */
public enum ExitTypeEnum {
    /**
     * 错误导致退出
     */
    ERROR_EXIT,
    /**
     * 本次手动退出
     */
    LOCAL_EXIT,
    /**
     * 远程操作退出
     */
    REMOTE_EXIT;

    ExitTypeEnum() {
    }

}
