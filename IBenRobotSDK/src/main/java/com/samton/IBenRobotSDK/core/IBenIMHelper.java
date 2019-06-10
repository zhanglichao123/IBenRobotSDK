package com.samton.IBenRobotSDK.core;


import android.content.Context;
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
     * 人工信息回调
     */
    private IBenMsgCallBack mCallBack = null;

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
            public void onError(Exception e) {
                LogUtils.e("容联初始化失败---" + e.getMessage());
            }

            @Override
            public void onInitialized() {
                // 设置IM登录监听
                ECDevice.setOnDeviceConnectListener(IBenIMHelper.this);
                // 登录
                login();
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
        ECInitParams initParams = ECInitParams.createParams();
        initParams.reset();
        // 用户ID
        initParams.setUserid(SPUtils.getInstance().getString(ROBOT_APP_KEY));
        // mInitParams.setUserid("17600399273222");
        initParams.setAppKey(YTX_APP_ID);
        initParams.setToken(YTX_APP_TOKEN);
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

    @Override
    public void onConnectState(ECDevice.ECConnectState ecConnectState, ECError ecError) {
        LogUtils.i("onConnectState   ecConnectState:" + ecConnectState.name() + ",errpr:" + ecError.errorMsg);
        // 如果登录失败的话进行从新登录
        if (ecConnectState == ECDevice.ECConnectState.CONNECT_FAILED) {
            Log.e("登入容联账号", "失败重新登入");
            // 登录
//            login();
        } else if (ecConnectState == ECDevice.ECConnectState.CONNECT_SUCCESS) {
            // 初始化人工消息回调函数
            initIMCallBack();//接收消息的后调
            Log.e("登入容联账号", "成功");
        }
    }
}
