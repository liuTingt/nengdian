package com.nengdian.com.nengdian.common;

public enum LiquidStatusEnum {
    Normal(0, "正常"),
    Low(1, "液位过低"),
    Height(2, "液位过高"),
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

    public String getDesc() {
        return desc;
    }

    public static String getStatusDesc(int status) {
        for (LiquidStatusEnum statusEnum: LiquidStatusEnum.values()) {
            if (statusEnum.getCode() == status) {
                return statusEnum.getDesc();
            }
        }
        return null;
    }
}
