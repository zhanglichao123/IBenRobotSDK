package com.samton.IBenRobotSDK.core;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.samton.AppConfig;
import com.samton.IBenRobotSDK.interfaces.IWakeUpCallBack;
import com.samton.serialport.SerialUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 唤醒工具(更改为6麦唤醒)
 *     version: 1.0
 * </pre>
 */

public class IBenWakeUpUtil {
    /**
     * 串口操作工具类
     */
    private SerialUtil mSerialUtil;
    /**
     * 唤醒工具单例
     */
    private volatile static IBenWakeUpUtil instance = new IBenWakeUpUtil();
    /**
     * 唤醒回调
     */
    private volatile IWakeUpCallBack mIWakeUpCallBack;

    /**
     * 获取唤醒工具单例
     *
     * @return 唤醒工具实例
     */
    public static IBenWakeUpUtil getInstance() {
        return instance;
    }

    /**
     * 私有构造
     */
    @SuppressLint("CheckResult")
    private IBenWakeUpUtil() {
        // 设置串口号、波特率
        switch (AppConfig.PLANK_TYPE) {
            case "rk3288h":// 3288黑板
                mSerialUtil = new SerialUtil("/dev/ttyS3");
                break;
            case "rk3288l":// 3288蓝板
                mSerialUtil = new SerialUtil("/dev/ttyS4");
                break;
            case "rk3399l":// 3399蓝板
                mSerialUtil = new SerialUtil("/dev/ttyXRUSB0");
                break;
        }
        Observable.interval(0, 500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mSerialUtil == null) return;
                    // 读取数据
                    byte[] data = mSerialUtil.getDataByte();
                    // 不为空的话进行回写
                    if (data == null) return;
                    String result = new String(data);
                    int index = result.indexOf("angle");
                    if (index < 1) return;
                    result = result.substring(index + 6, index + 9).trim();
                    // 防止科大讯飞返回空字符串默认给角度值为0
                    if (TextUtils.isEmpty(result)) {
                        result = "0";
                    }
                    // 解析唤醒角度
                    int angle;
                    try {
                        angle = Integer.valueOf(result);
                    } catch (Exception e) {
                        angle = 0;
                    }
                    // 回调
                    if (mIWakeUpCallBack != null)
                        mIWakeUpCallBack.onWakeUp(angle);
                });
    }

    /**
     * 初始化唤醒工具类
     *
     * @param callBack 回调接口
     */
    public void setCallBack(IWakeUpCallBack callBack) {
        mIWakeUpCallBack = callBack;
    }

    /**
     * 加强第一个麦克风(冲向人的)
     */
    public void setBeam() {
        if (mSerialUtil == null) return;
        mSerialUtil.setData("BEAM0\n".getBytes());
    }

    /**
     * 停止语音唤醒监听
     */
    public void stopWakeUp() {
        mIWakeUpCallBack = null;
    }
}
