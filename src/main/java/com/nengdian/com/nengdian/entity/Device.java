package com.nengdian.com.nengdian.entity;


import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "device")
public class Device {

    /**
     * 设备序号,唯一标识
     */
    @Id
    @NotEmpty(message = "设备ID不能为空")
    private String devId;
    /**
     * 微信用户openid
     */
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
    private Integer installHeight = 290;
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
    private Integer upperLimit = 90;
    /**
     * 下限设置
     * 范围0～90
     */
    private Integer lowerLimit = 10;
    /**
     * 低能耗开关
     * 默认开启，用户不可设置
     */
    private boolean lowEnergySwitch = true;

    /**
     * 排水模式 插电款专用
     */
    private boolean drainageModel = true;

    /**
     * 太阳能款 检测周期
     */
    private Integer checkPeriod;

    /**
     * 删除状态 0：未删除  1：已删除
     */
    private boolean deleted;

    @CreatedDate
//    @Column(name = "create_time", nullable = false, updatable = false)
    private Date createTime;

    @LastModifiedDate
    @Column(name = "modify_time", nullable = false)
    private Date modifyTime;

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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean isDrainageModel() {
        return drainageModel;
    }

    public void setDrainageModel(boolean drainageModel) {
        this.drainageModel = drainageModel;
    }

    public Integer getCheckPeriod() {
        return checkPeriod;
    }

    public void setCheckPeriod(Integer checkPeriod) {
        this.checkPeriod = checkPeriod;
    }
}
