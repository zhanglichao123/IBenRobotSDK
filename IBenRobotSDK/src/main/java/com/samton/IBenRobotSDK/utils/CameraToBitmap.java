package com.samton.IBenRobotSDK.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import java.io.ByteArrayOutputStream;

/**
 * Author:lwg
 * Time:2017/6/8
 * Edit by syk:2017/07/03
 * Description: 相机工具类
 */

public class CameraToBitmap {
    public CameraToBitmap() {
    }

    /**
     * 将相机的ByteArray解析为Bitmap
     *
     * @param data    ByteArray
     * @param _camera Camera对象
     * @return 解析成功的Bitmap
     */
    public static Bitmap decodeToBitMap(byte[] data, Camera _camera) {
        if (data == null || _camera == null) {
            return null;
        }
        Camera.Size size = _camera.getParameters().getPreviewSize();
        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);
            Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
            stream.close();
            return bmp;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 旋转图片
     *
     * @param bmp    需要旋转的图片
     * @return 旋转过后的图片
     */
    public static Bitmap rotateMyBitmap(Bitmap bmp) {

        // 旋转一下 前置为-90 ，后置为90
        Matrix matrix = new Matrix();
        matrix.postRotate(180);
//        Bitmap bitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Bitmap bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
        return bitmap;
    }
}
