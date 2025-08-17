package com.nengdian.com.nengdian.vo;

import com.nengdian.com.nengdian.entity.Device;

public class DeviceDetailVO extends Device {

    /**
     * 液位百分比
     */
    private Integer liquidPercent;


    public Integer getLiquidPercent() {
        return liquidPercent;
    }

    public void setLiquidPercent(Integer liquidPercent) {
        this.liquidPercent = liquidPercent;
    }

    public DeviceDetailVO() {
    }

    public DeviceDetailVO(Device device) {
        this.setDevId(device.getDevId());
        this.setOpenid(device.getOpenid());
        this.setDevName(device.getDevName());
        this.setType(device.getType());
        this.setInstallHeight(device.getInstallHeight());
        this.setDistance(device.getDistance());
        this.setUpperLimit(device.getUpperLimit());
        this.setLowerLimit(device.getLowerLimit());
        this.setLowEnergySwitch(device.isLowEnergySwitch());
        this.setDeleted(device.isDeleted());
        this.setCreateTime(device.getCreateTime());
        this.setModifyTime(device.getModifyTime());
        this.setDrainageModel(device.isDrainageModel());
    }

}
