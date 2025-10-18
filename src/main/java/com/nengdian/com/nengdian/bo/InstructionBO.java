package com.nengdian.com.nengdian.bo;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 下发指令
 */
public class InstructionBO {
    /**
     * 上限
     * 单位：百分比值，10%，穿10
     */
    @JSONField(name = "SX")
    private double SX;

    /**
     * 下限
     * 单位：百分比值，10%，穿10
     */
    @JSONField(name = "XX")
    private double XX;
    /**
     * 睡眠开关
     */
    @JSONField(name = "SM")
    private boolean SM;
    /**
     * 安装高度
     */
    @JSONField(name = "I")
    private double I;
    /**
     * 液位最大深度
     */
    @JSONField(name = "F")
    private double F;
    /**
     * 开关状态
     * -1 无动作
     * 0 关
     * 1 开
     */
    @JSONField(name = "Pump")
    private int Pump;
    /**
     * 水箱模式
     * 1:水平圆柱形 2:垂直圆柱形 3:矩形    4:水平椭圆形
     * 5:垂直椭圆形  6: 水平胶囊形 7:垂直胶囊形 8:椭圆形 9:水平蝶形
     */
    @JSONField(name = "Size")
    private int Size;
    /**
     * 长度
     * 没有长度则赋值-1
     */
    @JSONField(name = "L")
    private double L = -1;
    /**
     * 宽度
     * 没有宽度则赋值-1
     */
    @JSONField(name = "W")
    private double W = -1;

    public double getSX() {
        return SX;
    }

    public void setSX(double SX) {
        this.SX = SX;
    }

    public double getXX() {
        return XX;
    }

    public void setXX(double XX) {
        this.XX = XX;
    }

    public boolean isSM() {
        return SM;
    }

    public void setSM(boolean SM) {
        this.SM = SM;
    }

    public double getI() {
        return I;
    }

    public void setI(double i) {
        I = i;
    }

    public double getF() {
        return F;
    }

    public void setF(double f) {
        F = f;
    }

    public int getPump() {
        return Pump;
    }

    public void setPump(int pump) {
        Pump = pump;
    }

    public int getSize() {
        return Size;
    }

    public void setSize(int size) {
        Size = size;
    }

    public double getL() {
        return L;
    }

    public void setL(double l) {
        L = l;
    }

    public double getW() {
        return W;
    }

    public void setW(double w) {
        W = w;
    }
}
