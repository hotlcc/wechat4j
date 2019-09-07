package com.hotlcc.wechat4j.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息类型enum
 *
 * @author Allen
 */
@SuppressWarnings({"unused"})
@AllArgsConstructor
@Getter
public enum MsgType {
    /** 文本消息 */
    TEXT_MSG(1),
    /** 图片消息 */
    IMAGE_MSG(3),
    /** 语音消息 */
    VOICE_MSG(34),
    /** 验证消息 */
    VERIFY_MSG(37),
    /** 可能的朋友的消息 */
    POSSIBLE_FRIEND_MSG(40),
    /** 共享名片 */
    SHARE_CARD_MSG(42),
    /** 视频通话消息 */
    VIDEO_CALL_MSG(43),
    /** 动画表情 */
    ANIMATED_STICKER_MSG(47),
    /** 位置消息 */
    LOCATION_MSG(48),
    /** 分享链接 */
    SHARE_LINK_MSG(49),
    /** VoIP消息 */
    VOIP_MSG(50),
    /** 初始化消息 */
    INIT_MSG(51),
    /** VoIP通知 */
    VOIP_NOTIFY_MSG(52),
    /** VoIP邀请 */
    VOIP_INVITE_MSG(53),
    /** 小视频 */
    VIDEO_MSG(62),
    /** 系统通知 */
    SYS_NOTICE_MSG(9999),
    /** 系统消息 */
    SYSTEM_MSG(10000),
    /** 撤回消息 */
    WITHDRAW_MSG(10002);

    private int code;
}
