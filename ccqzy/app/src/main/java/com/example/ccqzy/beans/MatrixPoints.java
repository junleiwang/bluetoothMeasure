package com.example.ccqzy.beans;

/**
 * Created by Administrator on 2018/3/6.
 *
 * 旋转矩阵的点
 */

public class MatrixPoints {
    public double A1;
    public double A2;
    public double A3;
    public double B1;
    public double B2;
    public double B3;
    public double C1;
    public double C2;
    public double C3;

    public MatrixPoints() {
    }

    public MatrixPoints(double a1, double a2, double a3, double b1, double b2, double b3, double c1, double c2, double c3) {
        A1 = a1;
        A2 = a2;
        A3 = a3;
        B1 = b1;
        B2 = b2;
        B3 = b3;
        C1 = c1;
        C2 = c2;
        C3 = c3;
    }

    public double getA1() {
        return A1;
    }

    public void setA1(double a1) {
        A1 = a1;
    }

    public double getA2() {
        return A2;
    }

    public void setA2(double a2) {
        A2 = a2;
    }

    public double getA3() {
        return A3;
    }

    public void setA3(double a3) {
        A3 = a3;
    }

    public double getB1() {
        return B1;
    }

    public void setB1(double b1) {
        B1 = b1;
    }

    public double getB2() {
        return B2;
    }

    public void setB2(double b2) {
        B2 = b2;
    }

    public double getB3() {
        return B3;
    }

    public void setB3(double b3) {
        B3 = b3;
    }

    public double getC1() {
        return C1;
    }

    public void setC1(double c1) {
        C1 = c1;
    }

    public double getC2() {
        return C2;
    }

    public void setC2(double c2) {
        C2 = c2;
    }

    public double getC3() {
        return C3;
    }

    public void setC3(double c3) {
        C3 = c3;
    }

    @Override
    public String toString() {
        return "MatrixPoints{" +
                "A1=" + A1 +
                ", A2=" + A2 +
                ", A3=" + A3 +
                ", B1=" + B1 +
                ", B2=" + B2 +
                ", B3=" + B3 +
                ", C1=" + C1 +
                ", C2=" + C2 +
                ", C3=" + C3 +
                '}';
    }
}
