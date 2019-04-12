package com.samton.IBenRobotSDK.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.Surface;

import com.myntai.d.sdk.MYNTCamera;
import com.myntai.d.sdk.USBMonitor;
import com.myntai.d.sdk.bean.FrameData;

import java.util.Random;

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
    private MYNTCamera.IFrameCallback frameCallback;

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
     * 相机数据回调
     */
    public interface FrameCallback {
        void onFrame(FrameData color, FrameData depth);
    }

    /**
     * 关闭相机
     */
    public void closeCamera() {
        LogUtils.e("关闭相机");
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
            LogUtils.e("关闭相机异常" + e.toString());
        }
        if (frameCallback != null) {
            frameCallback = null;
        }
        if (mHandler != null) {
            mHandler.removeMessages(0x90890);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x90890:
                    if (mCamera != null) {
                        mCamera.connect();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 开启相机
     */
    public void openCamera(@NonNull final Surface colorSf, @NonNull final Surface depthSf) {
        LogUtils.e("开启相机");
        // 确保相机是关闭状态
        // closeCamera();
        LogUtils.e("USB监听开始注册");
        mUSBMonitor = new USBMonitor(activity, new USBMonitor.IUSBMonitorListener() {
            @Override
            public void didAttach(MYNTCamera myntCamera) {// 设备插入
                LogUtils.e("设备插入");
                if (mCamera == null) {
                    // 初始化相机
                    mCamera = myntCamera;
                }
                // 不能立即启动相机，间隔1s后再打开
                mHandler.sendEmptyMessageDelayed(0x90890, 1000);
            }

            @Override
            public void didDettach(MYNTCamera myntCamera) {// 设备拔出
                LogUtils.e("设备拔出");
                if (mCamera != null) {
                    mCamera.close();
                    mCamera.destroy();
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
                    mCamera.close();
                    mCamera.destroy();
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
        mCamera.setDepthType(MYNTCamera.DEPTH_DATA_11_BITS);
        // 设置图像回调
        if (frameCallback != null) {
            mCamera.setFrameCallback(frameCallback);
        }
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
     * 设置图像回调
     */
    public void setFrameCallback(MYNTCamera.IFrameCallback frameCallback) {
        LogUtils.e("设置数据回调监听");
        if (frameCallback != null) {
            this.frameCallback = frameCallback;
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
//    /**
//     * 获取区域范围内的所有点距离(降噪)
//     */
//    public int getDistanceValue(FrameData data, Rect rect) {
//        int left = rect.left, right = rect.right, top = rect.top, bottom = rect.bottom;
//        int distanceSum = 0, distanceNum = 0;
//        for (int i = left; i < right; i += 30) {
//            for (int j = top; j < bottom; j += 30) {
//                int distance = data.getDistanceValue(j, i);
//                // 距离为0mm或者大于5000mm
//                if (distance <= 0 || distance > 5 * 1000) {
//                    continue;
//                }
//                // 当前获取到的距离与当前已获取所有点的平均值相差1000mm以上
//                if (distanceSum != 0 && distanceNum != 0 &&
//                        Math.abs((distanceSum / distanceNum) - distance) > 1000) {
//                    continue;
//                }
//                distanceSum += distance;
//                distanceNum++;
//                LogUtils.e("获取点位置x:" + j + ",y:" + i + ",距离:" + distance);
//            }
//        }
//        LogUtils.e("获取点位置结束distanceSum：" + distanceSum + "，distanceNum：" + distanceNum);
//        if (distanceSum <= 0 || distanceNum <= 0) {
//            return 0;
//        }
//        return (distanceSum / distanceNum);
//    }

    /**
     * 获取区域范围内的随机20点距离(降噪)
     */
    public int getDistanceValue(Rect rect) {
        int left = rect.left, right = rect.right, top = rect.top, bottom = rect.bottom;
        int distanceSum = 0, distanceNum = 0;
        Random random = new Random();
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(right - left) + left;
            int y = random.nextInt(bottom - top) + top;
            int distance = getDistanceValue(x, y);
            LogUtils.e("获取点位置x:" + x + ",y:" + y + ",距离:" + distance);
            // 距离为0mm或者大于5000mm
            if (distance <= 0 || distance > 5 * 1000) {
                continue;
            }
            // 当前获取到的距离与当前已获取所有点的平均值相差1000mm以上
            if (distanceSum != 0 && distanceNum != 0 &&
                    Math.abs((distanceSum / distanceNum) - distance) > 1000) {
                continue;
            }
            distanceSum += distance;
            distanceNum++;
            LogUtils.e("有效点位置x:" + x + ",y:" + y + ",距离:" + distance);
        }
        LogUtils.e("获取点位置结束distanceSum：" + distanceSum + "，distanceNum：" + distanceNum);
        if (distanceSum <= 0 || distanceNum <= 0) {
            return 0;
        }
        return (distanceSum / distanceNum);
    }

//    /**
//     * 获取区域范围内的所有点距离(降噪)
//     *
//     * @return 返回所有有效值(除0)的平均数
//     */
//    public int getDistanceValue(Rect rect) {
//        int left = rect.left, right = rect.right, top = rect.top, bottom = rect.bottom;
//        int distanceSum = 0, distanceNum = 0;
//        for (int i = left; i < right; i += 30) {
//            for (int j = top; j < bottom; j += 30) {
//                int distance = getDistanceValue(j, i);
//                // 距离为0mm或者大于5000mm
//                if (distance <= 0 || distance > 5 * 1000) {
//                    continue;
//                }
//                // 当前获取到的距离与当前已获取所有点的平均值相差1000mm以上
//                if (distanceSum != 0 && distanceNum != 0 &&
//                        Math.abs((distanceSum / distanceNum) - distance) > 1000) {
//                    continue;
//                }
//                distanceSum += distance;
//                distanceNum++;
//                LogUtils.e("获取点位置x:" + j + ",y:" + i + ",距离:" + distance);
//            }
//        }
//        if (distanceSum <= 0 || distanceNum <= 0) {
//            return 0;
//        }
//        return distanceSum / distanceNum;
//    }
}
