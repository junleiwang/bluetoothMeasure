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

@Table(name = "stationPoint")
public class StationPoint implements Serializable {

    @Column(name = "id",isId = true,autoGen = false)
    private String id;

    @Column(name = "orderNum")
    private String orderNum;

    @Column(name = "pointNum")
    private String pointNum;

    @Column(name = "x")
    private String x;

    @Column(name = "y")
    private String y;

    @Column(name = "h")
    private String h;

    @Column(name = "high")
    private String high;

    @Column(name = "note")
    private String note;

    @Column(name = "or1")
    private String or1;


    public StationPoint() {
    }

    public StationPoint(String id, String orderNum, String pointNum, String x, String y, String h
    , String high, String note, String or1) {
        this.id = id;
        this.orderNum = orderNum;
        this.pointNum = pointNum;
        this.x = x;
        this.y = y;
        this.h = h;
        this.high = high;
        this.note = note;
        this.or1 = or1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public String getPointNum() {
        return pointNum;
    }

    public void setPointNum(String pointNum) {
        this.pointNum = pointNum;
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

    public String getHigh() {
        return high;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOr1() {
        return or1;
    }

    public void setOr1(String or1) {
        this.or1 = or1;
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", orderNum='" + orderNum + '\'' +
                ", pointNum='" + pointNum + '\'' +
                ", x='" + x + '\'' +
                ", y='" + y + '\'' +
                ", h='" + h + '\'' +
                ", high='" + high + '\'' +
                ", note='" + note + '\'' +
                ", or1='" + or1 + '\'' +
                '}';
    }
}
