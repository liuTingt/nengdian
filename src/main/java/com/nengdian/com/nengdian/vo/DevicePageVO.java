package com.nengdian.com.nengdian.vo;

import com.nengdian.com.nengdian.entity.Device;

import java.util.List;


public class DevicePageVO {
    private long totalCount;

    private List<Device> list;

    public DevicePageVO(long totalCount, List<Device> list) {
        this.totalCount = totalCount;
        this.list = list;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public List<Device> getList() {
        return list;
    }

    public void setList(List<Device> list) {
        this.list = list;
    }
}
