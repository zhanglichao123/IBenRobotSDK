package com.samton.IBenRobotSDK.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.myntai.d.sdk.MYNTCamera;
import com.myntai.d.sdk.USBMonitor;
import com.myntai.d.sdk.bean.FrameData;

/**
 * @author: xkbai
 * e-mail: xkkbai@163.com
 * time: 2019/3/15
 * desc: 使用小觅摄像头的工具
 */
public class MyntCameraProxy {
    private String TAG = "MyntCameraProxy";
    private static MyntCameraProxy instance;
    private Context context;

    private USBMonitor mUSBMonitor;
    private MYNTCamera mCamera;
    private IMyntCallBack iMyntCallBack;

    public MyntCameraProxy(Context context) {
        this.context = context;
    }

    public static MyntCameraProxy getInstance(Context context) {
        if (instance == null) {
            instance = new MyntCameraProxy(context);
        }
        return instance;
    }

    /**
     * 关闭相机
     */
    public void closeCamera() {
        LogUtils.e("关闭相机");
        if (mCamera != null) {
            mCamera.stop();
            mCamera.close();
            mCamera = null;
        }
        if (mUSBMonitor != null) {
            mUSBMonitor.unregister();
            mUSBMonitor.destroy();
            mUSBMonitor = null;
        }
        if (iMyntCallBack != null) {
            iMyntCallBack = null;
        }
    }

    /**
     * 开启相机
     */
    public void openCamera(@NonNull final Surface colorSf, @NonNull final Surface depthSf) {
        LogUtils.e("开启相机");
        // 确保相机是关闭状态
        // closeCamera();
        LogUtils.e("USB监听开始注册");
        mUSBMonitor = new USBMonitor(context, new USBMonitor.IUSBMonitorListener() {
            @Override
            public void didAttach(MYNTCamera myntCamera) {// 设备插入
                LogUtils.e("设备插入");
                if (mCamera == null) {
                    // 初始化相机
                    mCamera = myntCamera;
                }
                mCamera.connect();
            }

            @Override
            public void didDettach(MYNTCamera myntCamera) {// 设备拔出
                LogUtils.e("设备拔出");
                if (mCamera != null) {
                    mCamera.stop();
                    mCamera.close();
                    mCamera = null;
                }
            }

            @Override
            public void didConnectedCamera(MYNTCamera myntCamera) {// 连接成功
                LogUtils.e("连接成功");
                if (mCamera == null) {
                    mCamera = myntCamera;
                }
                // 开始获取数据
                startGetData(colorSf, depthSf);
            }

            @Override
            public void didDisconnectedCamera(MYNTCamera myntCamera) {// 连接断开
                LogUtils.e("连接断开");
                if (mCamera != null) {
                    mCamera.stop();
                    mCamera.close();
                    mCamera = null;
                }
            }
        });
        mUSBMonitor.register();
        LogUtils.e("USB监听注册成功");
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
        mCamera.setDepthType(MYNTCamera.DEPTH_DATA_8_BITS);
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
            return mCamera.isConnected();
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
     * 设置图像回调
     */
    public void setFrameCallBack(IMyntCallBack iMyntCallBack) {
        LogUtils.e("设置数据回调监听");
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
