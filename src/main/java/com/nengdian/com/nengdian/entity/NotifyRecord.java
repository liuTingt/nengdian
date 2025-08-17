package com.nengdian.com.nengdian.entity;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notify_record")
public class NotifyRecord {
    @Id
    private long id;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 设备id
     */
    private String devId;
    /**
     * 通知时间
     */
    @CreatedDate
    @Column(name = "notify_time", nullable = false, updatable = false)
    private LocalDateTime notifyTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public LocalDateTime getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(LocalDateTime notifyTime) {
        this.notifyTime = notifyTime;
    }
}
