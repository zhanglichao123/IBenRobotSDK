package com.samton.IBenRobotSDK.face;

/**
 * Created by lhg on 2017/10/18.
 */
public interface FaceCheckLicenseCallBack {
    void onCheckSuccess();

    void onCheckFail(String errorMessage);
}
