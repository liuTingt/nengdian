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
    private Double water;
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

    @JSONField(name = "NET")
    private String NET;

    @JSONField(name = "F")
    private Double F;

    @JSONField(name = "I")
    private Double I;

    /**
     * 太阳能款 太阳能电量
     */
    @JSONField(name = "B")
    private Double B;

    /**
     * 太阳能款 检测周期
     */
    @JSONField(name = "U")
    private Integer U;

    /**
     * 太阳能款 用户修改设备信息是否成功标识
     */
    @JSONField(name = "cfg_ack")
    private Integer cfg_ack;

    /**
     * 太阳能款 插电时长 单位：分钟
     */
    @JSONField(name = "start")
    private Integer start;

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public Double getWater() {
        return water;
    }

    public void setWater(Double water) {
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

    public String getNET() {
        return NET;
    }

    public void setNET(String NET) {
        this.NET = NET;
    }

    public Double getF() {
        return F;
    }

    public void setF(Double f) {
        F = f;
    }

    public Double getI() {
        return I;
    }

    public void setI(Double i) {
        I = i;
    }

    public Double getB() {
        return B;
    }

    public void setB(Double b) {
        B = b;
    }

    public Integer getU() {
        return U;
    }

    public void setU(Integer u) {
        U = u;
    }

    public Integer getCfg_ack() {
        return cfg_ack;
    }

    public void setCfg_ack(Integer cfg_ack) {
        this.cfg_ack = cfg_ack;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }
}
