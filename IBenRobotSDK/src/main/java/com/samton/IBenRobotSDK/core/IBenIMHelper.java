package com.samton.IBenRobotSDK.core;


import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.google.gson.Gson;
import com.samton.IBenRobotSDK.data.MessageBean;
import com.samton.IBenRobotSDK.interfaces.IBenMsgCallBack;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.samton.IBenRobotSDK.utils.SPUtils;
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

import static com.samton.IBenRobotSDK.data.Constants.ROBOT_APP_KEY;
import static com.samton.IBenRobotSDK.data.Constants.YTX_APP_ID;
import static com.samton.IBenRobotSDK.data.Constants.YTX_APP_TOKEN;

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

final class IBenIMHelper implements ECDevice.OnECDeviceConnectListener, OnChatReceiveListener {
    private static IBenIMHelper instance = null;
    /**
     * 登录参数
     */
    private ECInitParams mInitParams = null;
    /**
     * 是否初始化荣联SDK
     */
    private boolean isInit = false;
    /**
     * 人工信息回调
     */
    private IBenMsgCallBack mCallBack = null;
    /**
     * 标识
     */
    private int mTag = -1;
    /**
     * 5秒从新登录倒计时
     */
    private CountDownTimer timer = new CountDownTimer(5000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            // 登录
            login();
        }
    };

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
     * @param tag       标识
     * @param mContext  上下文对象
     * @param mCallBack 回调
     */
    public void init(int tag, final Context mContext, final IBenMsgCallBack mCallBack) {
        mTag = tag;
        this.mCallBack = mCallBack;
        // if (!isInit) {
        if (!ECDevice.isInitialized()) {
            ECDevice.initial(mContext.getApplicationContext(), new ECDevice.InitListener() {
                @Override
                public void onError(Exception e) {
                    LogUtils.e("容联初始化失败---" + e.getMessage());
                }

                @Override
                public void onInitialized() {
                    isInit = true;
                    // 设置IM登录监听
                    ECDevice.setOnDeviceConnectListener(IBenIMHelper.this);
                    // 登录
                    login();
                }
            });
        }
    }

    /**
     * 发送消息到IM
     *
     * @param receiver 消息接受者
     * @param sendMsg  要发送的信息
     */
    void sendTxtMsg(int tag, String receiver, String sendMsg) {
        mTag = tag;
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
                LogUtils.d("onSendMessageComplete:" + ecError.errorCode + ",msg:" + ecError.errorMsg);
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
        ECDevice.setOnChatReceiveListener(null);
        ECDevice.setOnChatReceiveListener(this);
    }

    @Override
    public void OnReceivedMessage(ECMessage msg) {
        // 接收到的IM消息，根据IM消息类型做不同的处理(IM消息类型：ECMessage.Type)
        ECMessage.Type type = msg.getType();
        if (type == ECMessage.Type.TXT) {
            // 在这里处理文本消息
            ECTextMessageBody textMessageBody = (ECTextMessageBody) msg.getBody();
            String result = textMessageBody.getMessage();
            LogUtils.i("OnReceivedMessage", "result:" + result);
            mCallBack.onSuccess(mTag, new Gson().fromJson(result, MessageBean.class));
        }
    }

    @Override
    public void onReceiveMessageNotify(ECMessageNotify ecMessageNotify) {
        LogUtils.i("onReceiveMessageNotify", ":" + ecMessageNotify.getMsgId() + ecMessageNotify.describeContents());
    }

    @Override
    public void OnReceiveGroupNoticeMessage(ECGroupNoticeMessage ecGroupNoticeMessage) {
        LogUtils.i("OnReceiveGroupNoticeMessage", "getGroupName:" + ecGroupNoticeMessage.getGroupName());
    }

    @Override
    public void onOfflineMessageCount(int i) {
        LogUtils.i("onOfflineMessageCount", "count:" + i);
    }

    @Override
    public int onGetOfflineMessage() {
        LogUtils.i("onGetOfflineMessage");
        return 0;
    }

    @Override
    public void onReceiveOfflineMessage(List<ECMessage> list) {
        LogUtils.i("onReceiveOfflineMessage  list:" + list.size());
    }

    @Override
    public void onReceiveOfflineMessageCompletion() {
        LogUtils.i("onReceiveOfflineMessageCompletion ");
    }

    @Override
    public void onServicePersonVersion(int i) {
        LogUtils.i("onServicePersonVersion ");
    }

    @Override
    public void onReceiveDeskMessage(ECMessage ecMessage) {
        LogUtils.i("onReceiveDeskMessage   ecMessage.getId():" + ecMessage.getId());
    }

    @Override
    public void onSoftVersion(String s, int i) {
        LogUtils.i("onReceiveDeskMessage   s:" + s);
    }

    /**
     * 登录到云通讯
     */
    private void login() {
        // 初始化登录参数
        mInitParams = ECInitParams.createParams();
        mInitParams.reset();
        // 用户ID
        mInitParams.setUserid(SPUtils.getInstance().getString(ROBOT_APP_KEY));
        // mInitParams.setUserid("17600399273222");
        mInitParams.setAppKey(YTX_APP_ID);
        mInitParams.setToken(YTX_APP_TOKEN);
        mInitParams.setAuthType(ECInitParams.LoginAuthType.NORMAL_AUTH);
        // LoginMode（强制上线：FORCE_LOGIN  默认登录：AUTO。使用方式详见注意事项）FORCE_LOGIN
        mInitParams.setMode(ECInitParams.LoginMode.FORCE_LOGIN);
        // 根据登录参数进行IM登录操作
        ECDevice.login(mInitParams);
    }

    @Override
    public void onConnect() {
        // 根据接入文档此方法不用管
    }

    @Override
    public void onDisconnect(ECError ecError) {
        // 根据接入文档此方法不用管
    }

    @Override
    public void onConnectState(ECDevice.ECConnectState ecConnectState, ECError ecError) {
        LogUtils.i("onConnectState   ecConnectState:" + ecConnectState.name() + ",errpr:" + ecError.errorMsg);
        // 如果登录失败的话进行从新登录
        if (ecConnectState == ECDevice.ECConnectState.CONNECT_FAILED) {
            Log.e("登入容联账号", "失败重新登入");
            // 开启重新登陆倒计时
            timer.start();
        } else if (ecConnectState == ECDevice.ECConnectState.CONNECT_SUCCESS) {
            // 初始化人工消息回调函数
            initIMCallBack();//接收消息的后调
            Log.e("登入容联账号", "成功");
        }
    }


}
