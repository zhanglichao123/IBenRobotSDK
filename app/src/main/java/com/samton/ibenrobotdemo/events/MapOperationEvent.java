package com.samton.ibenrobotdemo.events;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/15 13:55
 *   desc    : 地图操作事件
 *   version : 1.0
 * </pre>
 */
public class MapOperationEvent {
    /**
     * 保存地图
     */
    public final static int TYPE_SAVE_MAP = 0x0;
    /**
     * 加载地图
     */
    public final static int TYPE_LOAD_MAP = 0x1;
    /**
     * 操作类型
     */
    private int mOperationType;

    public MapOperationEvent(int operationType) {
        mOperationType = operationType;
    }

    public int getOperationType() {
        return mOperationType;
    }
}
