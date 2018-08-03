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

@Table(name = "modleDatas")
public class ModleDatas implements Serializable {

    @Column(name = "Id",isId = true,autoGen = false)
    private String Id;

    @Column(name = "MeasureInfoId")
    private String MeasureInfoId;

    @Column(name = "MeasurePointId")
    private String MeasurePointId;

    @Column(name = "MeasurePointName")
    private String MeasurePointName;

    @Column(name = "SortOrder")
    private int SortOrder;

    @Column(name = "isInitail")
    boolean isInitail;

    @Column(name = "X")
    private String X;

    @Column(name = "Y")
    private String Y;

    @Column(name = "H")
    private String H;

    @Column(name = "CreationTime")
    private String CreationTime;

    @Column(name = "isOver")
    boolean isOver;

    public ModleDatas() {
    }

    public ModleDatas(String id, String measureInfoId, String measurePointId,
                      String measurePointName, int sortOrder, boolean isInitail,
                      String x, String y, String h, String creationTime, boolean isOver) {
        this.Id = id;
        this.MeasureInfoId = measureInfoId;
        this.MeasurePointId = measurePointId;
        this.MeasurePointName = measurePointName;
        this.SortOrder = sortOrder;
        this.isInitail = isInitail;
        this.X = x;
        this.Y = y;
        this.H = h;
        this.CreationTime = creationTime;
        this.isOver = isOver;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getMeasureInfoId() {
        return MeasureInfoId;
    }

    public void setMeasureInfoId(String measureInfoId) {
        MeasureInfoId = measureInfoId;
    }

    public String getMeasurePointId() {
        return MeasurePointId;
    }

    public void setMeasurePointId(String measurePointId) {
        MeasurePointId = measurePointId;
    }

    public String getMeasurePointName() {
        return MeasurePointName;
    }

    public void setMeasurePointName(String measurePointName) {
        MeasurePointName = measurePointName;
    }

    public int getSortOrder() {
        return SortOrder;
    }

    public void setSortOrder(int sortOrder) {
        SortOrder = sortOrder;
    }

    public boolean isIsInitail() {
        return isInitail;
    }

    public void setIsInitail(boolean isInitail) {
        this.isInitail = isInitail;
    }

    public String getX() {
        return X;
    }

    public void setX(String x) {
        X = x;
    }

    public String getY() {
        return Y;
    }

    public void setY(String y) {
        Y = y;
    }

    public String getH() {
        return H;
    }

    public void setH(String h) {
        H = h;
    }

    public String getCreationTime() {
        return CreationTime;
    }

    public void setCreationTime(String creationTime) {
        CreationTime = creationTime;
    }

    public boolean isIsOver() {
        return isOver;
    }

    public void setIsOver(boolean isOver) {
        this.isOver = isOver;
    }

    @Override
    public String toString() {
        return "{" +
                 '\"'+"Id"+'\"'+ ":"+'\"'+ Id + '\"' +
                ","+'\"'+"MeasureInfoId"+'\"'+ ":"+'\"'+ MeasureInfoId + '\"' +
                ","+ '\"'+"MeasurePointId"+'\"'+ ":"+'\"'+ MeasurePointId + '\"' +
                ","+ '\"'+"MeasurePointName"+'\"'+ ":"+'\"'+ MeasurePointName + '\"' +
                ","+ '\"'+"SortOrder"+'\"'+ ":"+'\"'+ SortOrder + '\"' +
                ","+ '\"'+"isIntail"+'\"'+ ":"+ isInitail +
                ","+ '\"'+"X"+'\"'+ ":"+'\"'+ X + '\"' +
                ","+ '\"'+"Y"+'\"'+ ":"+'\"'+ Y + '\"' +
                ","+ '\"'+"H"+'\"'+ ":"+'\"'+ H + '\"' +
                ","+ '\"'+"CreationTime"+'\"'+ ":"+'\"'+ CreationTime + '\"' +
                ","+ '\"'+"isOver"+'\"'+ ":"+'\"'+ isOver + '\"' +
                '}';
    }
}
