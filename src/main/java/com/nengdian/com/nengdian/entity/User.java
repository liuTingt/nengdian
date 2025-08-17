package com.nengdian.com.nengdian.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user")
public class User {
    @Id
    private String openid;
    private String userName;
    /**
     * 语言
     */
    private String language;
    /**
     * 公众号提醒开关
     * 0:关   1:开
     */
    private boolean remindSwitch;

    /**
     * 蜂鸣器鸣叫
     * 0:关   1:开
     */
//    private String beepSwitch;

    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false)
    private Date createTime;

    @LastModifiedDate
    @Column(name = "modify_time", nullable = false)
    private Date modifyTime;


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
