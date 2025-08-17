package com.nengdian.com.nengdian.common;

public enum DeviceTypeEnum {
    SOLAR_ENERGY(1, "太阳能款"),
    ELECTRIC(2, "插电款"),
    ;

    private int type;

    private String desc;

    DeviceTypeEnum(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
