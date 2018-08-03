package com.example.ccqzy.androidutils;

/**
 * Created by Administrator on 2018/2/5.
 */

public class MathUtils {

    public MathUtils(){

    }

    /*
  * 获取一个角度的sin（正弦）值
  * */
    public double getSinValue(String value){
        double value1 = Double.parseDouble(value)* Math.PI/180;
        return Math.sin(value1);
    }

    /*
    * 获取一个角度的cos（余弦）值
    * */
    public double getCosValue(String value){
        double value1 = Double.parseDouble(value)* Math.PI/180;
        return Math.cos(value1);
    }

    /*
 * 获取一个角度的tan（正切）值
 * */
    public double getTanValue(String value){
        double value1 = Double.parseDouble(value)* Math.PI/180;
        return Math.tan(value1);
    }

    /*
    * 获取一个角度的cot(余切)值
    * */
    public double getCotValue(String value){
        double value1 = Double.parseDouble(value)* Math.PI/180;
        return 1/ Math.tan(value1);
    }
}
