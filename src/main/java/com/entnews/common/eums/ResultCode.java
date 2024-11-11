package com.entnews.common.eums;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "成功"),
    FAIL(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    VALIDATION_FAILED(422, "数据校验失败"),
    UNKNOWN_ERROR(500, "未知错误");

    private final int code;
    private final String msg;

    ResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsg(int code) {
        for (ResultCode item : ResultCode.values()) {
            if (item.getCode() == code) {
                return item.getMsg();
            }
        }
        return null;
    }
}
