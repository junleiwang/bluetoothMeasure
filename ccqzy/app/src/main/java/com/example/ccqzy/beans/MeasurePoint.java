package com.example.ccqzy.beans;

/**
 * Created by Administrator on 2018/2/6.
 * 测量点的坐标
 */

public class MeasurePoint {
    private String pointName;
    private String pointX;
    private String pointY;
    private String pointH;
    private String isOver;
    private int sortOrder;
    private String pointId;

    public MeasurePoint() {
    }

    public MeasurePoint(String pointName, String pointX, String pointY, String pointH, String isOver,
                        int sortOrder, String pointId) {
        this.pointName = pointName;
        this.pointX = pointX;
        this.pointY = pointY;
        this.pointH = pointH;
        this.isOver = isOver;
        this.sortOrder = sortOrder;
        this.pointId = pointId;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getPointX() {
        return pointX;
    }

    public void setPointX(String pointX) {
        this.pointX = pointX;
    }

    public String getPointY() {
        return pointY;
    }

    public void setPointY(String pointY) {
        this.pointY = pointY;
    }

    public String getPointH() {
        return pointH;
    }

    public void setPointH(String pointH) {
        this.pointH = pointH;
    }

    public String getIsOver() {
        return isOver;
    }

    public void setIsOver(String isOver) {
       this.isOver = isOver;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getPointId() {
        return pointId;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    @Override
    public String toString() {
        return "{" +
                '\"' + "pointName" + '\"' + ":" + '\"' + pointName + '\"' +
                "," + '\"' + "pointX" + '\"' + ":" + '\"' + pointX + '\"' +
                "," + '\"' + "pointY" + '\"' + ":" + '\"' + pointY + '\"' +
                "," + '\"' + "pointH" + '\"' + ":" + '\"' + pointH + '\"' +
                "," + '\"' + "isOver" + '\"' + ":" + '\"' + isOver + '\"' +
                "," + '\"' + "sortOrder" + '\"' + ":" + '\"' + sortOrder + '\"' +
                "," + '\"' + "pointId" + '\"' + ":" + '\"' + pointId + '\"' +
                '}';
    }


}
