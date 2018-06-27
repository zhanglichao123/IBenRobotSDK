package com.samton.ibenrobotdemo.events;

import com.slamtec.slamware.robot.Pose;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/15 14:02
 *   desc    : 更新机器人姿态事件
 *   version : 1.0
 * </pre>
 */
public class RobotPoseUpdateEvent {
    private Pose mPose;

    public RobotPoseUpdateEvent(Pose pose) {
        mPose = pose;
    }

    public Pose getPose() {
        return mPose;
    }
}
