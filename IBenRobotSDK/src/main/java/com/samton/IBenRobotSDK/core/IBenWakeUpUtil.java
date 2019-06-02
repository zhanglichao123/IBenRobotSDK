package com.samton.IBenRobotSDK.core;

import android.text.TextUtils;

import com.samton.IBenRobotSDK.interfaces.IWakeUpCallBack;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.samton.serialport.SerialUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
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

public final class IBenWakeUpUtil {
    /**
     * 串口操作工具类
     */
    private SerialUtil mSerialUtil;
    /**
     * 唤醒工具单例
     */
    private volatile static IBenWakeUpUtil instance = new IBenWakeUpUtil();
    /**
     * 读写回显定时器
     */
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    /**
     * 唤醒回调
     */
    private IWakeUpCallBack callBack = null;

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
    private IBenWakeUpUtil() {
        // 打开串口
        try {
            // 设置串口号、波特率，
            // mSerialUtil = new SerialUtil("/dev/ttyS3");//旧板子
            mSerialUtil = new SerialUtil("/dev/ttyS4");//新开发板子
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化唤醒工具类
     *
     * @param callBack 回调接口
     */
    public void setCallBack(@NonNull IWakeUpCallBack callBack) {
        this.callBack = callBack;
        // 清空计时器
        mCompositeDisposable.clear();
        DisposableObserver<Long> mReadObserver = getReadTimer();
        Observable.interval(0, 20, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mReadObserver);
        // 新加计时器
        mCompositeDisposable.add(mReadObserver);
    }

    /**
     * 加强第一个麦克风(冲向人的)
     */
    public void setBeam() {
        if (mSerialUtil != null) {
            mSerialUtil.setData("BEAM0\n".getBytes());
        }
    }

    /**
     * 停止语音唤醒监听
     */
    public void stopWakeUp() {
        // 置空回调
        callBack = null;
        // 清空回显定时器
        mCompositeDisposable.clear();
    }

    /**
     * 创建定时回写线程
     */
    private DisposableObserver<Long> getReadTimer() {
        return new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                if (mSerialUtil == null || callBack == null) return;
                // 读取数据
                byte[] data = mSerialUtil.getDataByte();
                // 不为空的话进行回写
                if (data != null) {
                    String result = new String(data);
                    int index = result.indexOf("angle");
                    if (index > 0) {
                        result = result.substring(index + 6, index + 9).trim();
                        // 防止科大讯飞返回空字符串默认给角度值为0
                        if (TextUtils.isEmpty(result)) {
                            result = "0";
                        }
                        // 解析唤醒角度
                        int angle;
                        try {
                            angle = Integer.valueOf(result);
                        } catch (Throwable throwable) {
                            angle = 0;
                        }
                        // 回调
                        callBack.onWakeUp(angle);
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.e(e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtils.e("onComplete");
            }
        };
    }
}
