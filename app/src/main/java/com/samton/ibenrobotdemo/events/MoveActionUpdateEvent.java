package com.samton.ibenrobotdemo.events;

import com.slamtec.slamware.action.Path;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/21 14:17
 *   desc    : 动作更新事件
 *   version : 1.0
 * </pre>
 */
public class MoveActionUpdateEvent {

    private Path mRemainingMilestones;
    private Path mRemainingPath;

    public MoveActionUpdateEvent(Path remainingMilestones, Path remainingPath) {
        this.mRemainingMilestones = remainingMilestones;
        this.mRemainingPath = remainingPath;
    }

    public Path getRemainingMilestones() {
        return mRemainingMilestones;
    }

    public Path getRemainingPath() {
        return mRemainingPath;
    }
}
