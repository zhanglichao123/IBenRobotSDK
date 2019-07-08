package com.samton.IBenRobotSDK.face;

import android.content.Context;
import android.util.Log;

import com.megvii.facepp.sdk.Facepp;
import com.megvii.licensemanager.sdk.LicenseManager;
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

    private static FaceManager manager;
    public Facepp facepp;
    private boolean isCheckFaceLicense = false;//证书是否授权

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
        LicenseManager licenseManager = new LicenseManager(context);
        licenseManager.setExpirationMillis(Facepp.getApiExpirationMillis(
                context, FaceUtil.getFileContent(context, faceppRawId)));
        String uuid = FaceUtil.getUUIDString(context);
        long apiName = Facepp.getApiName();
        licenseManager.setAuthTimeBufferMillis(0);
        licenseManager.takeLicenseFromNetwork(FaceKey.CN_LICENSE_URL, uuid, FaceKey.API_KEY, FaceKey.API_SECRET, apiName,
                //正式  申请365
                FaceKey.FACE_TIME, "Landmark", FaceKey.FACE_TIMES, new LicenseManager.TakeLicenseCallback() {
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
        faceppConfig.roi_right = FaceConstants.PREVIEW_WIDTH;
        faceppConfig.roi_bottom = FaceConstants.PREVIEW_HEIGHT;
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
                Facepp.Face[] detect = facepp.detect(imageData, width, height, Facepp.IMAGEMODE_NV21);
                return detect;
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
        Log.e("Liu", "FaceCommpare:imageData " + imageData.length / 1024);
        Log.e("Liu", "FaceCommpare:imageData2 " + imageData2.length / 1024);
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
