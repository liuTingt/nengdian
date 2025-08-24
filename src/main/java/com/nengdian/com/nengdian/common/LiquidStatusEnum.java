package com.nengdian.com.nengdian.common;

public enum LiquidStatusEnum {
    Normal(0, "正常"),
    Low(1, "低液位"),
    Height(2, "高液位"),
    ;

    private Integer code;
    private String desc;

    LiquidStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }
}
