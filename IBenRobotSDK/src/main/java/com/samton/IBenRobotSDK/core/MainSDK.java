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
import com.samton.IBenRobotSDK.net.HttpUtils;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.samton.IBenRobotSDK.utils.Utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.plugins.RxJavaPlugins;
import okhttp3.OkHttpClient;
import rxhttp.HttpSender;

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
     * @param application 上下文
     * @param appKey      机器人ID
     * @param type        主板类型
     * @param isDebug     是否是测试环境
     */
    public void init(Application application, String appKey, String type, boolean isDebug) {
        mApplication = application;
        AppConfig.DEBUG = isDebug;
        // 设置AppKey
        AppConfig.ROBOT_APPID = appKey;
        // 当前主板类型
        AppConfig.PLANK_TYPE = type;
        // 初始化工具类
        Utils.init(mApplication);
        // 读取XML中的必须配置
        readMetaDataAndSave(mApplication);
        // 科大讯飞的语音系统
        SpeechUtility.createUtility(mApplication, AppConfig.IFLYTEK_APPKEY);
        // 初始化网络请求
        initHttp();
    }

    /**
     * 初始化网络请求
     */
    private void initHttp() {
        //设置baseUrl的地址
        AppConfig.BASE_URL = AppConfig.DEBUG ? AppConfig.DEBUG_BASEURL : AppConfig.RELESE_URL;
        //设置QAUrl的地址
        AppConfig.QA_URL = AppConfig.DEBUG ? AppConfig.DEBUG_QAURL : AppConfig.RELESE_URL;
        // 设置debug模式，此模式下有日志打印
        HttpSender.setDebug(true);
        // 自定义OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                // 连接超时>>>10秒
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                // 读取超时>>>10秒
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        // 非必须,只能初始化一次，第二次将抛出异常
        HttpSender.init(okHttpClient);
        // 设置RxJava全局异常处理
        RxJavaPlugins.setErrorHandler(throwable -> {
            /*
              RxJava2的一个重要的设计理念是：不吃掉任何一个异常,即抛出的异常无人处理，便会导致程序崩溃
              这就会导致一个问题，当RxJava2“downStream”取消订阅后，“upStream”仍有可能抛出异常，
              这时由于已经取消订阅，“downStream”无法处理异常，此时的异常无人处理，便会导致程序崩溃
             */
        });
    }

    /**
     * 读取项目配置信息并保存到本地
     */
    private void readMetaDataAndSave(Context context) {
        try {
            ApplicationInfo info = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
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
     *
     * @param faceppRawId Face++的model
     * @param callBack    激活回调接口
     */
    @SuppressLint("CheckResult")
    public void activeRobot(final int faceppRawId, final IActiveCallBack callBack) {
        // 检验人脸识别证书是否过期
        FaceManager.getInstance().CheckFaceLicense(mApplication, faceppRawId, new FaceCheckLicenseCallBack() {
            @Override
            public void onCheckSuccess() {
                LogUtils.e("加载证书成功");
                // 初始化人脸识别
                FaceManager.getInstance().initCheckFace(mApplication, faceppRawId);
                // 机器人执行激活操作
                HttpUtils.activeRobot().subscribe(initBean -> {
                    // 初始化成功
                    if (initBean.getRs() != -1) {
                        // 成功回调
                        if (callBack != null) callBack.onSuccess();
                    } else {
                        // 失败回调
                        if (callBack != null) callBack.onFailed(initBean.getData().getErrorMsg());
                    }
                }, throwable -> {
                    // 初始化失败
                    if (callBack != null) callBack.onFailed(throwable.getMessage());
                });
            }

            @Override
            public void onCheckFail(String errorMessage) {
                LogUtils.e("加载证书失败" + errorMessage);
                if (callBack != null) callBack.onSuccess();
            }
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
