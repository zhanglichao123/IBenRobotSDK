package com.samton.IBenRobotSDK.core;

import android.content.Context;

import com.samton.IBenRobotSDK.data.MessageBean;
import com.samton.IBenRobotSDK.interfaces.IBenMsgCallBack;
import com.samton.IBenRobotSDK.net.HttpRequest;
import com.samton.IBenRobotSDK.net.HttpUtil;
import com.samton.IBenRobotSDK.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.samton.IBenRobotSDK.data.Constants.ROBOT_APP_KEY;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/09/07
 *     desc   : 聊天SDK
 *     version: 1.0
 * </pre>
 */

public final class IBenChatSDK {
    /**
     * 单例模式
     */
    private static IBenChatSDK instance = null;
    /**
     * 信息回调
     */
    private IBenMsgCallBack mCallBack = null;
    /**
     * 获取IBenChat状态
     */
    private Disposable mGetChatFlagSubscribe;
    /**
     * 发送消息给服务器
     */
    private Disposable mSendIBenSubscribe;

    /**
     * 私有构造函数
     */
    private IBenChatSDK() {
    }

    /**
     * 获取聊天SDK单例
     *
     * @return 聊天SDK单例
     */
    public static IBenChatSDK getInstance() {
        if (instance == null) {
            synchronized (IBenChatSDK.class) {
                if (instance == null) {
                    instance = new IBenChatSDK();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化IM模块
     *
     * @param context 上下文对象
     */
    public void initIMSDK(Context context) {
        // 初始化IM模块
        IBenIMHelper.getInstance().init(context.getApplicationContext());
    }

    /**
     * 设置消息回调监听
     *
     * @param callBack 消息回调
     */
    public void setCallBack(IBenMsgCallBack callBack) {
        mCallBack = callBack;
        IBenIMHelper.getInstance().setCallBack(mCallBack);
    }

    /**
     * 移除消息回调监听
     */
    public void removeCallBack() {
        cancelHttp();
        mCallBack = null;
        IBenIMHelper.getInstance().removeCallBack();
    }

    /**
     * 取消所有的网络请求
     */
    private void cancelHttp() {
        if (mGetChatFlagSubscribe != null && !mGetChatFlagSubscribe.isDisposed()) {
            mGetChatFlagSubscribe.dispose();
            mGetChatFlagSubscribe = null;
        }
        if (mSendIBenSubscribe != null && !mSendIBenSubscribe.isDisposed()) {
            mSendIBenSubscribe.dispose();
            mSendIBenSubscribe = null;
        }
    }

    /**
     * 与小笨机器人聊天
     *
     * @param msg 要发送的信息
     */
    public void sendMessage(String msg) {
        sendMessage(msg, "");
    }

    /**
     * 与小笨机器人聊天
     *
     * @param msg   要发送的信息
     * @param reMsg 返回的关联问题
     */
    public void sendMessage(String msg, String reMsg) {
        // 标识位赋值
        sendMessage(msg, reMsg, "0");
    }

    /**
     * 与小笨机器人聊天
     *
     * @param msg   要发送的信息
     * @param reMsg 返回的关联问题
     */
    public void sendMessage(String msg, String reMsg, String reIndex) {
        cancelHttp();
        mGetChatFlagSubscribe = HttpUtil.getInstance().create(HttpRequest.class)
                .getRobotChatFlag(SPUtils.getInstance().getString(ROBOT_APP_KEY))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(chatFlagBean -> {
                    if (chatFlagBean.getRs() == 1) {
                        // 发送给人工的消息
                        IBenIMHelper.getInstance().sendTxtMsg(chatFlagBean.getAccout(), msg);
                    } else {// 发送消息给小笨
                        send2IBen(msg, reMsg, reIndex);
                    }
                }, throwable -> mCallBack.onSuccess(getDefaultMessageBean()));
    }

    /**
     * 发送消息给小笨
     *
     * @param msg   要发送的消息
     * @param reMsg 返回的关联问题
     */
    private void send2IBen(String msg, String reMsg, String reIndex) {
        // APP_KEY
        String appKey = SPUtils.getInstance().getString(ROBOT_APP_KEY);
        // 当前时间
        String time = String.valueOf(System.currentTimeMillis());
        cancelHttp();
        mSendIBenSubscribe = HttpUtil.getInstance().create(HttpRequest.class)
                .send2IBen(appKey, time, msg, reMsg, reIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(messageBean -> mCallBack.onSuccess(messageBean),
                        throwable -> mCallBack.onSuccess(getDefaultMessageBean()));
    }

    /**
     * 构建 默认的发送消息 请求失败的消息
     *
     * @return 返回请求失败的实体类
     */
    private MessageBean getDefaultMessageBean() {
        MessageBean messageBean = new MessageBean();
        MessageBean.DataBean dataBean = new MessageBean.DataBean();
        MessageBean.DataBean.AppMessageBean appMessageBean = new MessageBean.DataBean.AppMessageBean();
        appMessageBean.setAnswerType(0);
        appMessageBean.setMessage("我没有找到答案，您可以问问别的问题呦");
        appMessageBean.setIsAnswer(true);
        List<MessageBean.DataBean.AppMessageBean> list = new ArrayList<>();
        list.add(appMessageBean);
        dataBean.setAppMessage(list);
        messageBean.setData(dataBean);
        return messageBean;
    }
}
