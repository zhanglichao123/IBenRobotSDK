## 关于IBenSDK使用说明文档

## 库的引入(Android Studio工程)

- 将以下`jar`/`aar`文件复制到工程的libs目录下;

	- `IBenRobotSDK.jar`;
	- `Msc.jar`科大讯飞;
	- `slamware_sdk_android.jar`思岚底盘;
	- `YTX_Android_Full_SDK.jar`容联云;
	- `MGFaceppSDK-0.5.2.aar`;
	- `MGLicenseManagerSDK-0.3.0.aar`;

- 项目`module`的`buid.gradle`配置;
- 项目清单文件``配置;

	```
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

```
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

```
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

3. **`IBenRecordUtil`**

	- `setCallBack(IBenRecordCallBack mCallBack)`
