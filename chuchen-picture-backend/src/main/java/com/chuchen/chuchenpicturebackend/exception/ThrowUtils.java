package com.chuchen.chuchenpicturebackend.exception;

/**
 * @author chuchen
 * @date 2025/5/29 16:44
 * @description 异常处理工具类
 */
public class ThrowUtils {

    /**
     * 抛出异常
     * @param condition 异常条件
     * @param e 异常
     */
    public static void throwIf(boolean condition, RuntimeException e) {
        if (condition) {
            throw e;
        }
    }

    /**
     * 抛出异常
     * @param condition 异常条件
     * @param errorCode 异常
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    /**
     *
     * @param condition 异常条件
     * @param errorCode 异常
     * @param message 异常消息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
