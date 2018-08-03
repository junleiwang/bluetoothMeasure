package com.example.ccqzy.beans;

/**
 * Created by Administrator on 2018/2/6.
 * 测量点的坐标
 */

public class DoublePoint {
    private double pointX;
    private double pointY;
    private double pointH;

    public DoublePoint() {
    }

    public DoublePoint(double pointX, double pointY, double pointH) {
        this.pointX = pointX;
        this.pointY = pointY;
        this.pointH = pointH;
    }

    public double getPointX() {
        return pointX;
    }

    public void setPointX(double pointX) {
        this.pointX = pointX;
    }

    public double getPointY() {
        return pointY;
    }

    public void setPointY(double pointY) {
        this.pointY = pointY;
    }

    public double getPointH() {
        return pointH;
    }

    public void setPointH(double pointH) {
        this.pointH = pointH;
    }


    @Override
    public String toString() {
        return "{" +
                "," + '\"' + "pointX" + '\"' + ":" + '\"' + pointX + '\"' +
                "," + '\"' + "pointY" + '\"' + ":" + '\"' + pointY + '\"' +
                "," + '\"' + "pointH" + '\"' + ":" + '\"' + pointH + '\"' +
                '}';
    }


}
