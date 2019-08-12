package com.samton.IBenRobotSDK.core;

import android.annotation.SuppressLint;

import com.samton.AppConfig;
import com.samton.IBenRobotSDK.interfaces.ISerialCallBack;
import com.samton.serialport.SerialUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *   author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/01/19 13:53
 *   desc    : 小笨智能SDK - 串口工具
 *   version : 1.0
 * </pre>
 */

public class IBenSerialUtil {
    /**
     * 串口操作工具类
     */
    private SerialUtil mSerialUtil;
    /**
     * 唤醒工具单例
     */
    private volatile static IBenSerialUtil instance = new IBenSerialUtil();
    /**
     * 唤醒回调监听
     */
    private volatile ISerialCallBack mISerialCallBack;

    /**
     * 获取串口工具单例
     *
     * @return 串口工具实例
     */
    public static IBenSerialUtil getInstance() {
        return instance;
    }

    /**
     * 私有构造
     */
    @SuppressLint("CheckResult")
    private IBenSerialUtil() {
        // 设置串口号、波特率
        switch (AppConfig.PLANK_TYPE) {
            case "rk3288h":// 3288黑板
                mSerialUtil = new SerialUtil("/dev/ttyS0");
                break;
            case "rk3288l":// 3288蓝板
                mSerialUtil = new SerialUtil("/dev/ttyS1");
                break;
            case "rk3399l":// 3399蓝板
                mSerialUtil = new SerialUtil("/dev/ttyXRUSB2");
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
                    if (data == null || data.length < 1) return;
                    String result = new String(data);
                    int startIndex = result.indexOf("{");
                    int endIndex = result.indexOf("}");
                    if (startIndex == -1 || endIndex == -1) return;
                    if (mISerialCallBack != null)
                        mISerialCallBack.onReadData(result);
                });
    }

    /**
     * 设置回调
     *
     * @param callBack 回调
     */
    public void setCallBack(ISerialCallBack callBack) {
        mISerialCallBack = callBack;

    }

    /**
     * 移除回调监听
     */
    public void removeCallBack() {
        mISerialCallBack = null;
    }

    /**
     * 向串口写数据
     *
     * @param msg 要写的数据
     */
    public void sendData(String msg) {
        if (mSerialUtil == null) return;
        mSerialUtil.setData(msg.getBytes());
    }
}
