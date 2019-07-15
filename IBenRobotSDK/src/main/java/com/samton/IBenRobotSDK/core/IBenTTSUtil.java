package com.samton.IBenRobotSDK.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.samton.IBenRobotSDK.interfaces.IBenTTSCallBack;
import com.samton.IBenRobotSDK.media.TTSManager;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 小笨文字转语音工具
 *     version: 1.0
 * </pre>
 */

public final class IBenTTSUtil {
    /**
     * 单例
     */
    private static IBenTTSUtil instance;
    /**
     * 录音管理类
     */
    private TTSManager mTTSManager = null;
    /**
     * 语音回调接口
     */
    private IBenTTSCallBack mCallBack = null;
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {
        /**
         * 开始播报
         */
        @Override
        public void onSpeakBegin() {
            if (mCallBack != null) mCallBack.onSpeakBegin();
        }

        /**
         * 合成进度
         * @param percent 当前进度
         * @param beginPos 开始进度
         * @param endPos 结束进度
         * @param info 附加信息
         */
        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        /**
         * 暂停播放
         */
        @Override
        public void onSpeakPaused() {
            if (mCallBack != null) mCallBack.onPause();
        }

        /**
         * 继续播放
         */
        @Override
        public void onSpeakResumed() {
            if (mCallBack != null) mCallBack.onResume();
        }

        /**
         * 播放进度
         * @param percent 当前进度
         * @param beginPos 开始进度
         * @param endPos 结束进度
         */
        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            if (mCallBack != null) mCallBack.onProgress(percent, beginPos, endPos);
        }

        /**
         * 播报结束
         * @param error 是否有错误
         */
        @Override
        public void onCompleted(SpeechError error) {
            if (mCallBack != null) mCallBack.onCompleted(error);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            // if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            // 	 String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            // 	 Log.d(TAG, "session id =" + sid);
            // }
        }
    };

    /**
     * 私有构造
     */
    private IBenTTSUtil() {
    }

    /**
     * 单例模式
     */
    public static synchronized IBenTTSUtil getInstance() {
        if (instance == null) {
            synchronized (IBenTTSUtil.class) {
                if (instance == null) {
                    instance = new IBenTTSUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     */
    public void init(Context context) {
        if (mTTSManager == null) {
            mTTSManager = new TTSManager(context.getApplicationContext());
        }
    }

    /**
     * 开始合成语音
     *
     * @param msg 需要合成语音的文字
     */
    public void startSpeaking(String msg, IBenTTSCallBack callBack) {
        this.mCallBack = callBack;
        mTTSManager.setParam();
        mTTSManager.startSpeaking(msg, mTtsListener);
    }

    /**
     * 获取播放状态
     *
     * @return 播放状态
     */
    public boolean isSpeaking() {
        return mTTSManager != null && mTTSManager.isSpeaking();
    }

    /**
     * 暂停播放
     */
    public void pauseSpeaking() {
        if (mTTSManager != null) {
            mTTSManager.pauseSpeaking();
        }
    }

    /**
     * 继续播放
     */
    public void resumeSpeaking() {
        if (mTTSManager != null) {
            mTTSManager.resumeSpeaking();
        }
    }

    /**
     * 停止合成
     */
    public void stopSpeaking() {
        if (mTTSManager != null) {
            mTTSManager.stopSpeaking();
        }
    }

    /**
     * 在界面销毁中调用
     */
    public void recycle() {
        // 回收资源
        if (mTTSManager != null) {
            mTTSManager.stopSpeaking();
            mTTSManager.destroy();
            // 清空回调函数
            mCallBack = null;
        }
    }

    /**
     * 配置语音合成参数
     *
     * @param ttsName   音色对应名字
     * @param ttsSpeed  语速
     * @param ttsPitch  音调
     * @param ttsVolume 音量
     */
    public void setTTSParam(String ttsName, String ttsSpeed, String ttsPitch, String ttsVolume) {
        if (mTTSManager != null) {
            return;
        }
        if (!TextUtils.isEmpty(ttsName)) {
            mTTSManager.setTtsName(ttsName);
        }
        if (!TextUtils.isEmpty(ttsSpeed)) {
            mTTSManager.setTtsSpeed(ttsSpeed);
        }
        if (!TextUtils.isEmpty(ttsPitch)) {
            mTTSManager.setTtsPitch(ttsPitch);
        }
        if (!TextUtils.isEmpty(ttsVolume)) {
            mTTSManager.setTtsVolume(ttsVolume);
        }
    }
}
