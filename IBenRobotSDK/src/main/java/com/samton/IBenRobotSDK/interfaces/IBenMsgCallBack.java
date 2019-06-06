package com.samton.IBenRobotSDK.interfaces;


import com.samton.IBenRobotSDK.data.MessageBean;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 与小笨聊天的回调
 *     version: 1.0
 * </pre>
 */

public interface IBenMsgCallBack {
    /**
     * 成功回调
     *
     * @param bean 回调帮助类
     */
    void onSuccess(MessageBean bean);

}
