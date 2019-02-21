package com.samton.IBenRobotSDK.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import com.megvii.facepp.sdk.Facepp;
import com.samton.IBenRobotSDK.data.Constants;
import com.samton.IBenRobotSDK.data.RouseBean;
import com.samton.IBenRobotSDK.face.BitmapUtil;
import com.samton.IBenRobotSDK.face.FaceManager;
import com.samton.IBenRobotSDK.net.HttpRequest;
import com.samton.IBenRobotSDK.net.HttpUtil;
import com.samton.IBenRobotSDK.utils.CameraProxy;
import com.samton.IBenRobotSDK.utils.CameraToBitmap;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.samton.IBenRobotSDK.utils.SPUtils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * @author : xkbai
 * e-mail : xkkbai@163.com
 * time   : 2019/2/19
 * desc   : 机器人主动唤醒工具类
 */
public class IBenRouseSDK implements Camera.PreviewCallback {
    private Context mContext;
    // 主动唤醒回调
    private IBenRouseCallback mIBenRouseCallback;
    // 单例模式返回
    private static IBenRouseSDK mInstance;
    // 动态添加预览窗口
    private RelativeLayout mParentView;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    // 获取人脸特征的频率时间间隔
    private long time = 0;
    private long currentTime = 0;
    // 是否正在识别
    private boolean isFaceCheck = false;
    // 相机对象
    private CameraProxy mCameraProxy = null;
    // 连续检测到正脸的次数
    private int detectedSuccessCount = 0;
    // 是否开启主动欢迎或主动迎宾
    private int isOpenAndType = 0;

    public IBenRouseSDK(Context context) {
        this.mContext = context;
    }

    /**
     * 获取主动唤醒实例
     */
    public static IBenRouseSDK getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new IBenRouseSDK(context);
        }
        return mInstance;
    }

    /**
     * 初始化相机，添加SufaceView
     */
    public void initCamera(RelativeLayout mParentView, IBenRouseCallback callback) {
        LogUtils.d("主动唤醒-初始化相机");
        this.mParentView = mParentView;
        this.mIBenRouseCallback = callback;
        // 动态初始化View
        mSurfaceView = new SurfaceView(mContext);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(1, 1);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mSurfaceView.setLayoutParams(lp);
        mParentView.addView(mSurfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mShCallback);
    }

    /**
     * 打开相机
     */
    private void openCamera(SurfaceHolder mHolder) {
        if (mCameraProxy != null) {
            return;
        }
        LogUtils.d("主动唤醒-打开相机");
        mCameraProxy = new CameraProxy(mContext);
        if (mCameraProxy.openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
            try {
                mCameraProxy.startPreview(mHolder, this);
            } catch (IOException e) {
                LogUtils.d("主动唤醒：打开相机失败" + e.toString());
                // 注销相机组件
                cancelCamera();
                // 间隔1s后重新尝试打开
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(10001);
                    }
                }, 1000);
            }
        }
    }

    /**
     * 关闭并注销相机组件
     */
    public void cancelCamera() {
        isFaceCheck = false;
        time = 0;
        currentTime = 0;
        if (mCameraProxy != null) {
            mCameraProxy.releaseCamera();
            mCameraProxy = null;
        }
        if (mParentView != null && mSurfaceView != null) {
            mParentView.removeView(mSurfaceView);
        }
    }

    /**
     * 预览结果回调
     */
    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        // 正在识别，正在上传
        if (isFaceCheck) {
            return;
        }
        mCameraProxy.addCallbackBuffer(bytes);
        currentTime = System.currentTimeMillis();
        if (currentTime - time > 500) {
            isFaceCheck = true;
            final Bitmap mSourceBitmap = CameraToBitmap.decodeToBitMap(bytes, camera);
            // 因为图片会发生旋转，因此要对图片进行旋转到和手机在一个方向上
            Bitmap bitmap = CameraToBitmap.rotateMyBitmap(mSourceBitmap);
            mSourceBitmap.recycle();
            Facepp.Face[] faces = FaceManager.getInstance().CheckFace(bytes, bitmap.getWidth(), bitmap.getHeight());
            if (faces == null || faces.length <= 0) {
                // 没有检测到人脸
                detectedSuccessCount = 0;
            } else {
                // 检测到有人脸，本地判断有没有正脸
                for (Facepp.Face face : faces) {
                    // 正脸角度为0，超过30度判定为非
                    if (Math.abs(face.pitch) > 30) {
                        // 没有检测到正脸
                        detectedSuccessCount = 0;
                    } else {
                        float distance = 2.0f;
                        //TODO 在这里要进行距离判断，2米以内的执行主动唤醒，4-6米的执行主动欢迎
                        if (distance <= 2) {
                            // TODO 执行主动唤醒
                            initiativeRouse(bitmap);
                        } else if (distance >= 4 && distance <= 6) {
                            // TODO 执行主动欢迎、迎宾
                            initiativeWelcomeAndMeeting(distance, bitmap);
                        }
                        break;
                    }
                }
            }
            time = currentTime;
            isFaceCheck = false;
        }
    }

    /**
     * 主动唤醒
     */
    private void initiativeRouse(Bitmap bitmap) {
        // 有正脸，开始计数
        detectedSuccessCount++;
        // TODO 连续4次检测到有正脸，上传后台识别
        if (detectedSuccessCount >= 2) {
            saveAndUpImage(bitmap);
            detectedSuccessCount = 0;
        }
    }

    /**
     * 主动欢迎，主动迎宾
     */
    private void initiativeWelcomeAndMeeting(float distance, Bitmap bitmap) {
        //  判断是否开启主动欢迎
        if (isOpenAndType == 0) {
            return;
        }

        if (isOpenAndType == 1) {// 主动欢迎
            // TODO 识别对应人的身份

            // TODO 播报主动欢迎语

        } else {                 // 主动迎宾
            // TODO 识别对应人的身份

            // TODO 播报主动欢迎语

            // TODO 行走到人面前
        }
    }

    /**
     * 设置是否开启主动欢迎
     * 0全部关闭，1开启主动欢迎，2开启主动迎宾
     */
    public void setIsOpenWelcome(int isOpenAndType) {
        this.isOpenAndType = isOpenAndType;
    }

    /**
     * 计算旋转角度并播报
     */
    private void rotateAndHello(Bitmap bitmap, RouseBean bean) {
        if (bitmap == null || bean == null) {
            throw new NullPointerException("bitmap is null or bean is null");
        }
        // 最正的人脸数据
        RouseBean.FaceRectangleBean rectangle = bean.getFaceRectangle();
        if (rectangle == null) {
            throw new NullPointerException("rectangle is null");
        }
        // 照片宽高
        int bitmapW = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        // 人脸位置
        int faceX = rectangle.getLeft();
        int faceY = rectangle.getTop();
        // 人脸框宽高
        int faceW = rectangle.getWidth();
        int faceH = rectangle.getHeight();
        // 照片正中线
        int bitmapCenteW = bitmapW / 2;
        // 人脸中间点
        int faceCenterX = faceX + (faceW / 2);
        int faceCenterY = faceY + (faceH / 2);
        // 三角形水平线长度
        int trigonLineX = bitmapCenteW - faceCenterX;
        // 三角形垂直线长度
        int trigonLineY = bitmapH - faceCenterY;
        // 计算弧度换算出角度，假设拍摄照片的镜头角度是60度
        double angle = Math.atan2(trigonLineX, trigonLineY) * 55.0 / Math.PI;
        LogUtils.d("主动唤醒-需要转身角度为：" + angle);
        // 通知页面跳转至ChatFragment
        if (mIBenRouseCallback == null) {
            throw new NullPointerException("mIBenRouseCallback is null");
        } else {
            mIBenRouseCallback.rouseDataCallBack(bean.getSpeach(), angle);
        }
//        // 在充电桩时不转身，正对面不需要旋转
//        if (Math.abs(bitmapCenteW - faceCenterX) > 10) {
//            if (bitmapCenteW > faceCenterX) {
//                // ServerService.controlRotate(angle);
//            } else if (bitmapCenteW < faceCenterX) {
//                // ServerService.controlRotate(-angle);
//            }
//        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 10001:
                    // 重新初始化相机组件
                    if (mParentView != null && mIBenRouseCallback != null) {
                        initCamera(mParentView, mIBenRouseCallback);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 处理数据回调接口
     */
    public interface IBenRouseCallback {
        /**
         * @param speakMsg    播报语
         * @param rotateAngle 旋转角度
         */
        void rouseDataCallBack(String speakMsg, double rotateAngle);
    }

    /**
     * 初始化回调
     */
    private SurfaceHolder.Callback mShCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // 初始化完成，打开相机
            openCamera(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            holder.getSurface().release();
        }
    };

    /**
     * 上传识别到人脸的图片
     */
    private void saveAndUpImage(final Bitmap bitmap) {
        final String path = BitmapUtil.saveBitmapToSd(bitmap);
        if (TextUtils.isEmpty(path)) {
            LogUtils.d("主动唤醒-图片地址为空");
            return;
        }
        LogUtils.d("主动唤醒-开始上传图片");
        // 开始上传，注销相机组件
        cancelCamera();
        final File file = new File(path);
        String appKey = SPUtils.getInstance().getString(Constants.ROBOT_APP_KEY);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part photo = MultipartBody.Part.createFormData("fimage", file.getName(), requestBody);
        HttpUtil.getInstance().create(HttpRequest.class)
                .recognitionFace(photo, RequestBody.create(null, appKey))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<RouseBean>() {
                    @Override
                    public void accept(@NonNull RouseBean bean) throws Exception {
                        LogUtils.d("主动唤醒-上传成功" + bean.getRs());
                        if (bean.getRs().equals("1")) {
                            rotateAndHello(bitmap, bean);
                        } else {
                            // 重新初始化相机组件
                            mHandler.sendEmptyMessage(10001);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtils.d("主动唤醒-上传异常" + throwable.toString());
                        // 重新初始化相机组件
                        mHandler.sendEmptyMessage(10001);
                    }
                });
    }
}
