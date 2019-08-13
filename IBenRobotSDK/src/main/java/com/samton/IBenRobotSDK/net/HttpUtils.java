package com.samton.IBenRobotSDK.net;
/***
 * You may think you know what the following code does.
 * But you dont. Trust me.
 * Fiddle with it, and youll spend many a sleepless
 * night cursing the moment you thought youd be clever
 * enough to "optimize" the code below.
 * Now close this file and go play with something else.
 */

import com.samton.AppConfig;
import com.samton.IBenRobotSDK.data.ActiveBean;
import com.samton.IBenRobotSDK.data.ChatFlagBean;
import com.samton.IBenRobotSDK.data.InitBean;
import com.samton.IBenRobotSDK.data.MessageBean;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rxhttp.wrapper.param.RxHttp;

/***
 *
 *   █████▒█    ██  ▄████▄   ██ ▄█▀       ██████╗ ██╗   ██╗ ██████╗
 * ▓██   ▒ ██  ▓██▒▒██▀ ▀█   ██▄█▒        ██╔══██╗██║   ██║██╔════╝
 * ▒████ ░▓██  ▒██░▒▓█    ▄ ▓███▄░        ██████╔╝██║   ██║██║  ███╗
 * ░▓█▒  ░▓▓█  ░██░▒▓▓▄ ▄██▒▓██ █▄        ██╔══██╗██║   ██║██║   ██║
 * ░▒█░   ▒▒█████▓ ▒ ▓███▀ ░▒██▒ █▄       ██████╔╝╚██████╔╝╚██████╔╝
 *  ▒ ░   ░▒▓▒ ▒ ▒ ░ ░▒ ▒  ░▒ ▒▒ ▓▒       ╚═════╝  ╚═════╝  ╚═════╝
 *  ░     ░░▒░ ░ ░   ░  ▒   ░ ░▒ ▒░
 *  ░ ░    ░░░ ░ ░ ░        ░ ░░ ░
 *           ░     ░ ░      ░  ░
 *
 * Created by guoyang on 2019/8/13.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */
public class HttpUtils {
    /**
     * 激活机器人信息接口
     */
    private static final String ACTIVE_ROBOT = "robotInfo/activeRobot";

    /**
     * 初始化机器人信息接口
     */
    private static final String INIT_ROBOT_INFO = "robotInfo/robotInitNew";

    /**
     * 获取开关状态接口
     */
    private static final String GET_ROBOT_CHAT_FLAG = "robotInfo/getRobotChatFlag";

    /**
     * 与小笨聊天的地址接口(QA)
     */
    private static final String CHAT = "iben_qa/RobotQADispatcher";

    /**
     * 激活机器人信息接口
     *
     * @return ActiveBean:激活实体类
     */
    public static Observable<ActiveBean> activeRobot() {
        return RxHttp.get(ACTIVE_ROBOT)
                .add("robUuid", AppConfig.ROBOT_APPID)
                .asObject(ActiveBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 初始化机器人信息接口
     *
     * @return InitBean:机器人实体类
     */
    public static Observable<InitBean> robotInit() {
        return RxHttp.get(INIT_ROBOT_INFO)
                .add("robotUUID", AppConfig.ROBOT_APPID)
                .asObject(InitBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 初始化机器人信息接口
     *
     * @param checkFlag 模式
     * @return InitBean:机器人实体类
     */
    public static Observable<InitBean> robotInit(String checkFlag) {
        return RxHttp.get(INIT_ROBOT_INFO)
                .add("robotUUID", AppConfig.ROBOT_APPID)
                .add("checkFlag", checkFlag)
                .asObject(InitBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 获取开关状态接口
     *
     * @return ChatFlagBean:实体类
     */
    public static Observable<ChatFlagBean> getRobotChatFlag() {
        return RxHttp.get(GET_ROBOT_CHAT_FLAG)
                .add("robUuid", AppConfig.ROBOT_APPID)
                .asObject(ChatFlagBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 与小笨聊天的地址接口
     *
     * @param time    时间戳
     * @param msg     消息内容
     * @param reMsg   关联问题
     * @param reIndex 关联第几个问题
     * @return MessageBean:实体类
     */
    public static Observable<MessageBean> getChat(String time, String msg, String reMsg, String reIndex) {
        return RxHttp.get(CHAT)
                .setDomainToQAIfAbsent()
                .add("appKey", AppConfig.ROBOT_APPID)
                .add("time", time)
                .add("message", msg)
                .add("relationQuestion", reMsg)
                .add("relationIndex", reIndex)
                .asObject(MessageBean.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
