package com.nengdian.com.nengdian.common;

public enum ResultCodeEnum {
    PARAM_ERROR(6001, "参数错误"),
    NOT_FIND_DEVICE(6002, "用户下没有找到设备"),
    NOT_FIND_DEVICE_RECORD(6003, "未找到设备采集记录"),
    NOT_FIND_USER(6004, "未找到用户"),
    FIND_RECORD_ERROR(6005, "查询历史水位记录失败"),
    SAVE_DEVICE_ERROR(6006, "保存设备失败"),
    DEVICE_HAS_EXIST(6007, "设备已存在"),
    UPDATE_USER_ERROR(6008, "更新用户失败"),
    NOT_FIND_USER_SERVICE_OPEN_ID(6009, "用户服务号不存在"),

    ACCESS_TOKEN_ERROR(7001, "获取到微信凭证失败"),
    SEND_MESSAGE_ERROR(7002, "发送消息失败"),
    QUERY_WECHAT_USER_ERROR(7003, "查询微信用户信息失败"),

    ;

    private int code;
    private String msg;

    ResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
