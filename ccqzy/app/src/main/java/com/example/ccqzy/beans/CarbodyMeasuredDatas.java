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

@Table(name = "radianPoint")
public class CarbodyMeasuredDatas implements Serializable {

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

    @Column(name = "HorizontalAngle")
    private String HorizontalAngle;

    @Column(name = "RightAngles")
    private String RightAngles;

    @Column(name = "SlantDistance")
    private String SlantDistance;

    @Column(name = "CreationTime")
    private String CreationTime;

    public CarbodyMeasuredDatas() {
    }

    public CarbodyMeasuredDatas(String id, String measureInfoId, String measurePointId,
                                String measurePointName, int sortOrder, String HorizontalAngle, String RightAngles,
                                String SlantDistance, String creationTime) {
        this.Id = id;
        this.MeasureInfoId = measureInfoId;
        this.MeasurePointId = measurePointId;
        this.MeasurePointName = measurePointName;
        this.SortOrder = sortOrder;
        this.HorizontalAngle = HorizontalAngle;
        this.RightAngles = RightAngles;
        this.SlantDistance = SlantDistance;
        this.CreationTime = creationTime;
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

    public String getHorizontalAngle() {
        return HorizontalAngle;
    }

    public void setHorizontalAngle(String HorizontalAngle) {
        this.HorizontalAngle = HorizontalAngle;
    }

    public String getRightAngles() {
        return RightAngles;
    }

    public void setRightAngles(String RightAngles) {
        this.RightAngles = RightAngles;
    }

    public String getSlantDistance() {
        return SlantDistance;
    }

    public void setSlantDistance(String SlantDistance) {
        this.SlantDistance = SlantDistance;
    }

    public String getCreationTime() {
        return CreationTime;
    }

    public void setCreationTime(String creationTime) {
        CreationTime = creationTime;
    }

    @Override
    public String toString() {
        return "{" +
                 '\"'+"Id"+'\"'+ ":"+'\"'+ Id + '\"' +
                ","+'\"'+"MeasureInfoId"+'\"'+ ":"+'\"'+ MeasureInfoId + '\"' +
                ","+ '\"'+"MeasurePointId"+'\"'+ ":"+'\"'+ MeasurePointId + '\"' +
                ","+ '\"'+"MeasurePointName"+'\"'+ ":"+'\"'+ MeasurePointName + '\"' +
                ","+ '\"'+"SortOrder"+'\"'+ ":"+'\"'+ SortOrder + '\"' +
                ","+ '\"'+"HorizontalAngle"+'\"'+ ":"+'\"'+ HorizontalAngle + '\"' +
                ","+ '\"'+"RightAngles"+'\"'+ ":"+'\"'+ RightAngles + '\"' +
                ","+ '\"'+"SlantDistance"+'\"'+ ":"+'\"'+ SlantDistance + '\"' +
                ","+ '\"'+"CreationTime"+'\"'+ ":"+'\"'+ CreationTime + '\"' +
                '}';
    }
}
