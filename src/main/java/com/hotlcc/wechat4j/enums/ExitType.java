package com.hotlcc.wechat4j.enums;

import lombok.AllArgsConstructor;

/**
 * 微信退出类型
 *
 * @author Allen
 */
@AllArgsConstructor
public enum ExitType {
    /** 错误退出 */
    ERROR_EXIT,
    /** 本地退出 */
    LOCAL_EXIT,
    /** 远程退出 */
    REMOTE_EXIT
}
