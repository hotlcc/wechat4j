package com.hotlcc.wechat4j.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用工具类
 *
 * @author Allen
 */
public final class CommonUtil {
    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    private CommonUtil() {
    }

    /**
     * 睡眠线程
     *
     * @param millis 时间
     * @param nanos  nanos
     */
    public static void threadSleep(long millis, int nanos) {
        try {
            Thread.sleep(millis, nanos);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 睡眠线程
     *
     * @param millis 时间
     */
    public static void threadSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
