package com.samton.IBenRobotSDK.core;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.util.UserWords;
import com.samton.IBenRobotSDK.interfaces.IBenRecordCallBack;
import com.samton.IBenRobotSDK.media.RecordManager;
import com.samton.IBenRobotSDK.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 小笨语音识别工具
 *     version: 1.0
 * </pre>
 */

public class IBenRecordUtil {
    /**
     * 单例
     */
    private static IBenRecordUtil instance;
    /**
     * String构造对象
     */
    private static StringBuilder mStringBuilder = null;
    /**
     * Map对象用来保存识别数据
     */
    private HashMap<String, String> map = null;
    /**
     * 录音管理类
     */
    private RecordManager mRecordManager = null;
    /**
     * 语音回调接口
     */
    private IBenRecordCallBack mCallBack = null;
    /**
     * 语音听写(中文/英文)
     */
    private String language = "zh_cn";
    /**
     * 科大讯飞识别监听
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            // 当前正在说话 i = 音量大小
            if (mCallBack != null) {
                mCallBack.onVolumeChanged(i, bytes);
            }
        }

        @Override
        public void onBeginOfSpeech() {
            // 开始说话
            if (mCallBack != null) {
                mCallBack.onBeginOfSpeech();
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 结束说话
            if (mCallBack != null) {
                mCallBack.onEndOfSpeech();
            }
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            // 解析识别结果
            doResult(recognizerResult);
            // 最终识别结果
            if (mStringBuilder != null) {
                LogUtils.d("当前的拾音为:" + mStringBuilder.toString().trim());
            }
            if (isLast && mStringBuilder != null) {
                String result = mStringBuilder.toString().trim();
                if (TextUtils.isEmpty(result)) {
                    mCallBack.onError("识别结果为空");
                } else {
                    if (!"。".equals(result)) {
                        mCallBack.onResult(result);
                    } else {
                        mCallBack.onError("识别结果为空");
                    }
                }
                mStringBuilder.delete(0, mStringBuilder.length());
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            mCallBack.onError(speechError.getErrorCode() + "");
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
        }
    };

    /**
     * 私有构造
     */
    private IBenRecordUtil() {
    }

    /**
     * 单例模式
     */
    public static synchronized IBenRecordUtil getInstance() {
        if (instance == null) {
            synchronized (IBenRecordUtil.class) {
                if (instance == null) {
                    instance = new IBenRecordUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 设置语音回调接口
     */
    public void setCallBack(IBenRecordCallBack mCallBack) {
        this.mCallBack = mCallBack;
    }

    /**
     * 初始化
     */
    public void init(Context mContext) {
        if (mRecordManager == null) {
            mRecordManager = new RecordManager(mContext);
        }
    }

    /**
     * 设置听写语言
     *
     * @param isChinese 是否为中文
     */
    public void setLanguage(boolean isChinese) {
        language = isChinese ? "zh_cn" : "en_us";
    }

    /**
     * 获取听写语言
     *
     * @return zh_cn:中文,en_us:英文
     */
    public String getLanguage() {
        return language;
    }

    /**
     * 开始识别
     */
    public void startRecognize() {
        // 设置此次的标识
        // 清空之前的识别结果
        map = new LinkedHashMap<>();
        mRecordManager.setParam();
        mRecordManager.startListener(mRecognizerListener);
    }

    /**
     * 是否在录音状态
     *
     * @return 是否在录音状态
     */
    public boolean isListening() {
        return mRecordManager != null && mRecordManager.isListening();
    }

    /**
     * 结束识别
     */
    public void stopRecognize() {
        // 结束语音识别
        if (mRecordManager != null) {
            mRecordManager.cancel();
        }
    }

    /**
     * 在界面销毁中调用
     */
    public void recycle() {
        // 重置标识
        // 回收科大讯飞录音
        if (mRecordManager != null) {
            mRecordManager.cancel();
            mRecordManager.destroy();
            // 清空回调函数
            mCallBack = null;
        }
    }

    /**
     * 识别技术的操作
     */
    private void doResult(RecognizerResult recognizerResult) {
        // 解析JSON
        String results = parseIatResult(recognizerResult.getResultString());
        if (TextUtils.isEmpty(results)) {
            return;
        }
        // 读取json结果中的sn字段
        String sn = null;
        try {
            JSONObject resultJson = new JSONObject(recognizerResult.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 将数据存入map集合
        map.put(sn, results);
        // 初始化解析数据帮助类
        mStringBuilder = new StringBuilder();
        // 将数据放入帮助类中
        for (String key : map.keySet()) {
            mStringBuilder.append(map.get(key));
        }
    }

    /**
     * 解析科大讯飞识别结果
     *
     * @param json 科大讯飞的识别Json
     * @return 解析后的String
     */
    private String parseIatResult(String json) {
        StringBuilder sb = new StringBuilder();
        try {
            JSONTokener token = new JSONTokener(json);
            JSONObject joResult = new JSONObject(token);
            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                sb.append(obj.getString("w"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 开放接口更新热词
     *
     * @param name
     * @param hotWords
     * @param listener
     */
    public int updateLexicon(String name, ArrayList<String> hotWords, LexiconListener listener) {
        if (mRecordManager == null || hotWords == null || hotWords.size() <= 0) {
            return ErrorCode.MSP_ERROR_FAIL;
        }
        UserWords userWords = new UserWords();
        userWords.putWords("默认热词", hotWords);
        return mRecordManager.updateLexicon(name, userWords.toString(), listener);
    }
}
