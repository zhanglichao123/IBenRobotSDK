## 关于IBenSDK使用说明文档

---

### 库的引入(Android Studio工程)

- **将以下`jar`/`aar`文件复制到工程的`libs`目录下**

	- `IBenRobotSDK.jar`机器人SDK库;
	- `Msc.jar`科大讯飞;
	- `slamware_sdk_android.jar`思岚底盘;
	- `YTX_Android_Full_SDK.jar`容联云;
	- `MGFaceppSDK-0.5.2.aar`Face++;
	- `MGLicenseManagerSDK-0.3.0.aar`Face++;

- **将以下`so`文件复制到工程的`libs`目录下的`armeabi/armeabi-v7a`目录下**

	- `libmsc.so`科大讯飞;
	- `librpsdk.so`思岚底盘;
	- `libSerial_Port.so`串口通讯;
	- `libserphone.so`容联云(`IM`消息和音视频以及音视频会议功能库,必须添加);
	- `libvoicechange.so`容联云(**5.3.0**以及之后版本添加了语音变声接口库,用来将录制的语音文件进行音频数据处理转换成不同发声效果);
	- `libECMedia.so`容联云(使用音视频功能,必须添加);
	- `libyuntx_gl_disp.so`容联云(**5.3.0**以及之后版本添加了视频图像绘制库,使用`ECOpenGLView`控件需要添加);

- **项目`module`的`buid.gradle`配置**

	```
	android {
	    defaultConfig {
	        ndk {
	            abiFilters 'armeabi', 'armeabi-v7a'
	        }
	    }
	
	    //生成libs目录
	    sourceSets {
	        main {
	            jniLibs.srcDirs = ['libs']
	        }
	    }
	}
	
	repositories {
	    flatDir {
	        dirs 'libs'
	    }
	}
	
	dependencies {
        implementation(name: 'MGLicenseManagerSDK-0.3.0', ext: 'aar')
	    implementation(name: 'MGFaceppSDK-0.5.2', ext: 'aar')
	}
	```
	
- **项目清单文件`AndroidManifest.xml`配置**

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

### 项目初始化

- **在项目的自定义`Application`类的`onCreate()`方法中进行`SDK`的初始化**

	```
    /**
     * 初始化SDK
     *
     * @param application 上下文
     * @param appKey      机器人ID
     * @param type        主板类型
     * @param isDebug     是否是测试环境
     */
    MainSDK.getInstance().init(mApplication, mAppKey, mPlankType, false);
	```

- **在确保网络正常的前提下调用激活机器人方法**

	```
    /**
     * 激活机器人
     *
     * @param faceppRawId Face++的model
     * @param callBack    激活回调接口
     */
    MainSDK.getInstance().activeRobot(R.raw.megviifacepp_0_5_2_model, new MainSDK.IActiveCallBack() {
            /**
             * 激活成功
             */
            @Override
            public void onSuccess() {
            }
            /**
             * 激活失败
             * @param msg 失败原因
             */
            @Override
            public void onFailed(String msg) {
            }
    });
	```

### API功能说明

1. **`IBenActionUtil`关于机器人头部/左右手的动作操作类**

    - `headAction(int angle)`发送头部转动指令,`angle`旋转角度:小于0度为左转,大于0度为右转,0为归位;
    
    ```
    /**
     * @param angle旋转角度:小于0度为左转,大于0度为右转,0为归位
     */
    IBenActionUtil.getInstance().headAction(angle);
    ```
    	
    - `leftArm(int angle)`发送左手转动指令,`angle`转动角度:0为放下;
    
    ```
    /**
     * @param angle转动角度:0为放下
     */
    IBenActionUtil.getInstance().leftArm(angle);
    ```
    
    - `rightArm(int angle)`发送右手转动指令,`angle`转动角度:0为放下;
    
    ```
    /**
     * @param angle转动角度:0为放下
     */
    IBenActionUtil.getInstance().leftArm(angle);
    ```
    	
    - `getHeadAngle()`获取当前头部角度,返回值为`int`类型的角度信息;
    
    ```
    /**
     * @return 当前头部角度
     */
    IBenActionUtil.getInstance().getHeadAngle();
    ```
    	
    - `getLeftArmAngle()`获取当前左手角度,返回值为`int`类型的角度信息;
    
    ```
    /**
     * @return 当前左手角度
     */
    IBenActionUtil.getInstance().getLeftArmAngle();
    ```
    	
    - `getRightArmAngle()`获取当前右手角度,返回值为`int`类型的角度信息;
    
    ```
    /**
     * @return 当前右手角度
     */
    IBenActionUtil.getInstance().getRightArmAngle();
    ```
	
2. **`IBenChatSDK`关于机器人聊天的操作类**

    - `initIMSDK(Context context)`初始化IM模块,`context`上下文对象;
    
    ```
    /**
     * @param context上下文对象，建议传application的上下文
     */
    IBenChatSDK.getInstance().initIMSDK(context);
    ```
    	
    - `setCallBack(IBenMsgCallBack callBack)`设置消息回调监听,`callBack`消息回调;
    
    ```
    /**
     * @param callBack 发送消息成功后服务器返回信息监听上下文
     */
    IBenChatSDK.getInstance().setCallBack(new IBenMsgCallBack() {
        /**
         * @param messageBean 服务器返回的消息体
         */
        @Override
        public void onSuccess(MessageBean messageBean) {
        }
    });
    ```
    
    - `removeCallBack()`移除消息回调监听;
    
    ```
    IBenChatSDK.getInstance().removeCallBack();
    ```
    	
    - `sendMessage(String msg, String reMsg, String reIndex)`向后台发送聊天内容;
    
    ```
    /**
     * @param msg 当前发送的消息
     * @param reMsg 关联问题
     * @param reIndex 关联问题下标(本地标记，从0开始)
     */
    IBenChatSDK.getInstance().sendMessage(msg, reMsg, reIndex);
    ```	

3. **`IBenRecordUtil`关于机器人语音听写工具类**
    
    - `init(Context context)`初始化机器人语音听写工具类;
    
    ```
    /**
     * @param context 上下文对象
     */
    IBenRecordUtil.getInstance().init(context);
    ```
    	
    - `setCallBack(IBenRecordCallBack mCallBack)`设置语音听写结果回调接口;
    
    ```
    /**
     * @param callBack 录音回调监听
     */
    IBenRecordUtil.getInstance().setCallBack(new IBenRecordCallBack() {
        /**
         * 开始说话
         */
        @Override
        public void onBeginOfSpeech() {
        }
        /**
         * 音量更改
         *
         * @param i     级别
         * @param bytes 数据
         */
        @Override
        public void onVolumeChanged(int volume, byte[] bytes) {
        }
        /**
         * 结束说话
         */
        @Override
        public void onEndOfSpeech() {
        }
        /**
         * 听写结果
         *
         * @param result 识别数据
         */
        @Override
        public void onResult(String msg) {
        }
        /**
         * 错误回调
         *
         * @param errorMsg (科大讯飞)错误信息
         */
        @Override
        public void onError(String msg) {
        }
    });
    ```
    	
    - `setLanguage(boolean isChinese)`设置听写语言,`isChinese`是否为中文;
    
    ```
    /**
     * @param isChinese 是否为中文
     */
    IBenRecordUtil.getInstance().setLanguage(isChinese);
    ```
    	
    - `startRecognize()`开启语音识别;
    
    ```
    IBenRecordUtil.getInstance().startRecognize();
    ```
    	
    - `isListening()`判断是否在语音识别状态;
    
    ```
    /**
     * @return 是否在录音状态
     */
    IBenRecordUtil.getInstance().isListening();
    ```
    	
    - `stopRecognize()`结束语音识别;
    
    ```
    IBenRecordUtil.getInstance().stopRecognize();
    ```
    	
    - `recycle()`回收语音识别资源;
    
    ```
    IBenRecordUtil.getInstance().recycle();
    ```

4. **`IBenSerialUtil`关于机器人串口通信工具类**

	- `sendData(String msg)`向串口写入数据信息;

    ```
    /**
     * @param msg 要写的数据
     */
	IBenSerialUtil.getInstance().sendData(msg);
	```
	
	- `setCallBack(ISerialCallBack callBack)`设置串口信息回写监听回调;

    ```
    /**
     * @param callBack 串口通讯回调
     */
	IBenSerialUtil.getInstance().setCallBack(new ISerialCallBack() {
            /**
             * 回写数据
             * @param result 数据
             */
            @Override
            public void onReadData(String result) {
            }
        });
	```
	
	- `removeCallBack()`移除回调监听;

	```
	IBenSerialUtil.getInstance().removeCallBack();
	```

5. **`IBenTTSUtil`关于机器人语音播报工具类**

    - `init(Context context)`初始化机器人语音播报工具类;
    
    ```
    /**
     * @param context 上下文对象
     */
    IBenTTSUtil.getInstance().init(context);
    ```
    	
    - `startSpeaking(String msg, IBenTTSCallBack callBack)`开始语音播报;
    
    ```
    /**
     * @param msg 需要合成语音的文字
     * @param callBack 语音合成回调
     */
    IBenTTSUtil.getInstance().startSpeaking(msg, new IBenTTSCallBack() {
        /**
         * 语音播报进度
         * 
         * @param percent 当前进度
         * @param beginPos 开始进度
         * @param endPos 结束进度
         */
        @Override
        public void onProgress(int percent, int beginPos, int endPos) {
        }
        /**
         * 暂停播报
         */
        @Override
        public void onPause() {
        }
        /**
         * 继续播报
         */
        @Override
        public void onResume() {
        }
        /**
         * 开始合成
         */
        @Override
        public void onSpeakBegin() {
        }
        /**
         * 合成结束
         * 
         * @param error 错误码(合成错误会返回)
         */
        @Override
        public void onCompleted(SpeechError error) {
        }
    });
    ```
    	
    - `isSpeaking()`判断是否在语音播报状态;
    
    ```
    /**
     * @return 是否正在播报
     */
    IBenTTSUtil.getInstance().isSpeaking();
    ```
    	
    - `pauseSpeaking()`暂停语音播报;
    
    ```
    IBenTTSUtil.getInstance().pauseSpeaking();
    ```
    	
    - `resumeSpeaking()`继续语音播报(与pauseSpeaking()成对使用);
    
    ```
    IBenTTSUtil.getInstance().resumeSpeaking();
    ```
    	
    - `stopSpeaking()`停止语音播报;
    
    ```
    IBenTTSUtil.getInstance().stopSpeaking();
    ```
    	
    - `recycle()`回收语音播报资源;
    
    ```
    IBenTTSUtil.getInstance().recycle();
    ```
    	
    - `setTTSParam(String ttsName, String ttsSpeed, String ttsPitch, String ttsVolume)`配置语音播报参数,`ttsName`音色对应名字,`ttsSpeed`语速,`ttsPitch`音调,`ttsVolume`音量;
    
    ```
    /**
     * @param ttsName   音色对应名字
     * @param ttsSpeed  语速
     * @param ttsPitch  音调
     * @param ttsVolume 音量
     */
    IBenTTSUtil.getInstance().setTTSParam(ttsName, ttsSpeed, ttsPitch, ttsVolume);
    ```
	
6. **`IBenWakeUpUtil`关于机器人语音唤醒工具类**

    - `setCallBack(IWakeUpCallBack callBack)`设置语音唤醒监听回调;
    
    ```
    /**
     * @param callBack 语音唤醒回调
     */
    IBenWakeUpUtil.getInstance().setCallBack(new IWakeUpCallBack() {
        /**
         * 回调已经唤醒
         *
         * @param angle 唤醒类型
         */
        @Override
        public void onWakeUp(int angle) {
        }
    }；
    ```
    	
    - `setBeam()`加强六麦中的正向一麦;
    	
    ```
    IBenWakeUpUtil.getInstance().setBeam();
    ```
    	
    - `stopWakeUp()`停止语音唤醒监听;
    
    ```
    IBenWakeUpUtil.getInstance().stopWakeUp();
    ```

7. **`HttpUtils`关于机器人网络请求工具类**

    - `robotInit()`初始化机器人信息接口;
        
    ```
    IBenRobotSDK.net.HttpUtils.robotInit();
    ```

9. **`IBenMoveSDK`关于机器人底盘移动工具类**

    - `connectRobot(String ip, int port, ConnectCallBack callBack)`连接机器人底盘;
    
    ```
    /**
     * @param ip 底盘ip地址
     * @param port 底盘端口号
     * @param callBack 底盘连接回调
     */
    IBenMoveSDK.getInstance().connectRobot(ip, port, new IBenMoveSDK.ConnectCallBack() {
        /**
         * 连接成功
         */
        @Override
        public void onConnectSuccess() {
        }
        /**
         * 连接失败
         */
        @Override
        public void onConnectFailed() {
        }
    });
    ```
    	
    - `isConnect()`获取机器人底盘连接状态;
    
    ```
    /**
     * @return 底盘是否已连接
     */
    IBenMoveSDK.getInstance().isConnect();
    ```
    	
    - `disConnectRobot()`断开机器人底盘的连接;
    
    ```
    IBenMoveSDK.getInstance().disConnectRobot();
    ```
    	
    - `setMapUpdate(boolean isUpdate)`设置是否开启地图更新;
    
    ```
    /**
     * @param isUpdate 是否开启地图更新
     */
    IBenMoveSDK.getInstance().setMapUpdate(isUpdate);
    ```
    	
    - `removeMap(ResultCallBack<Boolean> callBack)`清除当前加载的地图;
    
    ```
    /**
     * @param callBack 删除地图回调
     */
    IBenMoveSDK.getInstance().removeMap(new IBenMoveSDK.ResultCallBack<Boolean>() {       
        /**
         * 删除地图结果
         * 
         * @param aBoolean 是否删除成功
         */
        @Override
        public void onResult(Boolean aBoolean) {
        }
    });
    ```
    	
    - `getBatteryInfo(GetBatteryCallBack callBack)`获取电池信息;
    
    ```
    /**
     * @param callBack 获取电量回调
     */
    IBenMoveSDK.getInstance().getBatteryInfo(new IBenMoveSDK.GetBatteryCallBack() {
        /**
         * 获取电量成功
         * 
         * @param result 点亮状态信息(Json格式数据，isCharging:是否在充电、batteryPercent:电量百分比)
         */
        @Override
        public void onSuccess(String result) {
        }
    
        /**
         * 获取电量失败
         */
        @Override
        public void onFailed() {
        }
    });
    ```
    	
    - `getLocation(ResultCallBack<Location> callBack)`获取当前机器人所在位置的坐标点信息;
    
    ```
    /**
     * @param callBack 获取当前位置信息回调
     */
    IBenMoveSDK.getInstance().getLocation(new IBenMoveSDK.ResultCallBack<Location>() {
        /**
         * @param location 当前底盘的坐标信息
         */
        @Override
        public void onResult(Location location) {
        }
    });
    ```
    	
    - `getPose(ResultCallBack<Pose> callBack)`获取当前机器人所在位置的姿态;
    
    ```
    /**
     * @param callBack 获取当前位置信息回调
     */
    IBenMoveSDK.getInstance().getPose(new IBenMoveSDK.ResultCallBack<Pose>() {
        /**
         * @param pose 当前底盘的姿态信息(Pose包含坐标信息和角度信息)
         */
        @Override
        public void onResult(Pose pose) {
        }
    });
    ```
    	
    - `setPose(Pose pose, StopBtnState btnState)`设置当前机器人所在位置的姿态;
    
    ```
    /**
     * @param pose 当前底盘的姿态信息
     * @param callBack 急停按钮回调
     */
    IBenMoveSDK.getInstance().setPose(pose, new IBenMoveSDK.StopBtnState() {
        /**
         * @param isOpen 急停按钮是否开启
         */
        @Override
        public void isOnEmergencyStop(boolean isOpen) {
        }
    });
    ```
    	
    - `moveByDirection(MoveDirection direction, StopBtnState btnState)/moveByDirection(MoveDirection direction, long period, StopBtnState btnState)`根据方向进行移动和间隔持续移动,该方法不会避障;
    
    ```
    /**
     * @param direction 移动方向
     * @param period 每次执行间隔时长，单位ms(推荐300)
     * @param callBack 急停按钮回调
     */
    IBenMoveSDK.getInstance().moveByDirection(direction, period, new IBenMoveSDK.StopBtnState() {
        /**
         * @param isOpen 急停按钮是否开启
         */
        @Override
        public void isOnEmergencyStop(boolean isOpen) {
        }
    });
    ```
    	
    - `rotate(double angle, StopBtnState btnState)`旋转机器人;
    
    ```
    /**
     * @param angle 旋转角度(小于0度为左转,大于0度为右转)
     * @param callBack 急停按钮回调
     */
    IBenMoveSDK.getInstance().rotate(angle, new IBenMoveSDK.StopBtnState() {
        /**
         * @param isOpen 急停按钮是否开启
         */
        @Override
        public void isOnEmergencyStop(boolean isOpen) {
        }
    });
    ```
    	
    - `goHome(MoveCallBack callBack, StopBtnState btnState)`回充电桩;
    
    ```
    /**
     * @param callBack1 回充电桩回调
     * @param callBack2 急停按钮回调
     */
    IBenMoveSDK.getInstance().goHome(new IBenMoveSDK.MoveCallBack() {
        /**
         * @param isSuccess 是否成功回到充电桩
         */
        @Override
        public void onFinish(boolean isSuccess) {
        }},new IBenMoveSDK.StopBtnState() {
        /**
         * @param isOpen 急停按钮是否开启
         */
        @Override
        public void isOnEmergencyStop(boolean isOpen) {
        }
    });
    ```
    	
    - `goLocation(Location location, float yaw, MoveCallBack callBack, StopBtnState btnState)`机器人行走到指定点;
    
    ```
    /**
     * @param location 要去的点位信息
     * @param yaw 到达点位需要旋转的角度
     * @param callBack1 去点位回调
     * @param callBack2 急停按钮回调
     */
    IBenMoveSDK.getInstance().goLocation(location, yaw, new IBenMoveSDK.MoveCallBack() {
        /**
         * @param isSuccess 是否成功到达指定点
         */
        @Override
        public void onFinish(boolean isSuccess) {
        }}, new IBenMoveSDK.StopBtnState() {
        /**
         * @param isOpen 急停按钮是否开启
         */
        @Override
        public void isOnEmergencyStop(boolean isOpen) {
        }
    });
    ```
    	
    - `cancelAllActions()`停止所有动作;
    
    ```
    IBenMoveSDK.getInstance().cancelAllActions();
    ```
    	
    - `clearAllWalls()`清除所有虚拟墙;
    
    ```
    IBenMoveSDK.getInstance().clearAllWalls();
    ```
    	
    - `isHome(ResultCallBack<Boolean> callBack)`判断机器人是否是无线充电状态;
    
    ```
    /**
     * @param callBack 是否在充电桩回调
     */
    IBenMoveSDK.getInstance().isHome(new IBenMoveSDK.ResultCallBack<Boolean>() {
        /**
         * @param isHome 是否在充电桩
         */
        @Override
        public void onResult(Boolean isHome) {
        }
    });
    ```
    	
    - `getPowerStatus(ResultCallBack<Integer> callBack)`查询机器人电池状态;
    
    ```
    /**
     * @param callBack 获取电池状态回调
     */
    IBenMoveSDK.getInstance().getPowerStatus(new IBenMoveSDK.ResultCallBack<Integer>() {
        /**
         * @param powerStatus 电池当前状态标记(0:不在充电,1:线充,2:桩充)
         */
        @Override
        public void onResult(Integer powerStatus) {
        }
    });
    ```
    	
    - `saveMap(String mapName, MapCallBack callBack)`保存地图;
    
    ```
    /**
     * @param mapName 地图名称
     * @param callBack 保存地图回调
     */
    IBenMoveSDK.getInstance().saveMap(mapName, new IBenMoveSDK.MapCallBack() {
        /**
         * 地图保存成功
         */
        @Override
        public void onSuccess() {
        } 
        /**
         * 地图保存失败
         */
        @Override
        public void onFailed() {
        }
    });
    ```
    	
    - `loadMap(String mapNamePath, Pose cachePose, MapCallBack callBack)`根据地图名字加载地图;
    
    ```
    /**
     * @param mapFilePath 地图文件路径
     * @param pose 保存地图是的姿态信息 
     * @param callBack 加载地图回调
     */
    IBenMoveSDK.getInstance().loadMap(mapFilePath, pose, new IBenMoveSDK.MapCallBack() {
        /**
         * 地图加载成功
         */
        @Override
        public void onSuccess() {
        }
        /**
         * 地图加载失败
         */
        @Override
        public void onFailed() {
        }
    });
    ```
    	
    - `hasSystemEmergencyStop(ResultCallBack<Boolean> callBack)`判断底盘急停按钮是否开启;
    
    ```
    /**
     * @param callBack 急停按钮状态回调
     */
    IBenMoveSDK.getInstance().hasSystemEmergencyStop(new IBenMoveSDK.ResultCallBack<Boolean>() {
        /**
         * @param isStop 急停按钮是否开启
         */
        @Override
        public void onResult(Boolean isStop) {
        }
    });
    ```
    	
    - `isMoveing(ResultCallBack<Boolean> callBack)`判断底盘是否正在运动;
    
    ```
    /**
     * @param callBack 获取底盘移动回调
     */
    IBenMoveSDK.getInstance().isMoveing(new IBenMoveSDK.ResultCallBack<Boolean>() {
        /**
         * @param isMove 目前是否在移动
         */
        @Override
        public void onResult(Boolean isMove) {
        }
    });
    ```

10. **`IBenPrintSDK`关于机器人打印机工具类**

    - `initPrinter(Context context)`初始化打印机，开启连接;
    
    ```
    /**
     * 初始化打印机，开启连接
     *
     * @param context 上下文
     */
    IBenPrintSDK.getInstance().initPrinter(context);
	```

    - `closePrint()`关闭打印机连接;

    ```
    /**
     * 关闭打印机连接
     */
    IBenPrintSDK.getInstance().closePrint();
	```
    
    - `isConnect()`判断打印机是否已连接;

    ```
    /**
     * 判断打印机是否已连接
     * 
     * @return 当前打印机是否已连接
     */
    IBenPrintSDK.getInstance().isConnect();
	```
    
    - `getCurrentStatus()`获取打印机当前状态;

    ```
    /**
     * 获取打印机当前状态
     * 
     * @return 当前打印机的状态
     */
    IBenPrintSDK.getInstance().getCurrentStatus();
	```
    - `setPrinter(int value)`设置打印位置;
    - `setFont(int size)`设置字体大小;     
    - `printText(String content)`设置打印文字内容;
    
    ```
    /**
     * 设置打印文字字体大小和内容
     * 
     * @param value 打印位置
     * @param size 字体大小
     * @param content 文本内容
     */
    IBenPrintSDK.getInstance().setPrinter(value)
                            .setFont(size)
                            .printText(content);
	```
    
    - `printColorImg2Gray(Bitmap bitmap, PrinterConstants.PAlign type)`设置打印灰度图片内容;
    
    ```
    /**
     * 设置打印灰度图片内容
     * 
     * @param bitmap 图片
     * @param type 图片位置
     */ 
    IBenPrintSDK.getInstance().printColorImg2Gray(bitmap, type);
	```
    
    - `cutPaper()`打印结束切纸;

    ```
    /**
     * 打印结束切纸
     */
    IBenPrintSDK.getInstance().cutPaper();
	```
