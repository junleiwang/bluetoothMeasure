package com.example.ccqzy.beans;

/**
 * Created by Administrator on 2018/2/6.
 * 测量点的坐标
 */

public class PointCoordinate {
    private double X;
    private double Y;
    private double H;

    public PointCoordinate() {
    }

    public PointCoordinate(double X, double Y, double H) {
        this.X = X;
        this.Y = Y;
        this.H = H;
    }


    public double getX() {
        return X;
    }

    public void setX(double X) {
        this.X = X;
    }

    public double getY() {
        return Y;
    }

    public void setY(double Y) {
        this.Y = Y;
    }

    public double getH() {
        return H;
    }

    public void setH(double H) {
        this.H = H;
    }


    @Override
    public String toString() {
        return "{" +
                '\"'+
                "," + '\"' + "X" + '\"' + ":" + '\"' + X + '\"' +
                "," + '\"' + "Y" + '\"' + ":" + '\"' + Y + '\"' +
                "," + '\"' + "H" + '\"' + ":" + '\"' + H + '\"' +
                '}';
    }


}
