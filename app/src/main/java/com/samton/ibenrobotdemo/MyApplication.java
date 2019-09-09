package com.samton.ibenrobotdemo;

import android.app.Application;

import com.samton.IBenRobotSDK.core.MainSDK;

/**
 *
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // 初始化小笨机器人SDK
        MainSDK.getInstance().init(this, "", "rk3288l", false);
    }
}
