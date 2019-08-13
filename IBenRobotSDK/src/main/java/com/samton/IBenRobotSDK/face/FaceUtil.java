package com.samton.IBenRobotSDK.face;

import android.content.Context;
import android.util.Base64;

import com.samton.IBenRobotSDK.utils.SPUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class FaceUtil {

    /**
     * 获取UUID
     *
     * @return String类型
     */
    public static String getUUIDString() {
        String KEY_UUID = "key_uuid";
        SPUtils spInstance = SPUtils.getInstance();
        String uuid = spInstance.getString(KEY_UUID);
        if (uuid.trim().length() != 0)
            return uuid;
        uuid = UUID.randomUUID().toString();
        uuid = Base64.encodeToString(uuid.getBytes(), Base64.DEFAULT);
        spInstance.put(KEY_UUID, uuid);
        return uuid;
    }

    /**
     * 获取文件内容
     *
     * @param context 上下文
     * @param id
     * @return
     */
    public static byte[] getFileContent(Context context, int id) {
        InputStream inputStream;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int count;
        try {
            inputStream = context.getResources().openRawResource(id);
            while ((count = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, count);
            }
            byteArrayOutputStream.close();
        } catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }
}
