package com.samton.IBenRobotSDK.face;

import com.arcsoft.face.FaceFeature;

/**
 * 本地VIP人脸信息库
 */
public class VipFaceBean {
    // 位置标示
    private int _id;
    // 姓名
    private String name;
    // 姓别
    private int sex;
    // 职位
    private String jobTitle;
    // 人脸图片
    private String faceUrl;
    // 人脸特征信息
    private FaceFeature faceFeature;

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getFaceUrl() {
        return faceUrl;
    }

    public void setFaceUrl(String faceUrl) {
        this.faceUrl = faceUrl;
    }

    public FaceFeature getFaceFeature() {
        return faceFeature;
    }

    public void setFaceFeature(FaceFeature faceFeature) {
        this.faceFeature = faceFeature;
    }
}
