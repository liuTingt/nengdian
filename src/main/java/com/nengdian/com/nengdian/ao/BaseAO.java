package com.nengdian.com.nengdian.ao;

import javax.validation.constraints.NotEmpty;

public class BaseAO {
    @NotEmpty
    private String devId;

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }
}
