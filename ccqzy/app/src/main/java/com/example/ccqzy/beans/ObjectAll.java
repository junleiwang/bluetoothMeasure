package com.example.ccqzy.beans;

import com.lidroid.xutils.http.RequestParams;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/2/9.
 */

public class ObjectAll extends RequestParams implements Serializable {

    private String mac;
    private String carbodyMeasureInfos;

    public ObjectAll() {
    }

    public ObjectAll(String mac, String carbodyMeasureInfos) {
        this.mac = mac;
        this.carbodyMeasureInfos = carbodyMeasureInfos;
    }

    public String getMAC() {
        return mac;
    }

    public void setMAC(String mac) {
        this.mac = mac;
    }

    public String getCarbodyMeasureInfos() {
        return carbodyMeasureInfos;
    }

    public void setCarbodyMeasureInfos(String carbodyMeasureInfos) {
        this.carbodyMeasureInfos = carbodyMeasureInfos;
    }

    @Override
    public String toString() {
        return "{" +
                '\"' + "mac" + '\"' + ":" + '\"' + mac + '\"' +
                "," + '\"' + "carbodyMeasureInfos" + '\"' + ":" + carbodyMeasureInfos +
                '}';
    }

}
