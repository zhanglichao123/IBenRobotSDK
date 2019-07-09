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
    private static final boolean isTest = true;

    /**
     * 服务器地址
     */
    public static final String BASE_URL = isTest ? "http://47.99.179.135:7080/" : "http://kf.ibenrobot.com/";

    /**
     * 与小笨聊天的地址接口
     */
    public static final String CHAT = isTest ? "iben_qa/RobotQADispatcher" : "iben_qa/RobotQADispatcher";

    /**
     * 激活机器人信息接口
     */
    public static final String ADD_ROBOT_INFO = isTest ? "XiaoBenManager/robotInfo/activeRobot" : "robotInfo/activeRobot";

    /**
     * 初始化机器人信息接口
     */
    public static final String INIT_ROBOT_INFO = isTest ? "XiaoBenManager/robotInfo/robotInitNew" : "robotInfo/robotInitNew";

    /**
     * 获取开关状态接口
     */
    public static final String GET_ROBOT_CHAT_FLAG = isTest ? "XiaoBenManager/robotInfo/getRobotChatFlag" : "robotInfo/getRobotChatFlag";

    /**
     * 富文本连接头
     */
    public static final String RICH_HEADER = BASE_URL + (isTest ? "XiaoBenManager/resources/views/show.html?content=" : "resources/views/show.html?content=");
}
