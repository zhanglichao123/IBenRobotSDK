package com.samton.ibenrobotdemo.events;

import com.slamtec.slamware.geometry.Line;

import java.util.Vector;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/15 14:28
 *   desc    : 更新虚拟墙数据事件
 *   version : 1.0
 * </pre>
 */
public class WallUpdateEvent {

    private Vector<Line> mWalls = new Vector<>();

    public WallUpdateEvent(Vector<Line> walls) {
        mWalls.clear();
        mWalls.addAll(walls);
    }

    public Vector<Line> getWalls() {
        if (mWalls == null) {
            return new Vector<>();
        }
        return mWalls;
    }
}
