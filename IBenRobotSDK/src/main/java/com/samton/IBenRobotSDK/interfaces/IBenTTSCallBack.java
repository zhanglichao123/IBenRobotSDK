package com.samton.IBenRobotSDK.interfaces;

import com.iflytek.cloud.SpeechError;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 小笨TTS回调接口
 *     version: 1.0
 * </pre>
 */

public interface IBenTTSCallBack {
    /**
     * 语音播报进度
     */
    default void onProgress(int percent, int beginPos, int endPos) {
    }

    /**
     * 暂停播报
     */
    default void onPause() {
    }

    /**
     * 继续播报
     */
    default void onResume() {
    }

    /**
     * 开始转换
     */
    void onSpeakBegin();

    /**
     * 转换结束
     *
     * @param error 错误码
     */
    void onCompleted(SpeechError error);
}
