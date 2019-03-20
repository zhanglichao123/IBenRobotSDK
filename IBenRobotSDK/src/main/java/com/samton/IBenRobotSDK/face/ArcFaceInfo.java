package com.samton.IBenRobotSDK.face;

import com.arcsoft.face.FaceInfo;

public class ArcFaceInfo extends FaceInfo {
    // 人脸序号
    private int index;
    // 年龄
    private int age;
    // 姓别
    private int gender;
    // 前后俯仰角度
    private float pitch;
    // 横向旋转角度
    private float roll;
    // 左右转动角度
    private float yaw;
    // 状态信息，如果值为0 表示检测正常，roll，yaw， pitch 的数值可信，否则上面三个角度信息是不可信的
    private int status;
    // 以上为所有用户共有的信息，以下为VIP信息
    private VipFaceBean vipFaceBean;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getRoll() {
        return roll;
    }

    public void setRoll(float roll) {
        this.roll = roll;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public VipFaceBean getVipFaceBean() {
        return vipFaceBean;
    }

    public void setVipFaceBean(VipFaceBean vipFaceBean) {
        this.vipFaceBean = vipFaceBean;
    }
}
