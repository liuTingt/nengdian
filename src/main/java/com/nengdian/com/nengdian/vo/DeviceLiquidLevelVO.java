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
     * 0 正常， 1，低液位报警， 2，高液位报警
     */
    private Integer liquidStatus;
    /**
     * 设备液位状态
     */
    private String status;
    /**
     * 设备类型
     */
    private Integer type;
    /**
     * 太阳能款 电量
     */
    private Double powerLevel;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getPowerLevel() {
        return powerLevel;
    }

    public void setPowerLevel(Double powerLevel) {
        this.powerLevel = powerLevel;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
