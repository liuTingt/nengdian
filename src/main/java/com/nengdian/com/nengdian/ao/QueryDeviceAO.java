package com.nengdian.com.nengdian.ao;

import com.nengdian.com.nengdian.common.DeviceTypeEnum;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class QueryDeviceAO {
    /**
     * 微信用户openid
     */
    @NotEmpty
    private String openid;
    /**
     * 设备液位状态
     */
    @NotEmpty
    private List<Integer> statusList;

    private String devId;

    private String devName;
    /**
     * 设备类型
     * @see DeviceTypeEnum
     */
    private Integer type;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public List<Integer> getStatusList() {
        return statusList;
    }

    public void setStatusList(List<Integer> statusList) {
        this.statusList = statusList;
    }

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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
