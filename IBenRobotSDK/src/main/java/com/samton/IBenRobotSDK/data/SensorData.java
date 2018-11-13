package com.samton.IBenRobotSDK.data;

/**
 * Created by 34794 on 2018/11/12.
 * 传感器数据类
 */

public class SensorData {
    //所有的数据
    private byte[] data;
    private boolean isLegal=false;//是否合法  即是否通过数据验证
    //二氧化碳 单位 ppm
    private int carbonDioxide;
    //甲醛  单位 ug/m3
    private int formaldehyde;
    //所有室内有机气态物质  ug/m3
    private int TVOC;
    private int PM2_5;
    private int PM10;
    //温度  摄氏度
    private double temperature;
    //湿度  RH
    private double humidity;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isLegal() {
        return isLegal;
    }

    public void setLegal(boolean legal) {
        isLegal = legal;
    }

    public int getCarbonDioxide() {
        return carbonDioxide;
    }

    public void setCarbonDioxide(int carbonDioxide) {
        this.carbonDioxide = carbonDioxide;
    }

    public int getFormaldehyde() {
        return formaldehyde;
    }

    public void setFormaldehyde(int formaldehyde) {
        this.formaldehyde = formaldehyde;
    }

    public int getTVOC() {
        return TVOC;
    }

    public void setTVOC(int TVOC) {
        this.TVOC = TVOC;
    }

    public int getPM2_5() {
        return PM2_5;
    }

    public void setPM2_5(int PM2_5) {
        this.PM2_5 = PM2_5;
    }

    public int getPM10() {
        return PM10;
    }

    public void setPM10(int PM10) {
        this.PM10 = PM10;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}
