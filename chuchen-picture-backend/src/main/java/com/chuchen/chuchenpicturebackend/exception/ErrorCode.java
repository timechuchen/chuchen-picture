package com.chuchen.chuchenpicturebackend.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    /**
     * 这里是错误码，这有个小技巧：这里尽量和 HTTP 的错误吗保持一致，比如 未登录的 HTTP 状态码是 401，
     * 那么这里就写 40100 ，后面加 00 是为了区分不同的错误，比如 40101 表示无权限等
     */
    SUCCESS(0, "ok"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NOT_LOGIN_ERROR(40100, "未登录"),
    NO_AUTH_ERROR(40101, "无权限"),
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    FORBIDDEN_ERROR(40300, "禁止访问"),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
