package com.hotlcc.wechat4j.enums;

/**
 * 微信退出类型
 *
 * @author Allen
 */
public enum ExitTypeEnum {
    ERROR_EXIT("错误退出"),
    LOCAL_EXIT("本地退出"),
    REMOTE_EXIT("远程退出");

    private String desc;

    ExitTypeEnum(String desc) {
        this.desc = desc;
    }

}
