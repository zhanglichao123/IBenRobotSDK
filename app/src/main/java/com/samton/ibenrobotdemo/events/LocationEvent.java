package com.samton.ibenrobotdemo.events;

import com.slamtec.slamware.robot.Location;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/15 13:40
 *   desc    : 获取当前坐标点事件
 *   version : 1.0
 * </pre>
 */
public class LocationEvent {

    private Location mLocation;

    public LocationEvent(Location location) {
        mLocation = location;
    }

    public Location getLocation() {
        return mLocation;
    }
}
