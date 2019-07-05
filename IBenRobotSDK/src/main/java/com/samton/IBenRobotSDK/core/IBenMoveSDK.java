package com.samton.IBenRobotSDK.core;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;

import com.samton.IBenRobotSDK.data.Constants;
import com.samton.IBenRobotSDK.utils.FileIOUtils;
import com.samton.IBenRobotSDK.utils.FileUtils;
import com.samton.IBenRobotSDK.utils.ImageUtils;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.slamtec.slamware.SlamwareCorePlatform;
import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.action.IAction;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.robot.CompositeMap;
import com.slamtec.slamware.robot.DockingStatus;
import com.slamtec.slamware.robot.GridMap;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Map;
import com.slamtec.slamware.robot.MapKind;
import com.slamtec.slamware.robot.MapLayer;
import com.slamtec.slamware.robot.MapType;
import com.slamtec.slamware.robot.MoveOption;
import com.slamtec.slamware.robot.Pose;
import com.slamtec.slamware.robot.PowerStatus;
import com.slamtec.slamware.robot.Rotation;
import com.slamtec.slamware.sdp.CompositeMapHelper;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * <pre>
 *     @author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/10/18
 *     desc   : 机器人移动SDK
 *     version: 1.0
 * </pre>
 */

public final class IBenMoveSDK {
    /**
     * 单例
     */
    private static IBenMoveSDK mInstance = null;
    /**
     * 思岚SDK平台对象
     */
    private SlamwareCorePlatform mRobotPlatform;
    /**
     * 点位的Disposable
     */
    private Disposable mLocationSubscribe;
    /**
     * 重连的Disposable
     */
    private Disposable mConnectSubscribe;
    /**
     * 移动的Disposable
     */
    private Disposable mMoveSubscribe;
    /**
     * 旋转的Disposable
     */
    private Disposable mRotateSubscribe;
    /**
     * 是否低电量模式
     */
    private boolean isWarnPower = false;

    /**
     * 私有构造
     */
    private IBenMoveSDK() {
    }

    /**
     * 获取机器人移动SDK单例
     *
     * @return 机器人移动SDK单例
     */
    public static IBenMoveSDK getInstance() {
        if (mInstance == null) {
            synchronized (IBenMoveSDK.class) {
                if (mInstance == null) {
                    mInstance = new IBenMoveSDK();
                }
            }
        }
        return mInstance;
    }

    /**
     * 连接机器人
     *
     * @param ip       IP
     * @param port     端口
     * @param callBack 连接回调
     */
    @SuppressLint("CheckResult")
    public void connectRobot(String ip, int port, ConnectCallBack callBack) {
        if (callBack == null) return;
        // 判断ip和端口是否合法
        if (TextUtils.isEmpty(ip) || port <= 0 || port > 65535) {
            callBack.onConnectFailed();
        } else {
            Observable.create((ObservableOnSubscribe<Boolean>) e -> {
                // 设置机器人连接监听
                mRobotPlatform = SlamwareCorePlatform.connect(ip, port);
                // 获取机器人电量
                LogUtils.e("当前机器人电量>>>" + mRobotPlatform.getBatteryPercentage());
                e.onNext(true);
                e.onComplete();
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aBoolean -> {
                        // 回调机器人连接成功
                        callBack.onConnectSuccess();
                        // 取消重连计时器
                        cancelReconnectTimer();
                    }, throwable -> {
                        callBack.onConnectFailed();
                        // 这里进行三秒重连
                        startReconnectTimer(ip, port, callBack);
                    });
        }
    }

    /**
     * 开启重连计时器
     */
    private void startReconnectTimer(String ip, int port, ConnectCallBack callBack) {
        // 取消重连计时器
        cancelReconnectTimer();
        // 开始三秒后进行重连
        mConnectSubscribe = Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> connectRobot(ip, port, callBack));
    }

    /**
     * 取消重连计时器
     */
    private void cancelReconnectTimer() {
        if (mConnectSubscribe != null && !mConnectSubscribe.isDisposed()) {
            mConnectSubscribe.dispose();
            mConnectSubscribe = null;
        }
    }

    /**
     * 断开机器人连接
     */
    @SuppressLint("CheckResult")
    public void disConnectRobot() {
        // 取消重连计时器
        cancelReconnectTimer();
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            mRobotPlatform.disconnect();
            e.onNext(true);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("与机器人断开连接成功"),
                        Throwable::printStackTrace);
    }

    /**
     * 设置是否开启地图更新
     *
     * @param isUpdate 是或者否
     */
    @SuppressLint("CheckResult")
    public void setMapUpdate(boolean isUpdate) {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            mRobotPlatform.setMapUpdate(isUpdate);
            e.onNext(true);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("设置地图更新状态成功"),
                        throwable -> LogUtils.d("设置地图更新状态失败"));
    }

    /**
     * 清除地图
     */
    @SuppressLint("CheckResult")
    public void removeMap() {
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            mRobotPlatform.clearMap();
            e.onNext(true);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("清除地图状态成功"),
                        throwable -> LogUtils.d("清除地图状态失败"));
    }

    /**
     * 获取电池信息
     *
     * @param callBack 电池信息回调
     */
    @SuppressLint("CheckResult")
    public void getBatteryInfo(GetBatteryCallBack callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<String>) e -> {
            JSONObject jsonObject = new JSONObject();
            int percent = mRobotPlatform.getBatteryPercentage();
            boolean isCharging = mRobotPlatform.getBatteryIsCharging();
            jsonObject.put("batteryPercent", percent);
            jsonObject.put("isCharging", isCharging);
            e.onNext(jsonObject.toString());
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onSuccess,
                        throwable -> callBack.onFailed());
    }

    /**
     * 设置是否低电量模式
     *
     * @param warnPower 是否是低电量
     */
    public void setWarnPower(boolean warnPower) {
        isWarnPower = warnPower;
    }

    /**
     * 获取当前是否是低电量模式
     *
     * @return 是否是低电量
     */
    public boolean isWarnPower() {
        return isWarnPower;
    }

    /**
     * 获取当前点坐标
     *
     * @return 当前点坐标
     */
    @SuppressLint("CheckResult")
    public void getLocation(ResultCallBack<Location> resultCallBack) {
        if (resultCallBack == null) return;
        Observable.create((ObservableOnSubscribe<Location>) e -> {
            Location location = mRobotPlatform.getLocation();
            e.onNext(location);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultCallBack::onResult,
                        throwable -> resultCallBack.onResult(null));
    }

    /**
     * 获取机器人姿态
     *
     * @return 当前点姿态
     */
    @SuppressLint("CheckResult")
    public void getPose(ResultCallBack<Pose> resultCallBack) {
        if (resultCallBack == null) return;
        Observable.create((ObservableOnSubscribe<Pose>) e -> {
            Pose pose = mRobotPlatform.getPose();
            e.onNext(pose);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultCallBack::onResult,
                        throwable -> resultCallBack.onResult(null));
    }

    /**
     * 设置机器人的姿态
     *
     * @param pose 当前姿态
     */
    @SuppressLint("CheckResult")
    private void setPose(Pose pose, StopBtnState btnState) {
        if (pose == null || btnState == null) return;
        //先进行急停状态判断
        hasSystemEmergencyStop(isStop -> {
            if (isStop) {
                btnState.isOnEmergencyStop(true);
            } else {
                Observable.create((ObservableOnSubscribe<Boolean>) e -> {
                    mRobotPlatform.setPose(pose);
                    e.onNext(true);
                    e.onComplete();
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> LogUtils.d("设置机器人姿态成功"),
                                throwable -> LogUtils.d("设置机器人姿态失败"));
            }
        });
    }

    /**
     * 根据方向进行移动和间隔持续移动(默认300毫秒间隔)
     *
     * @param direction 方向
     */
    public void moveByDirection(MoveDirection direction, StopBtnState btnState) {
        moveByDirection(direction, 300, btnState);
    }

    /**
     * 根据方向进行移动和间隔持续移动
     *
     * @param direction 方向
     * @param period    间隔
     */
    public void moveByDirection(MoveDirection direction, long period, StopBtnState btnState) {
        //如果是低电量模式直接返回
        if (direction == null || btnState == null || isWarnPower) return;
        //取消所有动作
        cancelAllActions();
        //进行急停状态判断
        hasSystemEmergencyStop(isStop -> {
            if (isStop) {//如果开启急停直接回调
                btnState.isOnEmergencyStop(true);
            } else {// 开启运动计时器，定时移动
                startMoveTimer(direction, period);
            }
        });
    }

    /**
     * 开启移动定时器
     *
     * @param direction 方向
     * @param period    周期
     */
    private void startMoveTimer(MoveDirection direction, Long period) {
        // 首先停止之前的移动定时器
        cancelMoveTimer();
        isHome(isHome -> {
            if (isHome && direction != MoveDirection.FORWARD) return;
            mMoveSubscribe = Observable.interval(0, period, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .flatMap((Function<Long, ObservableSource<IMoveAction>>) aLong ->
                            Observable.create((ObservableOnSubscribe<IMoveAction>) e -> {
                                LogUtils.d("当前机器人正在移动");
                                IMoveAction action = mRobotPlatform.moveBy(direction);
                                e.onNext(action);
                            }).subscribeOn(Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(action -> LogUtils.d("机器人移动成功"),
                            throwable -> LogUtils.d("机器人移动失败"));
        });
    }

    /**
     * 取消移动计时器
     */
    private void cancelMoveTimer() {
        if (mMoveSubscribe != null && !mMoveSubscribe.isDisposed()) {
            mMoveSubscribe.dispose();
            mMoveSubscribe = null;
        }
    }

    /**
     * 旋转机器人
     *
     * @param angle 需要旋转的角度
     */
    @SuppressLint("CheckResult")
    public void rotate(double angle, StopBtnState btnState) {
        //判断机器人当前是否正在低电量状态
        if (btnState == null || isWarnPower) return;
        //判断机器人是否正在充电桩
        isHome(isHome -> {
            LogUtils.d("当前旋转机器人机器人是否在充电桩:" + isHome);
            if (isHome) return;
            //取消所有动作
            cancelAllActions();
            //判断机器人当前急停状态
            hasSystemEmergencyStop(isStop -> {
                if (isStop) {
                    btnState.isOnEmergencyStop(true);
                } else {
                    Observable.create((ObservableOnSubscribe<IMoveAction>) e -> {
                        float tempAngle = (float) (angle / 180 * Math.PI);
                        Rotation rotation = new Rotation(tempAngle);
                        IMoveAction action = mRobotPlatform.rotate(rotation);
                        e.onNext(action);
                        e.onComplete();
                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(action -> LogUtils.d("机器人旋转角度成功"),
                                    throwable -> LogUtils.d("机器人旋转角度失败"));
                }
            });
        });
    }

    /**
     * 回充电桩
     */
    @SuppressLint("CheckResult")
    public void goHome(MoveCallBack callBack, StopBtnState btnState) {
        if (callBack == null || btnState == null) return;
        //取消所有动作
        cancelAllActions();
        //判断当前机器人的急停状态
        hasSystemEmergencyStop(isStop -> {
            if (isStop) {
                btnState.isOnEmergencyStop(true);
            } else {
                Observable.create((ObservableOnSubscribe<IAction>) e -> {
                    DockingStatus dockingStatus = mRobotPlatform.getPowerStatus().getDockingStatus();
                    boolean isHome = dockingStatus == DockingStatus.OnDock;
                    LogUtils.d("当前执行回充电桩操作机器人是否在充电桩:" + isHome);
                    if (isHome) {
                        callBack.onStateChange(ActionStatus.FINISHED);
                        e.onComplete();
                    } else {
                        IAction action = mRobotPlatform.goHome();
                        e.onNext(action);
                        e.onComplete();
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(iAction -> startLocationTimer(iAction, -99, callBack, btnState),
                                throwable -> callBack.onStateChange(ActionStatus.ERROR));
            }
        });
    }

    /**
     * 行走到指定点
     *
     * @param location 定点对象
     * @param yaw      角度
     * @param callBack 回调
     */
    @SuppressLint("CheckResult")
    public void goLocation(Location location, float yaw, MoveCallBack callBack, StopBtnState btnState) {
        //如果是低电量模式直接返回
        if (isWarnPower || location == null || callBack == null || btnState == null)
            return;
        //取消所有动作
        cancelAllActions();
        //如果开启急停返回
        hasSystemEmergencyStop(isStop -> {
            if (isStop) {
                btnState.isOnEmergencyStop(true);
            } else {
                // 然后执行行走至定点操作
                Observable.create((ObservableOnSubscribe<IAction>) e -> {
                    // 判断机器人当前是否正在充电桩
                    DockingStatus dockingStatus = mRobotPlatform.getPowerStatus().getDockingStatus();
                    boolean isHome = dockingStatus == DockingStatus.OnDock;
                    LogUtils.d("当前去定点状态机器人是否在充电桩:" + isHome);
                    if (isHome) {
                        moveByDirection(MoveDirection.FORWARD, 300, btnState);
                        SystemClock.sleep(1000);
                        cancelAllActions();
                    }
                    MoveOption option = new MoveOption();
                    option.setSpeedRatio(0.4);
                    option.setWithYaw(false);
                    option.setPrecise(true);
                    option.setMilestone(true);
                    // 执行行走指令
                    IAction action = mRobotPlatform.moveTo(location, option, yaw);
                    e.onNext(action);
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(action -> {
                            // 创建定点回调计时器
                            startLocationTimer(action, yaw, callBack, btnState);
                        }, Throwable::printStackTrace);
            }
        });
    }

    /**
     * 停止所有动作
     */
    @SuppressLint("CheckResult")
    public void cancelAllActions() {
        // 取消移动计时器
        cancelMoveTimer();
        // 取消去定点计时器
        cancelLocationTimer();
        // 取消转动计时器
        cancelRotate();
        // 调用底盘取消当前动作
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            mRobotPlatform.getCurrentAction().cancel();
            e.onNext(true);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("机器人停止所有动作"),
                        Throwable::printStackTrace);
    }

    /**
     * 清除所有虚拟墙
     */
    @SuppressLint("CheckResult")
    public void clearAllWalls() {
        if (mRobotPlatform == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            mRobotPlatform.clearWalls();
            e.onNext(true);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("机器人清除虚拟墙成功"),
                        throwable -> LogUtils.d("机器人清除虚拟墙失败"));
    }

    /**
     * 判断机器人是否是无线充电状态
     *
     * @return 是否在无线充电状态
     */
    @SuppressLint("CheckResult")
    public void isHome(ResultCallBack<Boolean> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            DockingStatus dockingStatus = mRobotPlatform.getPowerStatus().getDockingStatus();
            boolean isHome = dockingStatus == DockingStatus.OnDock;
            e.onNext(isHome);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onResult,
                        throwable -> callBack.onResult(false));
    }

    /**
     * 查询机器人电池状态
     *
     * @return int:0-未在充电,1-线充,2-桩充
     */
    @SuppressLint("CheckResult")
    public void getPowerStatus(ResultCallBack<Integer> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Integer>) e -> {
            int powerStatus = 0;
            PowerStatus status = mRobotPlatform.getPowerStatus();
            if (status.isCharging()) {
                // 直流电源连接
                if (status.isDCConnected()) {
                    powerStatus = 1;
                } else {
                    powerStatus = 2;
                }
            }
            e.onNext(powerStatus);
            e.onComplete();
        }).subscribe(callBack::onResult,
                throwable -> callBack.onResult(0));
    }

    /**
     * 保存地图
     *
     * @param mapName  要保存的地图名字
     * @param callBack 回调函数
     */
    @SuppressLint("CheckResult")
    public void saveMap(String mapName, MapCallBack callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            // 生成保存路径
            String path = Constants.MAP_PATH + "/" + mapName;
            // 获取完全版地图并保存到指定路径中
            CompositeMap compositeMap = mRobotPlatform.getCompositeMap();
            CompositeMapHelper mapHelper = new CompositeMapHelper();
            mapHelper.saveFile(path, compositeMap);
            // 生成缩略图
            FileUtils.createOrExistsFile(Constants.MAP_PATH_THUMB + "/" + mapName + ".jpg");
            // 写入BMP图像
            for (MapLayer layer : compositeMap.getMaps()) {
                if (layer instanceof GridMap) {
                    GridMap map = ((GridMap) layer);
                    int width = map.getDimension().getWidth();
                    int height = map.getDimension().getHeight();
                    Bitmap bitmap = createImage(map.getMapData(), width, height);
                    byte[] bytes = ImageUtils.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG);
                    FileIOUtils.writeFileFromBytesByStream(FileUtils.getFileByPath(
                            Constants.MAP_PATH_THUMB + "/" + mapName + ".jpg"), bytes);
                }
            }
            e.onNext(true);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> callBack.onSuccess(),
                        throwable -> callBack.onFailed());
    }

    /**
     * 创建bitmap对象
     *
     * @param buffer 数据流
     * @param width  宽
     * @param height 高
     * @return 创建好的BitMap对象
     */
    @SuppressWarnings("NumericOverflow")
    private Bitmap createImage(byte[] buffer, int width, int height) {
        int[] rawData = new int[buffer.length];
        for (int i = 0; i < buffer.length; i++) {
            int temp = 0x7f + buffer[i];
            rawData[i] = Color.rgb(temp, temp, temp);
        }
        return Bitmap.createBitmap(rawData, width, height, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
    }

    /**
     * 根据地图名字加载地图
     *
     * @param mapNamePath 保存的地图文件路径
     * @param currentPose 当前位置的姿态信息
     * @param callBack    回调函数
     */
    @SuppressLint("CheckResult")
    public void loadMap(String mapNamePath, Pose currentPose, MapCallBack callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            // 地图加载帮助对象
            CompositeMapHelper helper = new CompositeMapHelper();
            CompositeMap map = helper.loadFile(mapNamePath);
            // 地图不为空的话加载地图
            mRobotPlatform.setCompositeMap(map, currentPose == null ? new Pose() : currentPose);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> callBack.onSuccess(),
                        throwable -> callBack.onFailed());
    }


    /**
     * 获取地图
     *
     * @return 返回地图
     */
    @SuppressLint("CheckResult")
    private void getMap(ResultCallBack<Map> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Map>) e -> {
            // 地图类型为8位位图
            MapType mapType = MapType.BITMAP_8BIT;
            // 地图种类为扫描建图
            MapKind mapKind = MapKind.EXPLORE_MAP;
            // 获取地图
            Map map = mRobotPlatform.getMap(mapType, mapKind, mRobotPlatform.getKnownArea(mapType));
            e.onNext(map);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onResult,
                        throwable -> callBack.onResult(null));
    }

    /**
     * 开启定位计时器
     *
     * @param callBack 回调
     */
    private void startLocationTimer(IAction action, float yaw, MoveCallBack callBack, StopBtnState btnState) {
        // 首先停止之前的定时任务
        cancelLocationTimer();
        // 停止旋转指令
        cancelRotate();
        mLocationSubscribe = Observable.create((ObservableOnSubscribe<ActionStatus>) e -> {
            while (true) {
                //判断当前的急停状态
                boolean hasSystemEmergencyStop = mRobotPlatform.getRobotHealth().getHasSystemEmergencyStop();
                if (hasSystemEmergencyStop) {
                    // 停止定位计时器
                    cancelLocationTimer();
                    btnState.isOnEmergencyStop(true);
                    break;
                } else {
                    ActionStatus currentStatus = action.getStatus();
                    if (currentStatus.equals(ActionStatus.FINISHED) || currentStatus.equals(ActionStatus.STOPPED)
                            || currentStatus.equals(ActionStatus.ERROR)) {
                        if (yaw == -99 || !currentStatus.equals(ActionStatus.FINISHED)) {
                            // 回调状态值
                            e.onNext(currentStatus);
                            e.onComplete();
                        } else {
                            // 停止定位计时器
                            cancelLocationTimer();
                            rotate(yaw, callBack, btnState);
                        }
                        break;
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onStateChange,
                        throwable -> callBack.onStateChange(ActionStatus.ERROR));
    }

    /**
     * 取消定位计时器
     */
    private void cancelLocationTimer() {
        if (mLocationSubscribe != null && !mLocationSubscribe.isDisposed()) {
            mLocationSubscribe.dispose();
            mLocationSubscribe = null;
        }
    }

    /**
     * 根据偏移量转动角度
     *
     * @param yaw 偏移量
     */
    @SuppressLint("CheckResult")
    private void rotate(float yaw, MoveCallBack callBack, StopBtnState btnState) {
        if (callBack == null || btnState == null) return;
        //判断急停状态是否开启
        hasSystemEmergencyStop(isStop -> {
            if (isStop) {//如果开启急停,停止转动倒计时,并且将急停状态回传
                cancelRotate();
                btnState.isOnEmergencyStop(true);
            } else {//否则进行旋转角度进度监听
                Rotation rotation = new Rotation(yaw);
                cancelRotate();
                mRotateSubscribe = Observable.create((ObservableOnSubscribe<ActionStatus>) e -> {
                    IMoveAction moveAction = mRobotPlatform.rotateTo(rotation);
                    while (true) {
                        if (moveAction == null) break;
                        ActionStatus status = moveAction.getStatus();
                        if (status.equals(ActionStatus.FINISHED) || status.equals(ActionStatus.STOPPED)
                                || status.equals(ActionStatus.ERROR)) {
                            e.onNext(status);
                            e.onComplete();
                            break;
                        }
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(callBack::onStateChange,
                                throwable -> callBack.onStateChange(ActionStatus.ERROR));
            }
        });
    }

    /**
     * 取消旋转计时器
     */
    private void cancelRotate() {
        if (mRotateSubscribe != null && !mRotateSubscribe.isDisposed()) {
            mRotateSubscribe.dispose();
            mRotateSubscribe = null;
        }
    }

    /**
     * 判断底盘急停按钮是否开启
     */
    @SuppressLint("CheckResult")
    public void hasSystemEmergencyStop(ResultCallBack<Boolean> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            boolean hasSystemEmergencyStop = mRobotPlatform.getRobotHealth().getHasSystemEmergencyStop();
            e.onNext(hasSystemEmergencyStop);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onResult,
                        throwable -> callBack.onResult(true));
    }

    /**
     * 判断底盘是否正在运动
     */
    @SuppressLint("CheckResult")
    public void isMoveing(ResultCallBack<Boolean> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            ActionStatus status = mRobotPlatform.getCurrentAction().getStatus();
            boolean isMove = status == ActionStatus.WAITING_FOR_START || status == ActionStatus.RUNNING;
            e.onNext(isMove);
            e.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onResult,
                        throwable -> callBack.onResult(false));
    }

    /**
     * 机器人连接回调
     */
    public interface ConnectCallBack {
        /**
         * 连接成功
         */
        void onConnectSuccess();

        /**
         * 连接失败
         */
        void onConnectFailed();
    }

    /**
     * 保存地图回调
     */
    public interface MapCallBack {
        /**
         * 保存成功
         */
        void onSuccess();

        /**
         * 保存失败
         */
        void onFailed();
    }

    /**
     * 急停按钮状态回调
     */
    public interface StopBtnState {
        /**
         * 按钮状态回调
         *
         * @param isOn 是否开启
         */
        void isOnEmergencyStop(boolean isOn);
    }

    /**
     * 动作回调
     */
    public interface MoveCallBack {
        /**
         * 机器人状态变更
         *
         * @param status 状态值(思岚ActionStatus枚举)
         */
        void onStateChange(ActionStatus status);
    }

    /**
     * 电量信息回调
     */
    public interface GetBatteryCallBack {
        /**
         * 获取电量成功
         *
         * @param msg 电量信息
         */
        void onSuccess(String msg);

        /**
         * 获取电量失败
         */
        void onFailed();
    }

    /**
     * 返回结果的CallBack
     *
     * @param <T> 范型的结果
     */
    public interface ResultCallBack<T> {
        void onResult(T t);
    }
}
