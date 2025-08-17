package com.nengdian.com.nengdian.bo;



public class DeviceRecordBO {
    /**
     * 设备ID
     */
    private String devId;

    /**
     * 液位高度
     */
    private String liquidHeight;

//    private Date createTime;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }


    public String getLiquidHeight() {
        return liquidHeight;
    }

    public void setLiquidHeight(String liquidHeight) {
        this.liquidHeight = liquidHeight;
    }
}
