package com.samton.IBenRobotSDK.interfaces;

import com.samton.IBenRobotSDK.data.SensorData;

/**
 * <pre>
 *   author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/01/19 13:55
 *   desc    : 串口回调
 *   version : 1.0
 * </pre>
 */

public interface ISensorCallBack {
    /**
     * 传感器参数数据
     *
     * @param sensor 数据
     */
    void onReadData(SensorData sensor);
}
