package com.example.ccqzy.beans;

/**
 * Created by Administrator on 2018/3/6.
 *
 * 坐标系的坐标,旋转角和缩放比例
 */

public class RotAngleCoordinate {

    //坐标
    private double x;
    private double y;
    private double z;
    //旋转角
    private double rotX;
    private double rotY;
    private double rotZ;
    //缩放比例
    private double Scale;

    public RotAngleCoordinate() {
    }

    public RotAngleCoordinate(double x, double y, double z, double rotX, double rotY, double rotZ, double scale) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        Scale = scale;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public double getRotX() {
        return rotX;
    }

    public void setRotX(double rotX) {
        this.rotX = rotX;
    }

    public double getRotY() {
        return rotY;
    }

    public void setRotY(double rotY) {
        this.rotY = rotY;
    }

    public double getRotZ() {
        return rotZ;
    }

    public void setRotZ(double rotZ) {
        this.rotZ = rotZ;
    }

    public double getScale() {
        return Scale;
    }

    public void setScale(double scale) {
        Scale = scale;
    }

    @Override
    public String toString() {
        return "RotAngleCoordinate{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", rotX=" + rotX +
                ", rotY=" + rotY +
                ", rotZ=" + rotZ +
                ", Scale=" + Scale +
                '}';
    }
}
