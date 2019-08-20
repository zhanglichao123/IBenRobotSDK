package com.samton.IBenRobotSDK.core;
/***
 * You may think you know what the following code does.
 * But you dont. Trust me.
 * Fiddle with it, and youll spend many a sleepless
 * night cursing the moment you thought youd be clever
 * enough to "optimize" the code below.
 * Now close this file and go play with something else.
 */

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
 * Created by guoyang on 2019/6/6.
 * github https://github.com/GuoYangGit
 * QQ:352391291
 * 关于头部/左右手的动作操作类
 */
public class IBenActionUtil {
    private static IBenActionUtil mInstance = null;
    // 头部旋转角度
    private int headAngle;
    // 左臂抬起角度
    private int leftArmAngle;
    // 右臂抬起角度
    private int rightArmAngle;

    private IBenActionUtil() {

    }

    public static IBenActionUtil getInstance() {
        if (mInstance == null) {
            synchronized (IBenActionUtil.class) {
                if (mInstance == null) {
                    mInstance = new IBenActionUtil();
                }
            }
        }
        return mInstance;
    }

    /**
     * 发送头部转动指令
     *
     * @param angle 转动角度:小于0度为左转,大于0度为右转,0为归位
     */
    public void headAction(int angle) {
        //避免发送重复角度指令
        if (angle == headAngle) return;
        headAngle = angle;
        int realAngle = angle + 90;
        if (!checkAngle(realAngle)) return;
        String action = "{SH" + getAngle(realAngle) + "300}";
        sendData(action);
    }

    /**
     * 发送左手转动指令
     *
     * @param angle 转动角度:0为放下
     */
    public void leftArm(int angle) {
        //避免发送重复角度指令
        if (angle == leftArmAngle) return;
        leftArmAngle = angle;
        int realAngle = angle + 90;
        if (!checkAngle(realAngle)) return;
        String action = "{SL" + getAngle(realAngle) + "300}";
        sendData(action);
    }

    /**
     * 发送右手转动指令
     *
     * @param angle 转动角度:0为放下
     */
    public void rightArm(int angle) {
        //避免发送重复角度指令
        if (angle == rightArmAngle) return;
        rightArmAngle = angle;
        int realAngle = angle + 90;
        if (!checkAngle(realAngle)) return;
        String action = "{SR" + getAngle(realAngle) + "300}";
        sendData(action);
    }

    /**
     * 获取当前头部角度
     *
     * @return 角度信息
     */
    public int getHeadAngle() {
        return headAngle;
    }

    /**
     * 获取当前左手角度
     *
     * @return 角度信息
     */
    public int getLeftArmAngle() {
        return leftArmAngle;
    }

    /**
     * 获取当前右手角度
     *
     * @return 角度信息
     */
    public int getRightArmAngle() {
        return rightArmAngle;
    }

    /**
     * 获取角度指令
     *
     * @param angle 角度信息
     * @return 角度指令
     */
    private String getAngle(int angle) {
        if (angle >= 100) {
            return String.valueOf(angle);
        } else {
            return "0" + String.valueOf(angle);
        }
    }

    /**
     * 检查角度是否合法
     *
     * @param angle 角度信息
     * @return true为合法, false为不合法
     */
    private boolean checkAngle(int angle) {
        return angle > 0 && angle < 360;
    }

    /**
     * 发送串口指令
     *
     * @param msg 指令信息
     */
    private void sendData(String msg) {
        IBenSerialUtil.getInstance().sendData(msg);
    }
}
