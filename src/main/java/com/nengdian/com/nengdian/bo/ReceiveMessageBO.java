package com.nengdian.com.nengdian.bo;

import com.alibaba.fastjson.annotation.JSONField;

public class ReceiveMessageBO {
    private String URL;
    @JSONField(name = "MsgId")
    private String msgId;
    private String ToUserName;

    /**
     * 用户openid
     */
    private String FromUserName;
    private Long CreateTime;
    private String MsgType;
    private String Content;
    private String Event;
    private Integer Latitude;

    private Integer Longitude;

    private Integer Precision;

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public Long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Long createTime) {
        CreateTime = createTime;
    }

    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    public String getEvent() {
        return Event;
    }

    public void setEvent(String event) {
        Event = event;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Integer getLatitude() {
        return Latitude;
    }

    public void setLatitude(Integer latitude) {
        Latitude = latitude;
    }

    public Integer getLongitude() {
        return Longitude;
    }

    public void setLongitude(Integer longitude) {
        Longitude = longitude;
    }

    public Integer getPrecision() {
        return Precision;
    }

    public void setPrecision(Integer precision) {
        Precision = precision;
    }
}
