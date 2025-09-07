package com.nengdian.com.nengdian.vo;

public class DevCountVO {
    private int onlineCount;

    private int alarmCount;

    private String language;

    public DevCountVO() {
    }

    public DevCountVO(int onlineCount, int alarmCount) {
        this.onlineCount = onlineCount;
        this.alarmCount = alarmCount;
    }

    public DevCountVO(int onlineCount, int alarmCount, String language) {
        this.onlineCount = onlineCount;
        this.alarmCount = alarmCount;
        this.language = language;
    }

    public int getOnlineCount() {
        return onlineCount;
    }

    public void setOnlineCount(int onlineCount) {
        this.onlineCount = onlineCount;
    }

    public int getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
