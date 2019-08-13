package com.samton.IBenRobotSDK.face;

import android.content.Context;

import com.megvii.facepp.sdk.Facepp;
import com.megvii.licensemanager.sdk.LicenseManager;
import com.samton.AppConfig;
import com.samton.IBenRobotSDK.utils.LogUtils;


/**
 * <pre>
 *     author : lhg
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/06/07
 *     desc   : 所有人脸识别管理器
 *     version: 1.0
 * </pre>
 */
public class FaceManager {

    public static FaceManager manager;
    public Facepp facepp;
    public boolean isCheckFaceLicense = false;//证书是否授权
    private LicenseManager mLicenseManager;

    private FaceManager() {
    }

    public synchronized static FaceManager getInstance() {
        if (null == manager) {
            synchronized (FaceManager.class) {
                if (null == manager) {
                    manager = new FaceManager();
                }
            }
        }
        return manager;
    }

    /**
     * 检查 License 合法性
     * 需要把model 放在assets  资源文件夹下
     *
     * @param context  上下文对象
     * @param callBack 回调
     */
    public void CheckFaceLicense(Context context, int faceppRawId,
                                 final FaceCheckLicenseCallBack callBack) {
        mLicenseManager = new LicenseManager(context);
        mLicenseManager.setExpirationMillis(Facepp.getApiExpirationMillis(
                context, FaceUtil.getFileContent(context, faceppRawId)));
        String uuid = FaceUtil.getUUIDString();
//        String uuid = "MmE1Nzk5NmEtZDYyNS00MTY4LTgyYjgtZTI2MDgzOGZiYzYz";
        long apiName = Facepp.getApiName();
        mLicenseManager.setAuthTimeBufferMillis(0);
        mLicenseManager.takeLicenseFromNetwork(AppConfig.CN_LICENSE_URL, uuid, AppConfig.FACE_KEY, AppConfig.FACE_SECRET, apiName,
                //正式  申请365
                AppConfig.FACE_TIME, "Landmark", String.valueOf(AppConfig.FACE_TIME), new LicenseManager.TakeLicenseCallback() {
                    @Override
                    public void onSuccess() {
                        isCheckFaceLicense = true;
                        callBack.onCheckSuccess();
                    }

                    @Override
                    public void onFailed(int i, byte[] bytes) {
                        isCheckFaceLicense = false;
                        String s = " ... no error message";
                        if (null != bytes) {
                            s = new String(bytes);
                        }
                        callBack.onCheckFail(i + "   " + s);
                    }
                });
    }

    /**
     * 初始化检测人脸
     *
     * @param context
     */
    public void initCheckFace(Context context, int faceppRawId) {
        if (!isCheckFaceLicense) {
            return;
        }
        try {
            if (null == facepp) {
                facepp = new Facepp();
                String message = facepp.init(context,
                        FaceUtil.getFileContent(context, faceppRawId));
                setFaceConfig();
                LogUtils.e("initCheckFace: " + message);
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    /**
     * 初始化相机后 设置参数
     */
    public void setFaceConfig() {
        if (!isCheckFaceLicense) {
            return;
        }
        if (null == facepp) {
            return;
        }
        Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
        faceppConfig.interval = 25;
        faceppConfig.minFaceSize = 50;
        faceppConfig.roi_left = 0;
        faceppConfig.roi_top = 0;
        faceppConfig.roi_right = 640;
        faceppConfig.roi_bottom = 480;
        faceppConfig.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_NORMAL;
        faceppConfig.rotation = 0;
        facepp.setFaceppConfig(faceppConfig);
    }

    /**
     * 检测人脸数据
     *
     * @param imageData
     * @param width
     * @param height
     * @return
     */
    public Facepp.Face[] CheckFace(byte[] imageData, int width, int height) {
        if (!isCheckFaceLicense) {
            return null;
        }
        try {
            if (null != facepp) {
                return facepp.detect(imageData, width, height, Facepp.IMAGEMODE_NV21);
            }
        } catch (Exception e) {
//            LogUtils.e(e.toString());
            return null;
        }

        return null;
    }

    /**
     * 比较两张图片
     *
     * @param imageData  第一张图片的字节数组
     * @param imageData2 第二张图片的字节数组
     * @return
     */
    public double FaceCommpare(byte[] imageData, byte[] imageData2) {
        if (null != facepp) {
            if (null != imageData && null != imageData2) {
                return facepp.faceCompare(imageData, imageData2);
            }
            return -1;
        }
        return -1;
    }

    public void release() {
        if (facepp != null) {
            facepp.release();
            facepp = null;
        }
    }
}
