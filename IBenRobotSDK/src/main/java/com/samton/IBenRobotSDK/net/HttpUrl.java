package com.samton.IBenRobotSDK.net;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/04/07
 *     desc   : 联网地址常量
 *     version: 1.0
 * </pre>
 */

public final class HttpUrl {
    /**
     * 是否为测试环境
     */
    private static final boolean isTest = false;
    /**
     * 服务器地址
     * 正式地址 http://114.55.111.3/
     * 测试地址 http://47.96.137.231:7080/
     */
    final static String BASE_URL =
            isTest ? "http://47.96.137.231:7080/" : "http://kf.ibenrobot.com/";
    /**
     * 与小笨聊天的地址接口
     * 正式地址 iben_qa/RobotQADispatcher
     * 测试地址 iben_qa/RobotQADispatcher
     */
    final static String CHAT = isTest ? "iben_qa/RobotQADispatcher" : "iben_qa/RobotQADispatcher";
    /**
     * 激活机器人信息接口
     * 正式地址 robotInfo/activeRobot
     * 测试地址 XiaoBenManager/robotInfo/activeRobot
     */
    final static String ADD_ROBOT_INFO = isTest ? "XiaoBenManager/robotInfo/activeRobot" : "robotInfo/activeRobot";
    /**
     * 初始化机器人信息接口
     * 正式地址 robotInfo/robotInitNew
     * 测试地址 XiaoBenManager/robotInfo/robotInitNew
     */
    final static String INIT_ROBOT_INFO = isTest ? "XiaoBenManager/robotInfo/robotInitNew" : "robotInfo/robotInitNew";
    /**
     * 获取开关状态接口
     * 正式地址 robotInfo/getRobotChatFlag
     * 测试地址 XiaoBenManager/robotInfo/getRobotChatFlag
     */
    final static String GET_ROBOT_CHAT_FLAG = isTest ? "XiaoBenManager/robotInfo/getRobotChatFlag" : "robotInfo/getRobotChatFlag";
    /**
     * 富文本连接头
     * 正式地址 resources/views/show.html?content=
     * 测试地址 XiaoBenManager/resources/views/show.html?content=
     */
    public final static String RICH_HEADER = BASE_URL + (isTest ? "resources/views/show.html?content=" : "resources/views/show.html?content=");
}
