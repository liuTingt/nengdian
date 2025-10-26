package com.nengdian.com.nengdian.entity;


import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

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
     * 0 正常， 1，低液位报警， 2，高液位报警
     */
    private Integer liquidStatus;
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
}
