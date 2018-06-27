package com.samton.ibenrobotdemo.events;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/15 13:59
 *   desc    : 电量信息事件
 *   version : 1.0
 * </pre>
 */
public class BatteryEvent {

    private String mBatteryInfo;

    public BatteryEvent(String batteryInfo) {
        mBatteryInfo = batteryInfo;
    }

    public String getBatteryInfo() {
        return mBatteryInfo == null ? "" : mBatteryInfo;
    }
}
