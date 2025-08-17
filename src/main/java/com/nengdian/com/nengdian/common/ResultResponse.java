package com.nengdian.com.nengdian.common;


public class ResultResponse<T> {
    private static final int FAILED_STATUS = 601;

    private static final int SUCCESS_STATUS = 200;
    private static final String SUCCESS_MSG = "服务调用成功";
    private static final String FAILED_MSG = "服务调用失败";

    private int status;
    private String msg;
    private T data;

    public ResultResponse() {
    }

    public static <T> ResultResponse<T> of(int status, String msg, T data) {
        ResultResponse<T> response = new ResultResponse();
        response.setStatus(status);
        response.setMsg(msg);
        response.setData(data);
        return response;
    }

    public static <T> ResultResponse<T> failed(String msg) {
        return of(FAILED_STATUS, msg, null);
    }

    public static <T> ResultResponse<T> failed(int code, String msg) {
        return of(code, msg, null);
    }

    public static <T> ResultResponse<T> failed() {
        return of(FAILED_STATUS, FAILED_MSG, null);
    }

    public static <T> ResultResponse<T> failed(BizException exception) {
        return of(exception.getCode(), exception.getMsg(), null);
    }

    public static <T> ResultResponse<T> failed(ResultCodeEnum result) {
        return of(result.getCode(), result.getMsg(), null);
    }

    public static <T> ResultResponse<T> success(String msg, T data) {
        return of(SUCCESS_STATUS, msg, data);
    }

    public static <T> ResultResponse<T> success(T data) {
        return of(SUCCESS_STATUS, SUCCESS_MSG, data);
    }

    public static <T> ResultResponse<T> success() {
        return of(SUCCESS_STATUS, SUCCESS_MSG, null);
    }

    public boolean isSuccess() {
        return SUCCESS_STATUS == this.status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
