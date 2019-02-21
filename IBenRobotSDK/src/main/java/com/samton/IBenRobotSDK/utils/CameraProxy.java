package com.samton.IBenRobotSDK.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PreviewCallback;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.io.IOException;

/**
 * 相机对象
 */
public class CameraProxy {
    private Camera mCamera = null;
    private CameraInfo mCameraInfo;
    private int mCameraId;
    private Context mContext = null;

    public CameraProxy(Context context) {
        mContext = context;
    }

    public Camera getCamera() {
        return mCamera;
    }

    /**
     * 释放相机，
     */
    public void releaseCamera() {
        if (null != mCamera) {
            //关闭数据回调
            mCamera.setPreviewCallback(null);
            //关闭surfaceView的显示
            mCamera.stopPreview();
            //释放相机
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 打开相机
     *
     * @param facing
     * @return
     */
    public boolean openCamera(int facing) {
        if (mCamera != null) {
            releaseCamera();
        }

        CameraInfo info = new CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                try {
                    mCamera = Camera.open(i);
//                    List<Camera.Size> supportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
//                    for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
//                        Log.e("Liu", "openCamera: height" + supportedPreviewSize.height + " width:" + supportedPreviewSize.width);
//                    }
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    mCamera = null;
                    return false;
                }
                mCameraInfo = info;
                return true;
            }
        }
        return false;
    }

    /**
     * 设置预览
     *
     * @param holder
     * @param callBack
     * @throws IOException
     */
    public void startPreview(SurfaceHolder holder, PreviewCallback callBack) throws IOException {
        if (null != mCamera) {
            try {
                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                parameters.setPreviewFormat(ImageFormat.NV21);
                if (parameters.getSupportedFocusModes().contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
                mCamera.setParameters(parameters);
                for (int i = 0; i < 2; i++) {
                    // Make sure there is always a buffer waiting for data,
                    // while another one may be blocked in the callback
                    mCamera.addCallbackBuffer(new byte[640 * 480 * 3 / 2]);
                }
                mCamera.setPreviewCallbackWithBuffer(callBack);
                setCameraDisplayOrientation();
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (Exception e) {
                LogUtils.e(e.toString());
            }

        }
    }

    public void addCallbackBuffer(byte[] data) {
        if (mCamera != null) {
            mCamera.addCallbackBuffer(data);
        }
    }

    public int getOrientation() {
        if (mCameraInfo == null) {
            return 0;
        }
        return mCameraInfo.orientation;
    }

    public boolean isFrontCamera() {
        if (mCameraInfo == null) {
            return false;
        }
        return mCameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
    }

    private void setCameraDisplayOrientation() {
        mCamera.setDisplayOrientation(180);
    }

    public int getRotate() {
        int rotation = ((Activity) mContext).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        result = (mCameraInfo.orientation - degrees + 360) % 360;
        return result;
    }
}
