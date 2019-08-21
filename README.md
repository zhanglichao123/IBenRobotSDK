## 关于IBenSDK使用说明文档

## 库的引入(Android Studio工程)

- 将以下`jar`/`aar`文件复制到工程的libs目录下;

	- `IBenRobotSDK.jar`机器人SDK库;
	- `Msc.jar`科大讯飞;
	- `slamware_sdk_android.jar`思岚底盘;
	- `YTX_Android_Full_SDK.jar`容联云;
	- `MGFaceppSDK-0.5.2.aar`;
	- `MGLicenseManagerSDK-0.3.0.aar`;

- 项目`module`的`buid.gradle`配置;
- 项目清单文件`AndroidManifest.xml`配置;

	```xml
	<?xml version="1.0" encoding="utf-8"?>
	<manifest xmlns:android="http://schemas.android.com/apk/res/android"
	          xmlns:tools="http://schemas.android.com/tools">
	    <!-- 相机权限 -->
	    <uses-feature android:name="android.hardware.camera"/>
	    <uses-feature android:name="android.hardware.camera.autofocus"/>
	    <uses-permission android:name="android.permission.CAMERA"/>
	    <!-- 联网权限 -->
	    <uses-permission android:name="android.permission.INTERNET"/>
	    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
	    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
	    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"/>
	    <!-- 读取内部存储数据权限 -->
	    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
	    <!-- 写入内部存储数据权限 -->
	    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	    <!-- 读取电话状态权限 -->
	    <uses-permission android:name="android.permission.READ_PHONE_STATE"/>
	    <!--定位权限-->
	    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
	    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
	    <!-- 允许程序录制音频 -->
	    <uses-permission android:name="android.permission.RECORD_AUDIO"/>
	    <!-- 允许应用程序修改全局声音设置的权限 -->
	    <uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS"/>
	    <!-- 允许使用PowerManager的WakeLocks在进程休眠时能够后台保持 -->
	    <uses-permission android:name="android.permission.WAKE_LOCK"/>
	    <!-- 允许应用更改网络状态 -->
	    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"/>
	    <!-- 允许应用获取震动权限 -->
	    <uses-permission android:name="android.permission.VIBRATE"/>
	    <!-- 允许应用读取系统日志 -->
	    <uses-permission
	        android:name="android.permission.READ_LOGS"
	        tools:ignore="ProtectedPermissions"/>
	    <!-- 开机自启 -->
	    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
	    <!-- USB权限 -->
	    <uses-permission android:name="com.android.example.USB_PERMISSION"/>
	    <uses-permission
	        android:name="android.permission.WRITE_SETTINGS"
	        tools:ignore="ProtectedPermissions"/>
	    <!-- 云通讯SDK接收IM消息的自定义权限 -->
	    <permission
	        android:name="com.samton.permission.RECEIVE_MSG"
	        android:protectionLevel="signature"/>
	    <!-- //接收来电的自定义权限 -->
	    <permission
	        android:name="com.samton.permission.INCOMING_CALL"
	        android:protectionLevel="signature"/>
	    <!--finish-->
	
	    <application
	        ...>
	        <!-- 科大讯飞 -->
	        <meta-data
	            android:name="IFLYTEK_APPKEY"
	            android:value="这里是科大讯飞的AppKey"/>
	        <!-- 容联云的AppID -->
	        <meta-data
	            android:name="YTX_APPID"
	            android:value="这里是容联云的AppID"/>
	        <!-- 容联云的ToKen -->
	        <meta-data
	            android:name="YTX_APPTOKEN"
	            android:value="这里是容联云的AppToken"/>
	        <!-- Face++的Key -->
	        <meta-data
	            android:name="FACE_KEY"
	            android:value="这里是Face++的AppKey"/>
	        <!-- Face++的Secret -->
	        <meta-data
	            android:name="FACE_SECRET"
	            android:value="这里是Face++的AppSecret"/>
	        <!-- 荣联云通讯 -->
	        <service
	            android:name="com.yuntongxun.ecsdk.ECClientService"
	            android:enabled="true"
	            android:process=":push"/>
	        <service
	            android:name="com.yuntongxun.ecsdk.ECClientService$InnerService"
	            android:enabled="true"
	            android:process=":push"/>
	
	        <receiver
	            android:name="com.yuntongxun.ecsdk.booter.CCPReceivers$AlarmReceiver"
	            android:process=":push"/>
	        <receiver
	            android:name="com.yuntongxun.ecsdk.booter.Alarm"
	            android:exported="false"
	            android:process=":push"/>
	        <receiver
	            android:name="com.yuntongxun.ecsdk.booter.CCPReceivers$ConnectionReceiver"
	            android:process=":push">
	            <intent-filter>
	                <action
	                    android:name="android.net.conn.CONNECTIVITY_CHANGE"
	                    tools:ignore="BatteryLife"/>
	            </intent-filter>
	        </receiver>
	        <!-- 荣联IM配置结束 -->
	    </application>
	</manifest>
	```

## 项目初始化

在项目的自定义`Application`类的`onCreate()`方法中进行`SDK`的初始化.

```java
/**
     * 初始化SDK
     *
     * @param application 上下文
     * @param appKey      机器人ID
     * @param type        主板类型
     * @param isDebug     是否是测试环境
     */
// 初始化小笨机器人SDK
MainSDK.getInstance().init(mApplication, mAppKey, mPlankType, false);
```

在确保网络正常的前提下调用激活机器人方法.

```java
/**
     * 激活机器人
     *
     * @param faceppRawId Face++的model
     * @param callBack    激活回调接口
     */
// 调用SDK方法激活机器人
 MainSDK.getInstance().activeRobot(R.raw.megviifacepp_0_5_2_model, new MainSDK.IActiveCallBack() {
                @Override
                public void onSuccess() {
						//这里是激活成功的返回
						...
                }

                @Override
                public void onFailed(String msg) {
						//这里是激活失败的返回
						...
                }
            });
```

## API功能说明

1. **`IBenActionUtil`关于机器人头部/左右手的动作操作类**

	- `headAction(int angle)`发送头部转动指令,`angle`旋转角度:小于0度为左转,大于0度为右转,0为归位;
	- `leftArm(int angle)`发送左手转动指令,`angle`转动角度:0为放下;
	- `rightArm(int angle)`发送右手转动指令,`angle`转动角度:0为放下;
	- `getHeadAngle()`获取当前头部角度,返回值为`int`类型的角度信息;
	- `getLeftArmAngle()`获取当前左手角度,返回值为`int`类型的角度信息;
	- `getRightArmAngle()`获取当前右手角度,返回值为`int`类型的角度信息;

2. **`IBenChatSDK`关于机器人聊天的操作类**

	- `initIMSDK(Context context)`初始化IM模块,`context`上下文对象;
	- `setCallBack(IBenMsgCallBack callBack)`设置消息回调监听,`callBack`消息回调;
	- `removeCallBack()`移除消息回调监听;
	- `sendMessage(String msg, String reMsg, String reIndex)`向后台发送聊天内容;

3. **`IBenRecordUtil`关于机器人语音听写工具类**

	- `init(Context context)`初始化机器人语音听写工具类;
	- `setCallBack(IBenRecordCallBack mCallBack)`设置语音听写结果回调接口;
	- `setLanguage(boolean isChinese)`设置听写语言,`isChinese`是否为中文;
	- `startRecognize()`开启语音识别;
	- `isListening()`判断是否在语音识别状态;
	- `stopRecognize()`结束语音识别;
	- `recycle()`回收语音识别资源;

4. **`IBenSerialUtil`关于机器人串口通信工具类**

	- `sendData(String msg)`向串口写入数据信息;
	- `setCallBack(ISerialCallBack callBack)`设置串口信息回写监听回调;
	- `removeCallBack()`移除回调监听;

5. **`IBenTTSUtil`关于机器人语音播报工具类**

	- `init(Context context)`初始化机器人语音播报工具类;
	- `startSpeaking(String msg, IBenTTSCallBack callBack)`开始语音播报;
	- `isSpeaking()`判断是否在语音播报状态;
	- `pauseSpeaking()`暂停语音播报;
	- `resumeSpeaking()`继续语音播报;
	- `stopSpeaking()`停止语音播报;
	- `recycle()`回收语音播报资源;
	- `setTTSParam(String ttsName, String ttsSpeed, String ttsPitch, String ttsVolume)`配置语音播报参数,`ttsName`音色对应名字,`ttsSpeed`语速,`ttsPitch`音调,`ttsVolume`音量;
	
6. **`IBenWakeUpUtil`关于机器人语音唤醒工具类**

	- `setCallBack(IWakeUpCallBack callBack)`设置语音唤醒监听回调;
	- `setBeam()`加强六麦中的正向一麦;
	- `stopWakeUp()`停止语音唤醒监听;

7. **`HttpUtils`关于机器人网络请求工具类**

	- `robotInit()`初始化机器人信息接口;

8. **`FaceManager`关于机器人人脸识别工具类**

	- `CheckFace(byte[] imageData, int width, int height)`检测人脸数据,`imageData`需要检测的图像信息,`width`图像的宽度,`height`图像的高度;

9. **`IBenMoveSDK`关于机器人底盘移动工具类**

	- `connectRobot(String ip, int port, ConnectCallBack callBack)`连接机器人底盘;
	- `isConnect()`获取机器人底盘连接状态;
	- `disConnectRobot()`断开机器人底盘的连接;
	- `setMapUpdate(boolean isUpdate)`设置是否开启地图更新;
	- `removeMap(ResultCallBack<Boolean> callBack)`清除当前加载的地图;
	- `getBatteryInfo(GetBatteryCallBack callBack)`获取电池信息;
	- `getLocation(ResultCallBack<Location> callBack)`获取当前机器人所在位置的坐标点信息;
	- `getPose(ResultCallBack<Pose> callBack)`获取当前机器人所在位置的姿态;
	- `setPose(Pose pose, StopBtnState btnState)`设置当前机器人所在位置的姿态;
	- `moveByDirection(MoveDirection direction, StopBtnState btnState)/moveByDirection(MoveDirection direction, long period, StopBtnState btnState)`根据方向进行移动和间隔持续移动,该方法不会避障;
	- `rotate(double angle, StopBtnState btnState)`旋转机器人;
	- `goHome(MoveCallBack callBack, StopBtnState btnState)`回充电桩;
	- `goLocation(Location location, float yaw, MoveCallBack callBack, StopBtnState btnState)`机器人行走到指定点;
	- `cancelAllActions()`停止所有动作;
	- `clearAllWalls()`清除所有虚拟墙;
	- `isHome(ResultCallBack<Boolean> callBack)`判断机器人是否是无线充电状态;
	- `getPowerStatus(ResultCallBack<Integer> callBack)`查询机器人电池状态;
	- `saveMap(String mapName, MapCallBack callBack)`保存地图;
	- `loadMap(String mapNamePath, Pose cachePose, MapCallBack callBack)`根据地图名字加载地图;
	- `hasSystemEmergencyStop(ResultCallBack<Boolean> callBack)`判断底盘急停按钮是否开启;
	- `isMoveing(ResultCallBack<Boolean> callBack)`判断底盘是否正在运动;