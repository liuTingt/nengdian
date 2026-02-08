package com.nengdian.com.nengdian.entity;


import com.alibaba.fastjson.annotation.JSONField;
import com.nengdian.com.nengdian.common.LiquidStatusEnum;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_record")
public class DeviceRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 设备ID
     */
    private String devId;

    /**
     * 液位高度 单位：米
     */
    private Integer liquidHeight;

    /**
     * 液位百分比
     */
    private Integer liquidPercent;

    /**
     * 液位状态
//     * 1：低液位 2：正常 3：高液位
     * 0 正常， 1，低液位报警， 2，高液位报警，3 离线
     */
    private Integer liquidStatus;

    /**
     * 设备类型
     */
    private Integer type;

    /**
     * 太阳能款 太阳能电量
     */
    private Double powerLevel;

    /**
     * 太阳能款 插电时长 单位：分钟
     */
    private Integer start;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public boolean isOffline() {
        LocalDateTime offlineTime = this.createTime.plusMinutes(9);
        return LocalDateTime.now().isAfter(offlineTime);
    }

    public String getStatus() {
        return LiquidStatusEnum.getStatusDesc(this.getLiquidStatus());
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

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }
}
