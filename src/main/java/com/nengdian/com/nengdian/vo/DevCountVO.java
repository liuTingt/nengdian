package com.nengdian.com.nengdian.vo;

public class DevCountVO {
    private int onlineCount;

    private int alarmCount;

    public DevCountVO() {
    }

    public DevCountVO(int onlineCount, int alarmCount) {
        this.onlineCount = onlineCount;
        this.alarmCount = alarmCount;
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
}
