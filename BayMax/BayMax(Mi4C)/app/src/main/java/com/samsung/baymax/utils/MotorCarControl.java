package com.samsung.baymax.utils;

import com.samsung.baymax.motorcar.Camera;

public class MotorCarControl {

    public double angle;  //角度
    public double range;  //幅度
    public double x;
    public double y;
    public double z;
    public double h;

    private static MotorCarControl motorCarControl = null;

    public static MotorCarControl getInstance() {
        // 单例模式
        if (motorCarControl == null) {
            motorCarControl = new MotorCarControl();
        }
        return motorCarControl;
    }
}
