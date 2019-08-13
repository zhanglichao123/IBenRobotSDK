package com.samton.IBenRobotSDK.core;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.iflytek.cloud.SpeechUtility;
import com.samton.AppConfig;
import com.samton.IBenRobotSDK.face.FaceCheckLicenseCallBack;
import com.samton.IBenRobotSDK.face.FaceManager;
import com.samton.IBenRobotSDK.net.HttpRequest;
import com.samton.IBenRobotSDK.net.HttpUtil;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.samton.IBenRobotSDK.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : syk
 * e-mail : shenyukun1024@gmail.com
 * time   : 2017/09/07
 * desc   : 机器人SDK入口
 */
public class MainSDK {
    private static Application mApplication;
    private static volatile MainSDK mInstance = null;

    private MainSDK() {
    }

    public static MainSDK getInstance() {
        if (mInstance == null) {
            synchronized (MainSDK.class) {
                if (mInstance == null) {
                    mInstance = new MainSDK();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化SDK
     *
     * @param application Application对象
     */
    public void init(Application application, String appkey, String type) {
        mApplication = application;
        // 设置AppKey
        AppConfig.ROBOT_APPID = appkey;
        // 当前主板类型
        AppConfig.PLANK_TYPE = type;
        // 初始化工具类
        Utils.init(mApplication);
        // 读取XML中的必须配置
        readMetaDataAndSave(mApplication);
        // 科大讯飞的语音系统
        SpeechUtility.createUtility(mApplication, AppConfig.IFLYTEK_APPKEY);
    }

    /**
     * 单独初始化Fece++
     */
    public void initFacepp(int faceppRawId) {
        FaceManager faceManager = FaceManager.getInstance();
        // 检验人脸识别证书是否过期
        faceManager.CheckFaceLicense(mApplication, faceppRawId, new FaceCheckLicenseCallBack() {
            @Override
            public void onCheckSuccess() {
                LogUtils.e("加载证书成功");
                // 初始化人脸识别
                faceManager.initCheckFace(mApplication, faceppRawId);
            }

            @Override
            public void onCheckFail(String errorMessage) {
                LogUtils.e("加载证书失败" + errorMessage);
            }
        });
    }

    /**
     * 读取项目配置信息并保存到本地
     */
    private void readMetaDataAndSave(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
//            // 获取AppKey
//            AppConfig.ROBOT_APPID = info.metaData.getString("IBEN_APPKEY", "");
//            // 获取当前主板类型
//            AppConfig.PLANK_TYPE = info.metaData.getString("PLANK_TYPE", "rk3399l");
            // 获取科大讯飞的AppKey
            AppConfig.IFLYTEK_APPKEY = info.metaData.getString("IFLYTEK_APPKEY", "");
            // 容联云的AppID
            AppConfig.YTX_APPID = info.metaData.getString("YTX_APPID", "");
            // 容联云的ToKen
            AppConfig.YTX_APPTOKEN = info.metaData.getString("YTX_APPTOKEN", "");
            // Face++的AppKey
            AppConfig.FACE_KEY = info.metaData.getString("FACE_KEY", "");
            // Face++的Secret
            AppConfig.FACE_SECRET = info.metaData.getString("FACE_SECRET", "");
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("请在AndroidManifest中配置相应的数据");
        }
    }

    /**
     * 激活机器人
     */
    @SuppressLint("CheckResult")
    public void activeRobot(IActiveCallBack callBack) {
        // 机器人执行激活操作
        HttpUtil.getInstance().create(HttpRequest.class)
                .activeRobot(AppConfig.ROBOT_APPID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(initBean -> {
                    // 初始化成功
                    if (initBean.getRs() != -1) {
                        // 成功回调
                        if (callBack != null)
                            callBack.onSuccess();
                    } else {
                        // 失败回调
                        if (callBack != null)
                            callBack.onFailed(initBean.getData().getErrorMsg());
                    }
                }, throwable -> {
                    // 初始化失败
                    if (callBack != null)
                        callBack.onFailed(throwable.getMessage());
                });
    }

    /**
     * 机器人激活回调
     */
    public interface IActiveCallBack {
        /**
         * 激活机器人成功
         */
        void onSuccess();

        /**
         * 激活机器人失败
         *
         * @param errorMsg 错误信息
         */
        void onFailed(String errorMsg);
    }
}
