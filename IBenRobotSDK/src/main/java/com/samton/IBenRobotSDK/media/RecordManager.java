package com.samton.IBenRobotSDK.media;

import android.content.Context;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.samton.IBenRobotSDK.core.IBenRecordUtil;
import com.samton.IBenRobotSDK.utils.LogUtils;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 科大讯飞语音识别帮助类
 *     version: 1.0
 * </pre>
 */

public class RecordManager {
    /**
     * 语音识别类
     */
    private SpeechRecognizer mIat;

    /**
     * 构造函数
     *
     * @param context 上下文对象
     */
    public RecordManager(Context context) {
        // 初始化讯飞听写
        mIat = SpeechRecognizer.createRecognizer(context, code -> {
            if (code != ErrorCode.SUCCESS) {
                LogUtils.d(">>>>>>>>语音听写初始化失败");
            } else {
                LogUtils.d(">>>>>>>>语音听写初始化成功");
            }
        });
    }

    /**
     * 参数设置
     */
    public void setParam() {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 引擎模式
        mIat.setParameter(SpeechConstant.ENGINE_MODE, SpeechConstant.MODE_AUTO);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        String lag = IBenRecordUtil.getInstance().getLanguage();
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, lag);
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, lag);
        // 采样率为16000单声道音频
//        mIat.setParameter(SpeechConstant.SAMPLE_RATE, "16000");
        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "5000");
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1800");
        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");
        // 网络连接超时时间
        mIat.setParameter(SpeechConstant.NET_TIMEOUT, "4500");
        // 说话的最长时间
        mIat.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "30000");
        // 录音文件名
//        String mFileName = System.currentTimeMillis() + "-" + UUID.randomUUID().toString() + ".wav";
//        File file = new File(Environment.getExternalStorageDirectory() + "/IBenService/voice_recorder_audios");
//        // 如果文件夹不存在则创建
//        if (!file.exists() && file.isDirectory()) {
//            file.mkdir();
//        }
//        String path = file.getPath() + "/" + mFileName;
//        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, path);
//        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
    }

    /**
     * 开始语音识别
     *
     * @param listener 回调函数
     */
    public void startListener(RecognizerListener listener) {
        mIat.startListening(listener);
    }

    /**
     * 释放链接
     */
    public void destroy() {
        if (null != mIat) {
            mIat.destroy();
        }
    }

    /**
     * 取消语音识别
     */
    public void cancel() {
        if (mIat.isListening() && mIat != null) {
            mIat.cancel();
        }
    }

    /**
     * 是否在录音状态
     *
     * @return 是否在录音状态
     */
    public boolean isListening() {
        return mIat != null && mIat.isListening();
    }

    /**
     * 开放接口更新热词
     *
     * @param name
     * @param content
     * @param listener
     */
    public int updateLexicon(String name, String content, LexiconListener listener) {
        if (mIat == null) {
            return ErrorCode.MSP_ERROR_FAIL;
        }
        return mIat.updateLexicon(name, content, listener);
    }
}
