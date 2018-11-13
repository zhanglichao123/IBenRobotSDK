package com.samton.IBenRobotSDK.core;

import android.util.Log;

import com.samton.IBenRobotSDK.data.SensorData;
import com.samton.IBenRobotSDK.interfaces.ISensorCallBack;
import com.samton.serialport.SensorUtil;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *   author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/01/19 13:53
 *   desc    : 小笨智能SDK - 传感器 参数串口工具类
 *   version : 1.0
 * </pre>
 */

public final class IBenSensorUtil {
    /**
     * 串口操作工具类
     */
    private SensorUtil mSerialUtil = null;
    /**
     * 唤醒工具单例
     */
    private volatile static IBenSensorUtil instance = new IBenSensorUtil();
    /**
     * 读写回显定时器
     */
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    /**
     * 串口回调
     */
    private ISensorCallBack mCallBack = null;

    /**
     * 获取串口工具单例
     *
     * @return 串口工具实例
     */
    public static IBenSensorUtil getInstance() {
        return instance;
    }

    /**
     * 私有构造
     */
    private IBenSensorUtil() {
        // 打开串口
        try {
            // 设置串口号、波特率，
            mSerialUtil = new SensorUtil("/dev/ttyS2");//新开发版
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置回调
     *
     * @param callBack 回调
     */
    public void setCallBack(ISensorCallBack callBack) {
        mCallBack = null;
        mCallBack = callBack;
        // 清空计时器
        mCompositeDisposable.clear();
        DisposableObserver<Long> mReadObserver = getReadTimer();
        Observable.interval(0, 200, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io()).subscribe(mReadObserver);
        // 新加计时器
        mCompositeDisposable.add(mReadObserver);
    }

    private static String hexString = "0123456789ABCDEF";

    private static String encode(byte[] bytes) {
        // 根据默认编码获取字节数组
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        // 将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
            if (i != bytes.length - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 创建定时回写线程
     */
    private DisposableObserver<Long> getReadTimer() {
        return new DisposableObserver<Long>() {
            @Override
            public void onNext(Long aLong) {
                // 读取数据
                byte[] datas = mSerialUtil.getDataByte();
                // 不为空的话进行回写
                if (datas != null) {
                    String encode = encode(datas);
                    String[] dataStr = encode.split(",");
                    int[] data = new int[dataStr.length];
                    try {
                        for (int i = 0; i < dataStr.length; i++) {
                            data[i] = Integer.parseInt(dataStr[i], 16);
                        }
                        SensorData sensor = new SensorData();
                        int aa = 0;
                        for (int i = 0; i < 16; i++) {
                            aa = aa + data[i];
                            Log.d("IBen", "onNext: " + data[i]);
                        }
                        boolean isRight; //表示正负  false 负 true 正的
                        isRight = ((data[13] >> 6) & 1) != 1;
                        Log.d("IBen", "onNext: 数据验证 相等则验证通过 " + aa + "   " + data[16]);
                        Log.d("二氧化碳", "onNext: " + (data[2] * 256 + data[3]));
                        Log.d("甲醛", "onNext: " + (data[4] * 256 + data[5]));
                        Log.d("TVOC", "onNext: " + (data[6] * 256 + data[7]));
                        Log.d("PM2.5", "onNext: " + (data[8] * 256 + data[9]));
                        Log.d("PM10", "onNext: " + (data[10] * 256 + data[11]));
                        Log.d("温度整数", "onNext: " + Math.abs(data[12]));
                        Log.d("温度小说", "onNext: " + (double) Math.abs(data[13]) / 10);
                        Log.d("湿度整数", "onNext: " + Math.abs(data[14]));
                        Log.d("湿度小数", "onNext: " + (double) Math.abs(data[15]) / 10);
                        Log.d("IBen", "onNext: *************************************");
                        if ((aa - (aa >> 8 << 8)) == data[16]) {
                            sensor.setLegal(true);
                        }
                        sensor.setData(datas);
                        sensor.setCarbonDioxide((data[2] * 256 + data[3]));
                        sensor.setFormaldehyde((data[4] * 256 + data[5]));
                        sensor.setTVOC((data[6] * 256 + data[7]));
                        sensor.setPM2_5((data[8] * 256 + data[9]));
                        sensor.setPM10((data[10] * 256 + data[11]));
                        if (isRight) {
                            sensor.setTemperature((data[12] + (double) data[13] / 10));
                            sensor.setHumidity((data[14] + (double) data[15] / 10));
                        } else {
                            sensor.setTemperature(-(data[12] + (double) data[13] / 10));
                            sensor.setHumidity(-(data[14] + (double) data[15] / 10));
                        }

                        if (mCallBack != null) {
                            mCallBack.onReadData(sensor);
                        }
                    } catch (Exception e) {
                        Log.e("IBen", "onNext: " + e.toString());
                    }

                }
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
    }

}
