package com.samton.IBenRobotSDK.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;

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

    public static Bitmap decodeToBitMap(byte[] date) {
        return decodeToBitMap(date, 480, 640);
    }

    /**
     * 将相机的ByteArray解析为Bitmap
     *
     * @param data ByteArray
     * @return 解析成功的Bitmap
     */
    public static Bitmap decodeToBitMap(byte[] data, int width, int height) {
        if (data == null) {
            return null;
        }
        try {
            YuvImage image = new YuvImage(data, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, stream);
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
     * @param bmp 需要旋转的图片
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
