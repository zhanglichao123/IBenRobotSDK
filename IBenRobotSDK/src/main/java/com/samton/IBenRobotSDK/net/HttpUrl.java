package com.samton.IBenRobotSDK.net;

import com.samton.AppConfig;

import rxhttp.wrapper.annotation.DefaultDomain;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 联网地址常量
 *     version: 1.0
 * </pre>
 */

public class HttpUrl {
    /**
     * 服务器地址
     */
    @DefaultDomain() //设置为默认域名
    public static final String BASE_URL = AppConfig.DEBUG ? "http://47.99.179.135:7080/" : "http://kf.ibenrobot.com/";

    /**
     * 激活机器人信息接口
     */
    public static final String ACTIVE_ROBOT = AppConfig.DEBUG ? "XiaoBenManager/robotInfo/activeRobot" : "robotInfo/activeRobot";

    /**
     * 初始化机器人信息接口
     */
    public static final String INIT_ROBOT_INFO = AppConfig.DEBUG ? "XiaoBenManager/robotInfo/robotInitNew" : "robotInfo/robotInitNew";

    /**
     * 获取开关状态接口
     */
    public static final String GET_ROBOT_CHAT_FLAG = AppConfig.DEBUG ? "XiaoBenManager/robotInfo/getRobotChatFlag" : "robotInfo/getRobotChatFlag";

    /**
     * 与小笨聊天的地址接口
     */
    public static final String CHAT = AppConfig.DEBUG ? "iben_qa/RobotQADispatcher" : "iben_qa/RobotQADispatcher";
}
