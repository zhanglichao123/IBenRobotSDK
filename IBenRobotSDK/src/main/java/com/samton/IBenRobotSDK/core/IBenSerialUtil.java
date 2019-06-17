package com.samton.IBenRobotSDK.core;

import com.samton.IBenRobotSDK.interfaces.ISerialCallBack;
import com.samton.serialport.SerialUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
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

public final class IBenSerialUtil {
    /**
     * 串口操作工具类
     */
    private SerialUtil mSerialUtil;
    /**
     * 唤醒工具单例
     */
    private volatile static IBenSerialUtil instance = new IBenSerialUtil();
    /**
     * 串口回调
     */
    private Disposable mReadSubscribe;

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
    private IBenSerialUtil() {
        // 打开串口
        try {
            // 设置串口号、波特率，
            // mSerialUtil = new SerialUtil("/dev/ttyS0");// 旧板子
            // mSerialUtil = new SerialUtil("/dev/ttyXRUSB2");// 3399主板
            mSerialUtil = new SerialUtil("/dev/ttyS1");//新开发版
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置回调
     *
     * @param callBack 回调
     */
    public void setCallBack(ISerialCallBack callBack) {
        // 清空计时器
        removeCallBack();
        mReadSubscribe = Observable.interval(0, 20, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mSerialUtil == null || callBack == null) return;
                    // 读取数据
                    byte[] data = mSerialUtil.getDataByte();
                    // 不为空的话进行回写
                    if (data != null) {
                        String result = new String(data);
                        int startIndex = result.indexOf("{");
                        int endIndex = result.indexOf("}");
                        if (startIndex != -1 && endIndex != -1) {
                            callBack.onReadData(result);
                        }
                    }
                });
    }

    /**
     * 移除回调监听
     */
    public void removeCallBack() {
        if (mReadSubscribe != null && !mReadSubscribe.isDisposed()) {
            mReadSubscribe.dispose();
            mReadSubscribe = null;
        }
    }

    /**
     * 向串口写数据
     *
     * @param msg 要写的数据
     */
    public void sendData(String msg) {
        if (mSerialUtil != null)
            mSerialUtil.setData(msg.getBytes());
    }

}
