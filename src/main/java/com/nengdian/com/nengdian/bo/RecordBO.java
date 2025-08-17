package com.nengdian.com.nengdian.bo;

public class RecordBO {
    /**
     * 上报的液位高度
     */
    private double X;
    /**
     * 上报的液位百分比
     */
    private double pt;
    /**
     * 液位状态 1 ~ 3 ，
     * 1:低液位 2:正常 3:高液位
     */
    private int Status;

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getPt() {
        return pt;
    }

    public void setPt(double pt) {
        this.pt = pt;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }
}
