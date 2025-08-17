package com.nengdian.com.nengdian.common;

public enum LiquidStatusEnum {
    Low(1, "低液位"),
    Normal(2, "正常"),
    Height(3, "高液位"),
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
