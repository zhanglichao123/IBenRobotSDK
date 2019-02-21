package com.samton.IBenRobotSDK.face;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BitmapUtil {
    /**
     * 获取sd卡根目录
     *
     * @return SD卡目录
     */
    private static String getSdCard() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 判断文件夹是否需要创建
     *
     * @param path 文件夹地址
     * @return 是否要创建
     */
    private static boolean createDir(String path) {
        File file = new File(path);
        return file.exists() || file.mkdirs();
    }

    /**
     * 随机生产文件名
     *
     * @return 文件名
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 保存bitmap图片到内存卡
     *
     * @param bitmap 图片对象
     * @return 存储路径
     */
    public static String saveBitmapToSd(Bitmap bitmap) {
        String savePath = getSdCard() + "/IBenService/Images/";
        //存储成一个名字  覆盖上一个 不会图片累计
        if (createDir(savePath)) {
            savePath = savePath + "image.jpeg";
        }
        try {
            File file = new File(savePath);
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            return savePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
