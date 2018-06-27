package com.samton.ibenrobotdemo.events;

import com.slamtec.slamware.robot.LaserScan;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/15 14:01
 *   desc    : 更新雷达扫描数据事件
 *   version : 1.0
 * </pre>
 */
public class LaserScanUpdateEvent {
    
    private LaserScan mLaserScan;

    public LaserScanUpdateEvent(LaserScan laserScan) {
        mLaserScan = laserScan;
    }

    public LaserScan getLaserScan() {
        return mLaserScan;
    }
}
