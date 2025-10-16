package com.nengdian.com.nengdian.ao;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SettingAO {

    /**
     * 设备序号,唯一标识
     */
    @Id
    @NotEmpty(message = "设备ID不能为空")
    private String devId;
    /**
     * 微信用户openid
     */
    @NotEmpty(message = "用户ID不能为空")
    private String openid;
    /**
     * 设备名称
     */
    private String devName;
    /**
     * 1：太阳能款
     * 2:：插电款
     */
    @NotNull(message = "设备类型不能为空")
    private Integer type;
    /**
     * 安装高度
     * 单位：厘米
     * 范围0.1~2.9米之间
     */
    private Integer installHeight = 10;
    /**
     * 传感器满液位距离
     * 单位：厘米
     * 范围最低0.1米
     */
    private Integer distance = 10;
    /**
     * 上限设置
     * 单位：百分比值，10%，存储10
     * 范围10～100
     */
    private Integer upperLimit = 10;
    /**
     * 下限设置
     * 范围0～90
     */
    private Integer lowerLimit = 0;
    /**
     * 低能耗开关
     * 默认开启，用户不可设置
     */
    private boolean lowEnergySwitch = false;

    /**
     * 排水模式
     */
    private boolean drainageModel;

    /**
     * 语言
     */
    private String language;
    /**
     * 公众号提醒开关
     * 0:关   1:开
     */
    private boolean remindSwitch;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
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

    public Integer getInstallHeight() {
        return installHeight;
    }

    public void setInstallHeight(Integer installHeight) {
        this.installHeight = installHeight;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public Integer getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(Integer upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public boolean isLowEnergySwitch() {
        return lowEnergySwitch;
    }

    public void setLowEnergySwitch(boolean lowEnergySwitch) {
        this.lowEnergySwitch = lowEnergySwitch;
    }

    public boolean isDrainageModel() {
        return drainageModel;
    }

    public void setDrainageModel(boolean drainageModel) {
        this.drainageModel = drainageModel;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isRemindSwitch() {
        return remindSwitch;
    }

    public void setRemindSwitch(boolean remindSwitch) {
        this.remindSwitch = remindSwitch;
    }
}
