package com.samton.ibenrobotdemo.events;

import android.graphics.RectF;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/15 13:52
 *   desc    : 地图更新事件
 *   version : 1.0
 * </pre>
 */
public class MapUpdateEvent {

    private RectF mArea;

    public MapUpdateEvent(RectF area) {
        mArea = area;
    }

    public RectF getArea() {
        return mArea;
    }
}
