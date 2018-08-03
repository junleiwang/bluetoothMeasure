package com.example.ccqzy.beans;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/2/7.
 *
 * 测点bean类
 * boolean类型数据区分大小写,一般小写
 *
 */

@Table(name = "editModle")
public class EditModle implements Serializable,Comparable<EditModle> {

    @Column(name = "id",isId = true,autoGen = false)
    private String id;

    @Column(name = "orderNum")
    private int orderNum;

    @Column(name = "pointName")
    private String pointName;

    @Column(name = "x")
    private String x;

    @Column(name = "y")
    private String y;

    @Column(name = "h")
    private String h;


    public EditModle() {
    }

    public EditModle(String id, int orderNum, String pointName, String x, String y, String h) {
        this.id = id;
        this.orderNum = orderNum;
        this.pointName = pointName;
        this.x = x;
        this.y = y;
        this.h = h;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getX() {
        return x;
    }

    public void setX(String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(String y) {
        this.y = y;
    }

    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }


    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", orderNum='" + orderNum + '\'' +
                ", pointName='" + pointName + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", h='" + h + '\'' +
                '}';
    }


    @Override
    public int compareTo(EditModle o) {
        return this.getOrderNum()-o.getOrderNum();
    }
}
