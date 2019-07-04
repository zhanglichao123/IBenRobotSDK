package com.samton.IBenRobotSDK.core;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.iflytek.cloud.SpeechUtility;
import com.samton.IBenRobotSDK.data.Constants;
import com.samton.IBenRobotSDK.face.FaceCheckLicenseCallBack;
import com.samton.IBenRobotSDK.face.FaceManager;
import com.samton.IBenRobotSDK.net.HttpRequest;
import com.samton.IBenRobotSDK.net.HttpUtil;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.samton.IBenRobotSDK.utils.SPUtils;
import com.samton.IBenRobotSDK.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * author : syk
 * e-mail : shenyukun1024@gmail.com
 * time   : 2017/09/07
 * desc   : 机器人SDK入口
 */
public final class MainSDK {
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
     *                    请在XML中配置好robUuid、appKey和appId
     */
    public void init(Application application) {
        mApplication = application;
        // 初始化工具类
        Utils.init(mApplication);
        // 读取XML中的必须配置
        readMetaDataAndSave(mApplication);
        // 科大讯飞的语音系统
        SpeechUtility.createUtility(mApplication, Constants.APP_ID);
    }

    /**
     * 单独初始化Fece++
     */
    public void initFacepp(final int faceppRawId) {
        final FaceManager faceManager = FaceManager.getInstance();
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
                throw new UnsupportedOperationException("加载证书失败" + errorMessage);
            }
        });
    }

    /**
     * 读取项目配置信息并保存到本地
     */
    private void readMetaDataAndSave(Context mContext) {
        try {
            ApplicationInfo info = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(), PackageManager.GET_META_DATA);
            // 获取AppKey
            String iben_appkey = info.metaData.getString("IBEN_APPKEY", "");
            // 获取当前主板类型
            String plank_type = info.metaData.getString("PLANK_TYPE", "rk3399l");
            // 初始化机器人配置信息
            SPUtils spInstance = SPUtils.getInstance();
            spInstance.put(Constants.ROBOT_APP_KEY, iben_appkey);// 机器人唯一标识
            spInstance.put(Constants.ROBOT_IM_ACCOUNT, iben_appkey);// 容联云ID
            spInstance.put(Constants.PLANK_TYPE, plank_type);// 主板类型
        } catch (PackageManager.NameNotFoundException e) {
            LogUtils.e("请在AndroidManifest中配置相应的数据");
        }
    }

    /**
     * 激活机器人
     */
    @SuppressLint("CheckResult")
    public void activeRobot(final IActiveCallBack callBack) {
        // 机器人执行激活操作
        HttpUtil.getInstance().create(HttpRequest.class)
                .activeRobot(SPUtils.getInstance().getString(Constants.ROBOT_APP_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(initBean -> {
                    // 初始化成功
                    if (initBean.getRs() != -1) {
                        // 成功回调
                        callBack.onSuccess();
                    } else {
                        // 失败回调
                        callBack.onFailed(initBean.getData().getErrorMsg());
                    }
                }, throwable -> {
                    // 初始化失败
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
