package com.example.ccqzy.beans;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/2/7.
 * 上传数据的bean类
 */
@Table(name = "carbodyMeasureInfos")
public class CarbodyMeasureInfos implements Serializable {

    @Column(name = "Id",isId = true,autoGen = false)
    private String Id;

    @Column(name = "Name")
    private String Name;

    @Column(name = "Component")
    private String Component;

    @Column(name = "CarType")
    private String CarType;

    @Column(name = "CarModel")
    private String CarModel;

    @Column(name = "SteelNo")
    private String SteelNo;

    @Column(name = "TrainNo")
    private String TrainNo;

    @Column(name = "ObserveNo")
    private int ObserveNo;

    @Column(name = "Position")
    private String Position;

    @Column(name = "X")
    private String X;

    @Column(name = "Y")
    private String Y;

    @Column(name = "H")
    private String H;

    @Column(name = "CreationTime")
    private String CreationTime;

    @Column(name = "StandpointId")
    private String StandpointId;

    @Column(name ="finished")
    boolean finished;

    @Column(name ="uped")
    boolean uped;

    @Column(name = "CarbodyCalculatedDatas")
    private List<CarbodyCalculatedDatas> CarbodyCalculatedDatas;

    @Column(name = "CarbodyMeasuredDatas")
    private List<CarbodyMeasuredDatas> CarbodyMeasuredDatas;

    public CarbodyMeasureInfos() {
    }

    public CarbodyMeasureInfos(String id, String name, String component, String carType,
                               String carModel, String steelNo, String trainNo, int observeNo,
                               String position, String x, String y, String h, String creationTime,
                               String standpointId, boolean finished, boolean uped, List<CarbodyCalculatedDatas> carbodyCalculatedDatas,
                               List<CarbodyMeasuredDatas> carbodyMeasuredDatas) {
        Id = id;
        Name = name;
        Component = component;
        CarType = carType;
        CarModel = carModel;
        SteelNo = steelNo;
        TrainNo = trainNo;
        ObserveNo = observeNo;
        Position = position;
        X = x;
        Y = y;
        H = h;
        CreationTime = creationTime;
        StandpointId = standpointId;
        this.finished = finished;
        this.uped = uped;
        CarbodyCalculatedDatas = carbodyCalculatedDatas;
        CarbodyMeasuredDatas = carbodyMeasuredDatas;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getComponent() {
        return Component;
    }

    public void setComponent(String component) {
        Component = component;
    }

    public String getCarType() {
        return CarType;
    }

    public void setCarType(String carType) {
        CarType = carType;
    }

    public String getCarModel() {
        return CarModel;
    }

    public void setCarModel(String carModel) {
        CarModel = carModel;
    }

    public String getSteelNo() {
        return SteelNo;
    }

    public void setSteelNo(String steelNo) {
        SteelNo = steelNo;
    }

    public String getTrainNo() {
        return TrainNo;
    }

    public void setTrainNo(String trainNo) {
        TrainNo = trainNo;
    }

    public int getObserveNo() {
        return ObserveNo;
    }

    public void setObserveNo(int observeNo) {
        ObserveNo = observeNo;
    }

    public String getPosition() {
        return Position;
    }

    public void setPosition(String position) {
        Position = position;
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

    public String getStandpointId() {
        return StandpointId;
    }

    public void setStandpointId(String standpointId) {
        StandpointId = standpointId;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public boolean isUped() {
        return uped;
    }

    public void setUped(boolean uped) {
        this.uped = uped;
    }

    public List<CarbodyCalculatedDatas> getCarbodyCalculatedDatas() {
        return CarbodyCalculatedDatas;
    }

    public void setCarbodyCalculatedDatas(List<CarbodyCalculatedDatas> carbodyCalculatedDatas) {
        CarbodyCalculatedDatas = carbodyCalculatedDatas;
    }

    public List<CarbodyMeasuredDatas> getCarbodyMeasuredDatas() {
        return CarbodyMeasuredDatas;
    }

    public void setCarbodyMeasuredDatas(List<CarbodyMeasuredDatas> carbodyMeasuredDatas) {
        CarbodyMeasuredDatas = carbodyMeasuredDatas;
    }

    @Override
    public String toString() {
        return "{" +
                '\"'+"Id"+'\"'+ ":"+'\"'+ Id + '\"' +
                ","+ '\"'+"Name"+'\"'+ ":"+'\"'+ Name + '\"' +
                ","+ '\"'+"Component"+'\"'+ ":"+'\"'+ Component + '\"' +
                ","+ '\"'+"CarType"+'\"'+ ":"+'\"'+ CarType + '\"' +
                ","+ '\"'+"CarModel"+'\"'+ ":"+'\"'+ CarModel + '\"' +
                ","+'\"'+"SteelNo"+'\"'+ ":"+'\"'+ SteelNo + '\"' +
                ","+ '\"'+"TrainNo"+'\"'+ ":"+'\"'+ TrainNo + '\"' +
                ","+ '\"'+"ObserveNo"+'\"'+ ":"+'\"'+ ObserveNo + '\"' +
                ","+ '\"'+"Position"+'\"'+ ":"+'\"'+ Position + '\"' +
                ","+'\"'+"X"+'\"'+ ":"+'\"'+ X + '\"' +
                ","+ '\"'+"Y"+'\"'+ ":"+'\"'+ Y + '\"' +
                ","+ '\"'+"H"+'\"'+ ":"+'\"'+ H + '\"' +
                ","+ '\"'+"CreationTime"+'\"'+ ":"+'\"'+ CreationTime + '\"' +
                ","+ '\"'+"StandpointId"+'\"'+ ":"+'\"'+ StandpointId + '\"' +
                ","+ '\"'+"finished"+'\"'+ ":"+'\"'+ finished + '\"' +
                ","+ '\"'+"uped"+'\"'+ ":"+'\"'+ uped + '\"' +
                ","+'\"'+"CarbodyCalculatedDatas"+'\"'+ ":"+'\"'+ CarbodyCalculatedDatas + '\"' +
                ","+'\"'+"CarbodyMeasuredDatas"+'\"'+ ":"+'\"'+ CarbodyMeasuredDatas + '\"' +
                '}';
    }
}
