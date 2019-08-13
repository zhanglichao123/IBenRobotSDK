package com.samton.IBenRobotSDK.core;

import android.content.Context;
import android.text.TextUtils;

import com.samton.IBenRobotSDK.data.MessageBean;
import com.samton.IBenRobotSDK.interfaces.IBenMsgCallBack;
import com.samton.IBenRobotSDK.net.HttpUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/09/07
 *     desc   : 聊天SDK
 *     version: 1.0
 * </pre>
 */

public class IBenChatSDK {
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
        //取消网络请求
        cancelHttp();
        mCallBack = null;
        IBenIMHelper.getInstance().removeCallBack();
    }

    /**
     * 取消所有的网络请求
     */
    private void cancelHttp() {
        //取消获取聊天类型的请求
        if (mGetChatFlagSubscribe != null && !mGetChatFlagSubscribe.isDisposed()) {
            mGetChatFlagSubscribe.dispose();
            mGetChatFlagSubscribe = null;
        }
        //取消向后台发送消息的请求
        if (mSendIBenSubscribe != null && !mSendIBenSubscribe.isDisposed()) {
            mSendIBenSubscribe.dispose();
            mSendIBenSubscribe = null;
        }
    }

    /**
     * 与小笨机器人聊天
     *
     * @param msg 发送的信息
     */
    public void sendMessage(String msg) {
        sendMessage(msg, "");
    }

    /**
     * 与小笨机器人聊天
     *
     * @param msg   发送的信息
     * @param reMsg 关联问题
     */
    public void sendMessage(String msg, String reMsg) {
        // 标识位赋值
        sendMessage(msg, reMsg, "0");
    }

    /**
     * 与小笨机器人聊天
     *
     * @param msg   发送的信息
     * @param reMsg 关联问题
     */
    public void sendMessage(String msg, String reMsg, String reIndex) {
        // 取消网络请求
        cancelHttp();
        // 向后台获取人工状态
        mGetChatFlagSubscribe = HttpUtils
                .getRobotChatFlag()
                .subscribe(chatFlagBean -> {
                    String account = chatFlagBean.getAccout();
                    // 发送消息给小笨
                    if (chatFlagBean.getRs() != 1) send2IBen(msg, account, reMsg, reIndex);
                    // 发送给人工的消息
                    if (!TextUtils.isEmpty(account)) {
                        IBenIMHelper.getInstance().sendTxtMsg(account, msg);
                    }
                }, throwable -> {
                    if (mCallBack == null) return;
                    // 失败返回默认的提示内容
                    mCallBack.onSuccess(getDefaultMessageBean());
                });
    }

    /**
     * 发送消息给小笨
     *
     * @param msg   发送的消息
     * @param reMsg 关联问题
     */
    private void send2IBen(String msg, String account, String reMsg, String reIndex) {
        // 当前时间
        String time = String.valueOf(System.currentTimeMillis());
        // 取消网络请求
        cancelHttp();
        mSendIBenSubscribe = HttpUtils
                .getChat(time, msg, reMsg, reIndex)
                .subscribe(msgBean -> {
                    if (mCallBack == null) return;
                    mCallBack.onSuccess(msgBean);
                    // 发送消息到容联云
                    sendMsgToYtx(account, msgBean);
                }, throwable -> {
                    if (mCallBack == null) return;
                    // 失败返回默认的提示内容
                    mCallBack.onSuccess(getDefaultMessageBean());
                });
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

    /**
     * 判断返回消息类型并回传给后台(人工消息框)
     */
    private void sendMsgToYtx(String account, MessageBean msgBean) {
        // 先做非空判断
        if (account == null || msgBean == null) return;
        MessageBean.DataBean mDataBean = msgBean.getData();
        if (mDataBean == null) return;
        List<MessageBean.DataBean.AppMessageBean> mAppMessage = mDataBean.getAppMessage();
        if (mAppMessage == null || mAppMessage.isEmpty()) return;
        MessageBean.DataBean.AppMessageBean mResult = mAppMessage.get(0);
        if (mResult == null) return;
        String resultAnswer = mResult.getMessage();
        // 判断类型
        switch (mResult.getAnswerType()) {
            case 1:
                resultAnswer = "富文本消息";
                break;
            case 2:
                resultAnswer = "超链接消息";
                break;
            case 3:
                resultAnswer = "图片消息";
                break;
            case 4:
                resultAnswer = "表单信息";
                break;
            case 5:
                resultAnswer = "流程消息";
                break;
            case 7:
                resultAnswer = "视频消息";
                break;
            case 8:
                resultAnswer = "动作消息";
                break;
            case 21:
                resultAnswer = "天气消息";
                break;
            case 22:
                resultAnswer = "故事消息";
                break;
        }
        // 发送给人工的消息
        IBenIMHelper.getInstance().sendTxtMsg(account, "#RENGONG#" + resultAnswer);
    }
}
