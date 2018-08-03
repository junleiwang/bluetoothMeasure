package com.example.ccqzy.beans;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Administrator on 2018/4/25.
 */

@Table(name = "cimcstable")
public class CimcsBean {

    @Column(name = "id",isId = true)
    private String id;

    @Column(name = "x")
    private double x;

    @Column(name = "y")
    private double y;

    @Column(name = "z")
    private double z;

    @Column(name = "rotx")
    private double rotx;

    @Column(name = "roty")
    private double roty;

    @Column(name = "rotz")
    private double rotz;

    @Column(name = "scale")
    private double scale;

    public CimcsBean() {
    }

    public CimcsBean(String id, double x, double y, double z, double rotx, double roty, double rotz, double scale) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
        this.scale = scale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public double getRotx() {
        return rotx;
    }

    public void setRotx(double rotx) {
        this.rotx = rotx;
    }

    public double getRoty() {
        return roty;
    }

    public void setRoty(double roty) {
        this.roty = roty;
    }

    public double getRotz() {
        return rotz;
    }

    public void setRotz(double rotz) {
        this.rotz = rotz;
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    @Override
    public String toString() {
        return "CimcsBean{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", rotx=" + rotx +
                ", roty=" + roty +
                ", rotz=" + rotz +
                ", scale=" + scale +
                '}';
    }
}
