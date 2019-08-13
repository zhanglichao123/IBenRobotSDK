package com.samton.IBenRobotSDK.core;


import android.annotation.SuppressLint;
import android.content.Context;

import com.google.gson.Gson;
import com.samton.AppConfig;
import com.samton.IBenRobotSDK.data.MessageBean;
import com.samton.IBenRobotSDK.interfaces.IBenMsgCallBack;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.yuntongxun.ecsdk.ECChatManager;
import com.yuntongxun.ecsdk.ECDevice;
import com.yuntongxun.ecsdk.ECError;
import com.yuntongxun.ecsdk.ECInitParams;
import com.yuntongxun.ecsdk.ECMessage;
import com.yuntongxun.ecsdk.OnChatReceiveListener;
import com.yuntongxun.ecsdk.im.ECMessageNotify;
import com.yuntongxun.ecsdk.im.ECTextMessageBody;
import com.yuntongxun.ecsdk.im.group.ECGroupNoticeMessage;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/09/12
 *     desc   : 小笨IM-SDK
 *     version: 1.0
 *     implements ECDevice.OnECDeviceConnectListener,OnChatReceiveListener
 * </pre>
 */

public class IBenIMHelper implements ECDevice.OnECDeviceConnectListener, OnChatReceiveListener {
    private static IBenIMHelper instance = null;
    /**
     * 人工信息回调
     */
    private IBenMsgCallBack mCallBack = null;
    private Disposable mLoginSubscribe;

    /**
     * 私有构造
     */
    private IBenIMHelper() {
    }

    /**
     * 获取IM模块单例
     *
     * @return IM单例
     */
    public static IBenIMHelper getInstance() {
        if (instance == null) {
            synchronized (IBenIMHelper.class) {
                if (instance == null) {
                    instance = new IBenIMHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context 上下文对象
     */
    public void init(Context context) {
        if (ECDevice.isInitialized()) return;
        ECDevice.initial(context.getApplicationContext(), new ECDevice.InitListener() {
            @Override
            public void onInitialized() {
                // 设置IM登录监听
                ECDevice.setOnDeviceConnectListener(IBenIMHelper.this);
                // 登录
                login();
            }

            @Override
            public void onError(Exception e) {
            }
        });
    }

    /**
     * 设置容联云消息监听
     *
     * @param callBack 消息监听
     */
    public void setCallBack(IBenMsgCallBack callBack) {
        this.mCallBack = callBack;
    }

    /**
     * 移除消息监听
     */
    public void removeCallBack() {
        this.mCallBack = null;
    }

    /**
     * 发送消息到IM
     *
     * @param receiver 消息接受者
     * @param sendMsg  要发送的信息
     */
    void sendTxtMsg(String receiver, String sendMsg) {
        // 组建一个待发送的ECMessage
        ECMessage ecMessage = ECMessage.createECMessage(ECMessage.Type.TXT);
        // 设置消息接受者
        ecMessage.setTo(receiver);
        // 创建一个文本消息体，并添加到消息对象中
        ECTextMessageBody msgBody = new ECTextMessageBody(sendMsg);
        // 将消息体存放到ECMessage中
        ecMessage.setBody(msgBody);
        // 调用SDK发送接口发送消息到服务器
        ECChatManager ecChatManager = ECDevice.getECChatManager();
        ecChatManager.sendMessage(ecMessage, new ECChatManager.OnSendMessageListener() {
            @Override
            public void onSendMessageComplete(ECError ecError, ECMessage ecMessage) {
            }

            @Override
            public void onProgress(String s, int i, int i1) {

            }
        });
    }

    /**
     * 初始化人工信息回调函数
     */
    private void initIMCallBack() {
        ECDevice.setOnChatReceiveListener(this);
    }

    @Override
    public void OnReceivedMessage(ECMessage msg) {
        // 接收到的IM消息，根据IM消息类型做不同的处理(IM消息类型：ECMessage.Type)
        ECMessage.Type type = msg.getType();
        if (type != ECMessage.Type.TXT || mCallBack == null) return;
        // 在这里处理文本消息
        ECTextMessageBody textMessageBody = (ECTextMessageBody) msg.getBody();
        String result = textMessageBody.getMessage();
        mCallBack.onSuccess(new Gson().fromJson(result, MessageBean.class));
    }

    @Override
    public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {
    }

    @Override
    public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {
    }

    @Override
    public void onOfflineMessageCount(int i) {
    }

    @Override
    public int onGetOfflineMessage() {
        return 0;
    }

    @Override
    public void onReceiveOfflineMessage(List<ECMessage> list) {
    }

    @Override
    public void onReceiveOfflineMessageCompletion() {
    }

    @Override
    public void onServicePersonVersion(int i) {
    }

    @Override
    public void onReceiveDeskMessage(ECMessage ecMessage) {
    }

    @Override
    public void onSoftVersion(String s, int i) {
    }

    /**
     * 登录到云通讯
     */
    private void login() {
        ECInitParams initParams = ECInitParams.createParams();
        initParams.reset();
        // 用户ID
        initParams.setUserid(AppConfig.ROBOT_APPID);
        initParams.setAppKey(AppConfig.YTX_APPID);
        initParams.setToken(AppConfig.YTX_APPTOKEN);
        initParams.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        // LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO。使用方式详见注意事项）FORCE_LOGIN
        initParams.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
        // 根据登录参数进行IM登录操作
        ECDevice.login(initParams);
    }

    @Override
    public void onConnect() {
        // 根据接入文档此方法不用管
    }

    @Override
    public void onDisconnect(ECError ecError) {
        // 根据接入文档此方法不用管
    }

    @SuppressLint("CheckResult")
    @Override
    public void onConnectState(ECDevice.ECConnectState ecConnectState, ECError ecError) {
        // 如果登录失败的话进行重新登录
        if (ecConnectState == ECDevice.ECConnectState.CONNECT_FAILED) {
            LogUtils.e("登入容联账号----失败重新登入");
            // 登录
            if (mLoginSubscribe != null && !mLoginSubscribe.isDisposed()) return;
            mLoginSubscribe = Observable.timer(5, TimeUnit.SECONDS)
                    .subscribeOn(Schedulers.io())
                    .subscribe(aLong -> login());
        } else if (ecConnectState == ECDevice.ECConnectState.CONNECT_SUCCESS) {
            // 初始化人工消息回调函数
            initIMCallBack();//接收消息的后调
            LogUtils.e("登入容联账号----登陆成功");
        }
    }
}
