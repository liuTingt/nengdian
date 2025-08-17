package com.nengdian.com.nengdian.vo;


public class DeviceLiquidLevelVO {
    private String devId;
      /**
     * 设备名称
     */
    private String devName;

    /**
     * 液位高度
     */
    private Integer liquidHeight;

    /**
     * 液位百分比
     */
    private Integer liquidPercent;

    /**
     * 液位状态
     * 1：低液位 2：正常 3：高液位
     */
    private Integer liquidStatus;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public Integer getLiquidHeight() {
        return liquidHeight;
    }

    public void setLiquidHeight(Integer liquidHeight) {
        this.liquidHeight = liquidHeight;
    }

    public Integer getLiquidPercent() {
        return liquidPercent;
    }

    public void setLiquidPercent(Integer liquidPercent) {
        this.liquidPercent = liquidPercent;
    }

    public Integer getLiquidStatus() {
        return liquidStatus;
    }

    public void setLiquidStatus(Integer liquidStatus) {
        this.liquidStatus = liquidStatus;
    }
}
