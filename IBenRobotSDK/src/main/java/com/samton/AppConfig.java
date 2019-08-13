package com.samton;
/***
 * You may think you know what the following code does.
 * But you dont. Trust me.
 * Fiddle with it, and youll spend many a sleepless
 * night cursing the moment you thought youd be clever
 * enough to "optimize" the code below.
 * Now close this file and go play with something else.
 */

import android.os.Environment;

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
 * Created by guoyang on 2019/8/12.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 */
public class AppConfig {
    /**
     * 是否在debug模式
     */
    public static boolean DEBUG = true;
    /**
     * 机器人AppID
     */
    public static String ROBOT_APPID = "";
    /**
     * 机器人的主板信息(3288/3399)
     */
    public static String PLANK_TYPE = "";
    /**
     * 科大讯飞的AppKey
     */
    public static String IFLYTEK_APPKEY = "";
    /**
     * 容联云的AppID
     */
    public static String YTX_APPID = "";
    /**
     * 容联云的ToKen
     */
    public static String YTX_APPTOKEN = "";
    /**
     * Face++的Key
     */
    public static String FACE_KEY = "";
    /**
     * Face++的Secret
     */
    public static String FACE_SECRET = "";
    /**
     * Face++的验证时间
     */
    public static final int FACE_TIME = 365;
    /**
     * Face++的验证接口(中文)
     */
    public static final String CN_LICENSE_URL = "https://api-cn.faceplusplus.com/sdk/v2/auth";
    /**
     * Face++的验证接口(英文)
     */
    public static final String US_LICENSE_URL = "https://api-us.faceplusplus.com/sdk/v2/auth";
    /**
     * 地图图片预览位置
     */
    public static final String MAP_PATH_THUMB = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IBenService/Maps/thumb";
    /**
     * 地图保存位置
     */
    public static final String MAP_PATH = Environment.getExternalStorageDirectory().getAbsoluteFile() + "/IBenService/Maps";
}
