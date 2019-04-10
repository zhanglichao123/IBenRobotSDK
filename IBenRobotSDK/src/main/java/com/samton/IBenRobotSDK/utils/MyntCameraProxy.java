package com.samton.IBenRobotSDK.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.myntai.d.sdk.MYNTCamera;
import com.myntai.d.sdk.USBMonitor;
import com.myntai.d.sdk.bean.FrameData;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author: xkbai
 * e-mail: xkkbai@163.com
 * time: 2019/3/15
 * desc: 使用小觅摄像头的工具
 */
public class MyntCameraProxy {
    private String TAG = "MyntCameraProxy";
    private static MyntCameraProxy instance;
    private Activity activity;

    private USBMonitor mUSBMonitor;
    private MYNTCamera mCamera;
    private IMyntCallBack iMyntCallBack;

    public MyntCameraProxy(Activity activity) {
        this.activity = activity;
    }

    public static MyntCameraProxy getInstance(Activity activity) {
        if (instance == null) {
            instance = new MyntCameraProxy(activity);
        }
        return instance;
    }

    /**
     * 关闭相机
     */
    public void closeCamera() {
        LogUtils.d("关闭相机");
        try {
            if (mCamera != null) {
                mCamera.close();
                mCamera.destroy();
                mCamera = null;
            }
            if (mUSBMonitor != null) {
                mUSBMonitor.unregister();
                mUSBMonitor.destroy();
                mUSBMonitor = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.d("关闭相机异常" + e.toString());
        }
        if (iMyntCallBack != null) {
            iMyntCallBack = null;
        }
    }

    /**
     * 开启相机
     */
    public void openCamera(@NonNull final Surface colorSf, @NonNull final Surface depthSf) {
        LogUtils.d("开启相机");
        // 确保相机是关闭状态
        // closeCamera();
        LogUtils.d("USB监听开始注册");
        mUSBMonitor = new USBMonitor(activity, new USBMonitor.IUSBMonitorListener() {
            @Override
            public void didAttach(MYNTCamera myntCamera) {// 设备插入
                LogUtils.d("设备插入");
                if (mCamera == null) {
                    // 初始化相机
                    mCamera = myntCamera;
                }
                mCamera.connect();
            }

            @Override
            public void didDettach(MYNTCamera myntCamera) {// 设备拔出
                LogUtils.d("设备拔出");
                if (mCamera != null) {
                    mCamera.close();
                    mCamera.destroy();
                    mCamera = null;
                }
            }

            @Override
            public void didConnectedCamera(MYNTCamera myntCamera) {// 连接成功
                LogUtils.d("连接成功");
                if (mCamera == null) {
                    mCamera = myntCamera;
                }
                // 开始获取数据
                startGetData(colorSf, depthSf);
            }

            @Override
            public void didDisconnectedCamera(MYNTCamera myntCamera) {// 连接断开
                LogUtils.d("连接断开");
                if (mCamera != null) {
                    mCamera.close();
                    mCamera.destroy();
                    mCamera = null;
                }
            }
        });
        mUSBMonitor.register();
        LogUtils.d("USB监听注册成功");
    }

    /**
     * 开始获取数据
     */
    private void startGetData(@NonNull Surface colorSf, @NonNull Surface depthSf) {
        if (mCamera == null) {
            return;
        }
        mCamera.open();
        // 彩色图预览相关的对象
        mCamera.setPreviewDisplay(colorSf, MYNTCamera.Frame.Color);
        // 深度图预览相关的对象
        mCamera.setPreviewDisplay(depthSf, MYNTCamera.Frame.Depth);
        // 设置预览尺寸
        mCamera.setPreviewSize(720);
        // 设置深度类型
        mCamera.setDepthType(MYNTCamera.DEPTH_DATA_11_BITS);
        // 预览框宽高
        final int width = mCamera.getPreviewWidth(), height = mCamera.getPreviewHeight();
        // 设置图像回调
        mCamera.setFrameCallback(new MYNTCamera.IFrameCallback() {
            @Override
            public void onFrame(FrameData data) {
                iMyntCallBack.onFrame(data, width, height);
            }
        });
        // 开始启动预览
        mCamera.start(MYNTCamera.Source.ALL);
    }

    /**
     * 获取相机是否插入
     */
    public boolean isAttached() {
        return mCamera != null;
    }

    /**
     * 获取相机是否连接
     */
    public boolean isConnected() {
        if (mCamera == null) {
            return false;
        } else {
            return mCamera.isOpen();
        }
    }

    /**
     * 获取指定点的距离
     */
    public int getDistanceValue(int index) {
        if (mCamera == null) {
            return 0;
        } else {
            return mCamera.getDistanceValue(index);
        }
    }

    /**
     * 获取指定点的距离
     */
    public int getDistanceValue(int x, int y) {
        if (mCamera == null) {
            return 0;
        } else {
            return getDistanceValue(mCamera.getPreviewWidth() * (y - 1) + x);
        }
    }

    /**
     * 获取指定点的距离(降噪)
     */
    public int denoiseDistanceValue(int x, int y) {
        if (mCamera == null) {
            return 0;
        } else {
            int dvc = getDistanceValue(x, y);       // 标准
            // 获取标准点上下左右10像素的点距离
            int dvt = getDistanceValue(x, y - 10);  // 上
            int dvb = getDistanceValue(x, y + 10);  // 下
            int dvl = getDistanceValue(x - 10, y);  // 左
            int dvr = getDistanceValue(x + 10, y);  // 右
            List<Integer> distances = Arrays.asList(dvc, dvt, dvb, dvl, dvr);
            Collections.sort(distances, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    if (o1 > o2) {
                        return 1;
                    }
                    if (o1 == o2) {
                        return 0;
                    }
                    return -1;
                }
            });
            distances.remove(0);
            distances.remove(distances.size() - 1);
            LogUtils.e("获取距离：" + distances.toString());
            int dvSum = 0;
            for (Integer distance : distances) {
                dvSum = distance + dvSum;
            }
            LogUtils.e("距离和：" + dvSum);
            if (dvSum == 0) {
                return 0;
            } else {
                return dvSum / distances.size();
            }
        }
    }

    /**
     * 设置图像回调
     */
    public void setFrameCallBack(IMyntCallBack iMyntCallBack) {
        LogUtils.d("设置数据回调监听");
        if (iMyntCallBack != null) {
            this.iMyntCallBack = iMyntCallBack;
        }
    }

    /**
     * 相机相关回调
     */
    public interface IMyntCallBack {
        // 图像数据，预览框宽高
        void onFrame(FrameData data, int width, int height);
    }
}
