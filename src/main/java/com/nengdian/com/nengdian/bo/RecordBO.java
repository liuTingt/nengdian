package com.nengdian.com.nengdian.bo;

import com.alibaba.fastjson.annotation.JSONField;

public class RecordBO {
    /**
     * 上报的液位高度
     */
    @JSONField(name = "X")
    private double X;
    /**
     * 上报的液位百分比
     */
    @JSONField(name = "water")
    private double water;
    /**
     * 液位状态 1 ~ 3 ，
     * 0 正常， 1，低液位报警， 2，高液位报警
     */
    @JSONField(name = "WS")
    private int WS;

    /**
     * 高液位报警设置参数
     */
    @JSONField(name = "SX")
    private int SX;

    /**
     * 高液位报警设置参数
     */
    @JSONField(name = "XX")
    private int XX;

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getWater() {
        return water;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public int getWS() {
        return WS;
    }

    public void setWS(int WS) {
        this.WS = WS;
    }

    public int getSX() {
        return SX;
    }

    public void setSX(int SX) {
        this.SX = SX;
    }

    public int getXX() {
        return XX;
    }

    public void setXX(int XX) {
        this.XX = XX;
    }
}
