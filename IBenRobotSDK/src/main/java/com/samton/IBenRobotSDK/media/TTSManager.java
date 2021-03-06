package com.samton.IBenRobotSDK.media;

import android.content.Context;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.samton.IBenRobotSDK.utils.SPUtils;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 科大讯飞文字转语音帮助类
 *     version: 1.0
 * </pre>
 */

public class TTSManager {
    /**
     * 上下文对象
     */
    private Context mContext;
    /**
     * 语音合成对象
     */
    private SpeechSynthesizer mTts;
    /**
     * 音色，默认发音人为小燕
     */
    private String ttsName = "xiaoyan";
    /**
     * 语速
     */
    private String ttsSpeed = "50";
    /**
     * 音调
     */
    private String ttsPitch = "50";
    /**
     * 音量
     */
    private String ttsVolume = "100";
    /**
     * 中英切换
     */
    public static boolean isVoiceSpeaker;

    /**
     * 构造函数
     *
     * @param context 上下文对象
     */
    public TTSManager(Context context) {
        // 初始化上下文对象
        this.mContext = context;
        // 初始化
        mTts = SpeechSynthesizer.createSynthesizer(context, code ->
                LogUtils.d("语音合成初始化" + (code == ErrorCode.SUCCESS ? "成功" : "失败")));
    }

    /**
     * 设置语音合成参数
     */
    public void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置在线合成发音人
        if (mTts.getParameter(SpeechConstant.ENGINE_TYPE).equals(SpeechConstant.TYPE_CLOUD)) {
            mTts.setParameter(SpeechConstant.VOICE_NAME, "vinn");
        } else { // 设置离线发音人

            // 目前IBenTTSUtil.setTTSParam()未启用，保留此行（修改音色）
            //  ttsName = SPUtils.getInstance().getString("voiceSpeaker", "xiaoyan");
            if (isVoiceSpeaker) {
                mTts.setParameter(SpeechConstant.VOICE_NAME, "catherine");
                mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath("catherine"));

            } else {
                // 设置发音人
                mTts.setParameter(SpeechConstant.VOICE_NAME, "nannan");
                // 设置发音人资源路径
                mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath("nannan"));
            }


        }
        // 设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, ttsSpeed);
        // 设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, ttsPitch);
        // 设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, ttsVolume);
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        // mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        // 保存路径
        // mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory().getAbsolutePath() + "/IBenService/Voices/" + SpeechConstant.VOICE_NAME + ".wav");
    }


    /**
     * 获取发音人资源路径
     */
    private String getResourcePath(String voiceName) {
        String tempBuffer = "";
        // 合成通用资源
        tempBuffer += ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet");
        tempBuffer += ";";
        // 发音人资源
        tempBuffer += ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/" + voiceName + ".jet");
        return tempBuffer;
    }

    /**
     * 开始转换
     *
     * @param msg      要转换的文字
     * @param listener 回调
     */
    public void startSpeaking(String msg, SynthesizerListener listener) {
        mTts.startSpeaking(msg, listener);
    }

    /**
     * 暂停说话
     */
    public void pauseSpeaking() {
        mTts.pauseSpeaking();
    }

    public void resumeSpeaking() {
        mTts.resumeSpeaking();
    }

    /**
     * 停止说话
     */
    public void stopSpeaking() {
        mTts.stopSpeaking();
    }

    /**
     * 释放链接
     */
    public void destroy() {
        mTts.destroy();
    }

    /**
     * 是否在合成状态，包括是否在播放状态，音频从服务端获取完成后，若未播放 完成，依然处于当前会话的合成中。
     */
    public boolean isSpeaking() {
        return mTts.isSpeaking();
    }

    public String getTtsName() {
        return ttsName;
    }

    public void setTtsName(String ttsName) {
        this.ttsName = ttsName;
    }

    public String getTtsSpeed() {
        return ttsSpeed;
    }

    public void setTtsSpeed(String ttsSpeed) {
        this.ttsSpeed = ttsSpeed;
    }

    public String getTtsPitch() {
        return ttsPitch;
    }

    public void setTtsPitch(String ttsPitch) {
        this.ttsPitch = ttsPitch;
    }

    public String getTtsVolume() {
        return ttsVolume;
    }

    public void setTtsVolume(String ttsVolume) {
        this.ttsVolume = ttsVolume;
    }
}
