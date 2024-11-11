package com.entnews.common.msg;

import com.entnews.common.eums.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;
    private T data;


    public Result(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static <T> Result<T> ok() {
        return new Result<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg());
    }

    public static <T> Result<T> ok(T data) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<T>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> Result<T> fail(String msg) {
        return new Result<T>(ResultCode.FAIL.getCode(), msg);
    }

    public static <T> Result<T> fail(int code, String msg) {
        return new Result<T>(code, msg);
    }

    public static <T> Result<T> fail(ResultCode resultCode) {
        return new Result<T>(resultCode.getCode(), resultCode.getMsg());
    }

}
