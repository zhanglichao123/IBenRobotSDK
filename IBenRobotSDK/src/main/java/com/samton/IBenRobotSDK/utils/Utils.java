package com.samton.IBenRobotSDK.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.samton.AppConfig;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 16/12/08
 *     desc  : Utils初始化相关
 * </pre>　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　丶
 */
public final class Utils {

    @SuppressLint("StaticFieldLeak")
    private static Application sApplication;

    private Utils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    public static void init(@NonNull final Application app) {
        Utils.sApplication = app;
    }

    /**
     * 获取Application
     *
     * @return Application
     */
    public static Application getApp() {
        if (sApplication != null) return sApplication;
        throw new NullPointerException("u should init first");
    }

    /**
     * 获取清单文件中配置的MetaData
     * @param key MetaData的key
     * @return MetaData的值
     */
    public static String getMetaData(String key) {
        String value = "";
        try {
            ApplicationInfo info = sApplication.getPackageManager()
                    .getApplicationInfo(sApplication.getPackageName(), PackageManager.GET_META_DATA);
            // 获取AppKey
            AppConfig.ROBOT_APPID = info.metaData.getString("IBEN_APPKEY", "");
            value = info.metaData.getString(key, "");

        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("请在AndroidManifest中配置相应的数据");
        }
        return value;
    }
}
