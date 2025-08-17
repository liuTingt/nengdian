package com.nengdian.com.nengdian.vo;

public class DeviceAvgLiquidLevelVO {
    private String devId;

    /**
     * 每天液位高度
     */
    private String avgLiquid;
    /**
     * 创建时间
     */
    private String createTime;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getAvgLiquid() {
        return avgLiquid;
    }

    public void setAvgLiquid(String avgLiquid) {
        this.avgLiquid = avgLiquid;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
