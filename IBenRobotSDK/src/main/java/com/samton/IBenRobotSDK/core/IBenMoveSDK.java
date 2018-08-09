package com.samton.IBenRobotSDK.core;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.util.Log;

import com.samton.IBenRobotSDK.data.Constants;
import com.samton.IBenRobotSDK.utils.BaseIoSubscriber;
import com.samton.IBenRobotSDK.utils.FileIOUtils;
import com.samton.IBenRobotSDK.utils.FileUtils;
import com.samton.IBenRobotSDK.utils.ImageUtils;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.slamtec.slamware.SlamwareCorePlatform;
import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.action.IAction;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.OperationFailException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.geometry.PointF;
import com.slamtec.slamware.geometry.Size;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
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
    private static IBenMoveSDK instance = null;
    /**
     * 思岚SDK平台对象
     */
    private SlamwareCorePlatform mRobotPlatform;
    /**
     * 定位计时器
     */
    private Timer mLocationTimer;
    private TimerTask mLocationTask;
    /**
     * 移动计时器
     */
    private Timer mMoveTimer;
    private TimerTask mMoveTask;
    /**
     * 重连计时器
     */
    private Timer mReconnectTimer;
    private TimerTask mReconnectTask;
    /**
     * 定位动作
     */
    private IAction mLocationAction;
    /**
     * 是否已经连接机器人
     */
    private boolean isConnected = false;
    /**
     * 电池电量信息
     */
    private String mBatteryInfo = null;
    /**
     * 机器人状态回调接口
     */
    private ConnectCallBack mCallBack;
    /**
     * 记录IP
     */
    private String mIp = "";
    /**
     * 记录端口
     */
    private int mPort = 0;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 动作接口
     */
    private IMoveAction moveAction;

    /**
     * 私有构造
     */
    private IBenMoveSDK() {
        // 初始化线程池
        mThreadPool = Executors.newFixedThreadPool(10);
    }

    /**
     * 获取机器人移动SDK单例
     *
     * @return 机器人移动SDK单例
     */
    public static IBenMoveSDK getInstance() {
        if (instance == null) {
            synchronized (IBenMoveSDK.class) {
                if (instance == null) {
                    instance = new IBenMoveSDK();
                }
            }
        }
        return instance;
    }

    /**
     * 回充电桩
     */
    public void goHome(MoveCallBack callBack) {
        if (mRobotPlatform != null) {
            // 停止当前所有动作
            cancelAllActions();
            // 沉睡0.3秒
            SystemClock.sleep(300);
            try {
                // 让机器人回充电桩
                mLocationAction = mRobotPlatform.goHome();
                // 创建回桩状态定时器
                startLocationTimer(-99, callBack);
            } catch (Throwable throwable) {
                onRequestError();
            }
        } else {
            onRequestError();
        }
    }

    /**
     * 请求失败
     */
    private void onRequestError() {
        if (isConnected) {
            cancelAllActions();
        }
        if (null != mCallBack) {
            mCallBack.onConnectFailed();
        }
    }

    /**
     * 获取机器人连接状态
     *
     * @return 机器人连接状态
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 连接机器人
     *
     * @param ip       IP
     * @param port     端口
     * @param callBack 连接回调
     */
    public void connectRobot(final String ip, final int port, @NonNull final ConnectCallBack callBack) {
        mCallBack = callBack;
        mIp = ip;
        mPort = port;
        // 判断ip和端口是否合法
        if (mIp == null || mIp.isEmpty() || mPort <= 0 || mPort > 65535) {
            mCallBack.onConnectFailed();
        }
        // 合法的话连接机器人
        else {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        // 设置机器人连接监听
                        mRobotPlatform = SlamwareCorePlatform.connect(ip, port);
                        // 获取机器人电量
                        LogUtils.e("当前机器人电量>>>" + mRobotPlatform.getBatteryPercentage());
                        // 发射命令-告知观察者连接成功
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.single()).observeOn(Schedulers.single()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (aBoolean) {
                        // 如果存在重连的话取消
                        cancelReconnectTimer();
                        // 标识位重置
                        isConnected = true;
                        // 回调机器人连接成功
                        mCallBack.onConnectSuccess();
                    } else {
                        isConnected = false;
                        mCallBack.onConnectFailed();
                        // 重连
                        startReconnectTimer();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        }

    }

    /**
     * 重连机器人
     */
    public void reconnect() {
        startReconnectTimer();
    }

    /**
     * 断开机器人连接
     */
    public void disconnectRobot() {
        // 判断机器人控制SDK是否为空
        if (mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        mRobotPlatform.disconnect();
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.single()).observeOn(Schedulers.single()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (!aBoolean) {
                        onRequestError();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        } else {
            onRequestError();
        }
    }

    /**
     * 设置是否开启地图更新
     *
     * @param isUpdate 是或者否
     */
    public void setMapUpdate(boolean isUpdate) {
        if (mRobotPlatform != null) {
            try {
                mRobotPlatform.setMapUpdate(isUpdate);
            } catch (Throwable throwable) {
                onRequestError();
            }

        } else {
            onRequestError();
        }
    }

    /**
     * 清除地图
     */
    public void removeMap() {
        if (mRobotPlatform != null) {
            try {
                mRobotPlatform.clearMap();
            } catch (Throwable throwable) {
                onRequestError();
            }
        } else {
            onRequestError();
        }
    }

    /**
     * 获取电池信息
     *
     * @param callBack 电池信息回调
     */
    public void getBatteryInfo(@NonNull final GetBatteryCallBack callBack) {
        if (mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        int percent = mRobotPlatform.getBatteryPercentage();
                        boolean isCharging = mRobotPlatform.getBatteryIsCharging();
                        jsonObject.put("batteryPercent", percent);
                        jsonObject.put("isCharging", isCharging);
                        mBatteryInfo = jsonObject.toString();
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.from(mThreadPool)).observeOn(Schedulers.from(mThreadPool)).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (!aBoolean) {
                        onRequestError();
                        callBack.onFailed();
                    } else {
                        callBack.onSuccess(mBatteryInfo);
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        } else {
            onRequestError();
            callBack.onFailed();
            mBatteryInfo = "";
        }
    }

    /**
     * 根据方向进行移动
     *
     * @param direction 方向
     */
    public void moveByDirection(MoveDirection direction) {
        cancelAllActions();
        final MoveDirection moveDirection = direction;
        if (moveDirection != null && mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        mRobotPlatform.moveBy(moveDirection);
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.from(mThreadPool)).observeOn(Schedulers.from(mThreadPool)).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (!aBoolean) {
                        onRequestError();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        } else {
            onRequestError();
        }

    }

    /**
     * 根据方向进行移动和间隔持续移动
     *
     * @param direction 方向
     * @param period    间隔
     */
    public void moveByDirection(MoveDirection direction, long period) {
        cancelAllActions();
        if (direction != null && mRobotPlatform != null) {
            // 开启运动计时器，定时移动
            startMoveTimer(direction, period);
        } else {
            onRequestError();
        }
    }

    /**
     * 旋转机器人
     *
     * @param angle 需要旋转的角度
     */
    public void rotate(double angle) {
        cancelAllActions();
        float tempAngle = (float) (angle / 180 * Math.PI);
        final Rotation rotation = new Rotation(tempAngle);
        if (mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        mRobotPlatform.rotate(rotation);
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.from(mThreadPool)).observeOn(Schedulers.from(mThreadPool)).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (!aBoolean) {
                        onRequestError();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        } else {
            onRequestError();
        }
    }

    /**
     * 旋转机器人
     *
     * @param rotation 角度
     */
    public void rotate(final Rotation rotation) {
        cancelAllActions();
        if (mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        mRobotPlatform.rotate(rotation);
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.from(mThreadPool)).observeOn(Schedulers.from(mThreadPool)).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (!aBoolean) {
                        onRequestError();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        } else {
            onRequestError();
        }
    }

    /**
     * 根据偏移量转动角度
     *
     * @param yaw 偏移量
     */
    public void rotate(final float yaw) {
        cancelAllActions();
        final Rotation rotation = new Rotation(yaw);
        if (mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        mRobotPlatform.rotate(rotation);
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.from(mThreadPool)).observeOn(Schedulers.from(mThreadPool)).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (!aBoolean) {
                        onRequestError();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        } else {
            onRequestError();
        }
    }

    public IMoveAction rotate2(float yaw){
        Rotation rotation = new Rotation(yaw);
        IMoveAction action = null;
        if(mRobotPlatform!=null){
            try {
                action = mRobotPlatform.rotate(rotation);
            } catch (RequestFailException e) {
                e.printStackTrace();
            } catch (ConnectionFailException e) {
                e.printStackTrace();
            } catch (ConnectionTimeOutException e) {
                e.printStackTrace();
            } catch (UnauthorizedRequestException e) {
                e.printStackTrace();
            } catch (UnsupportedCommandException e) {
                e.printStackTrace();
            } catch (ParseInvalidException e) {
                e.printStackTrace();
            } catch (InvalidArgumentException e) {
                e.printStackTrace();
            } catch (OperationFailException e) {
                e.printStackTrace();
            }

            return action;
        }else{
            return action;
        }
    }

    /**
     * 停止所有动作
     */
    public void cancelAllActions() {
        // 取消所有的计时任务
        cancelMoveTimer();
        cancelLocationTimer();
        if (mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    IMoveAction moveAction = mRobotPlatform.getCurrentAction();
                    try {
                        if (moveAction != null) {
                            moveAction.cancel();
                        }
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.from(mThreadPool)).observeOn(Schedulers.from(mThreadPool)).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (!aBoolean) {
                        onRequestError();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        } else {
            onRequestError();
        }
    }

    /**
     * 获取当前点坐标
     *
     * @return 当前点坐标
     */
    public Location getLocation() {
        if (mRobotPlatform != null) {
            try {
                return mRobotPlatform.getLocation();
            } catch (Throwable throwable) {
                onRequestError();
                return null;
            }
        } else {
            onRequestError();
            return null;
        }

    }

    /**
     * 获取机器人姿态
     *
     * @return 当前点姿态
     */
    public Pose getPose() {
        if (mRobotPlatform != null) {
            try {
                return mRobotPlatform.getPose();
            } catch (Throwable throwable) {
                onRequestError();
                return null;
            }
        } else {
            onRequestError();
            return null;
        }
    }

    /**
     * 设置机器人的姿态
     *
     * @param pose 当前姿态
     */
    private void setPose(final Pose pose) {
        if (pose != null) {
            if (mRobotPlatform != null) {
                Observable.create(new ObservableOnSubscribe<Boolean>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                        try {
                            mRobotPlatform.setPose(pose);
                            e.onNext(true);
                        } catch (Throwable throwable) {
                            e.onNext(false);
                        }
                    }
                }).subscribeOn(Schedulers.from(mThreadPool)).observeOn(Schedulers.from(mThreadPool)).subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (!aBoolean) {
                            onRequestError();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        onRequestError();
                    }
                });
            } else {
                onRequestError();
            }
        }
    }

    /**
     * 行走到指定点
     *
     * @param location 定点对象
     * @param yaw      角度
     * @param callBack 回调
     */
    public void old_go2Location(final Location location, final float yaw, final MoveCallBack callBack) {
        if (mRobotPlatform != null) {
            // 首先停止所有动作
            cancelAllActions();
            // 然后执行行走至定点操作
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        if (location != null) {
                            // 脱桩操作
                            if (isHome()) {
                                moveByDirection(MoveDirection.FORWARD, 300);
                                SystemClock.sleep(1000);
                                cancelAllActions();
                                SystemClock.sleep(500);
                            }
                            MoveOption option = new MoveOption();
                            option.setSpeedRatio(0.4);
                            option.setWithYaw(true);
                            // 执行行走指令
                            mLocationAction = mRobotPlatform.moveTo(location, option, yaw);
                            e.onNext(true);
                        }
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            })
                    .subscribeOn(Schedulers.from(mThreadPool))
                    .observeOn(Schedulers.from(mThreadPool))
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable disposable) {

                        }

                        @Override
                        public void onNext(@NonNull Boolean aBoolean) {
                            if (!aBoolean) {
                                onRequestError();
                            } else {
                                // 创建定点回调计时器
                                startLocationTimer(yaw, callBack);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            onRequestError();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            onRequestError();
        }
    }

    /**
     * 行走到指定点
     *
     * @param location 定点对象
     * @param yaw      角度
     * @param callBack 回调
     */
    public void go2Location(final Location location, final float yaw, final MoveCallBack callBack) {
        if (mRobotPlatform != null) {
            // 首先停止所有动作
            cancelAllActions();
            // 然后执行行走至定点操作
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        if (location != null) {
                            // 脱桩操作
                            if (isHome()) {
                                moveByDirection(MoveDirection.FORWARD, 300);
                                SystemClock.sleep(1000);
                                cancelAllActions();
                                SystemClock.sleep(500);
                            }
                            MoveOption option = new MoveOption();
                            option.setSpeedRatio(0.4);
                            option.setWithYaw(false);
                            option.setPrecise(true);
                            option.setMilestone(true);
                            // 执行行走指令
                            mLocationAction = mRobotPlatform.moveTo(location, option, yaw);
                            e.onNext(true);
                        }
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            })
                    .subscribeOn(Schedulers.from(mThreadPool))
                    .observeOn(Schedulers.from(mThreadPool))
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable disposable) {

                        }

                        @Override
                        public void onNext(@NonNull Boolean aBoolean) {
                            if (!aBoolean) {
                                onRequestError();
                            } else {
                                // 创建定点回调计时器
                                startLocationTimer(yaw, callBack);
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            onRequestError();
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } else {
            onRequestError();
        }
    }

    /**
     * 清除所有虚拟墙
     */
    public void clearAllWalls() {
        if (mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        mRobotPlatform.clearWalls();
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.single()).observeOn(Schedulers.single()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    if (!aBoolean) {
                        onRequestError();
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                }
            });
        } else {
            onRequestError();
        }
    }

    /**
     * 判断机器人是否是无线充电状态
     *
     * @return 是否在无线充电状态
     */
/*    public boolean isHome() {
        boolean isHome = false;
        if (mRobotPlatform != null) {
            try {
                boolean isCharging = mRobotPlatform.getPowerStatus().isCharging();
                boolean isDcConnect = mRobotPlatform.getPowerStatus().isDCConnected();
                isHome = isCharging && !isDcConnect;
            }  catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            onRequestError();
        }
        return isHome;
    }*/
    public boolean isHome() {
        boolean dockingStatusValue = false;
        try {
            PowerStatus status = mRobotPlatform.getPowerStatus();
            DockingStatus dockingStatus = status.getDockingStatus();
            dockingStatusValue = dockingStatus == DockingStatus.OnDock;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dockingStatusValue;
    }

    /**
     * 保存地图
     *
     * @param mapName  要保存地图的名字
     * @param callBack 回调函数
     */
    public void saveMap(final String mapName, final MapCallBack callBack) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                try {
                    // 获取底盘的Map对象
                    Map map = getMap();
                    if (map != null) {
                        // 判断目录是否存在不存在的话创建
                        FileUtils.createOrExistsDir(Constants.MAP_PATH);
                        // 文件输出流对象
                        FileOutputStream fos = new FileOutputStream(Constants.MAP_PATH + "/" + mapName);
                        // 对象输出流(写入)
                        ObjectOutputStream oos = new ObjectOutputStream(fos);
                        // 写入Origin对象
                        oos.writeFloat(map.getOrigin().getX());
                        oos.writeFloat(map.getOrigin().getY());
                        // 写入Dimension对象
                        oos.writeInt(map.getDimension().getWidth());
                        oos.writeInt(map.getDimension().getHeight());
                        // 写入Resolution对象
                        oos.writeFloat(map.getResolution().getX());
                        oos.writeFloat(map.getResolution().getY());
                        // 写入时间戳对象
                        oos.writeLong(map.getTimestamp());
                        // 写入真实地图对象
                        oos.writeObject(map.getData());
                        // oos.write(map.getData());
                        // 关闭流并且刷新操作
                        oos.close();
                        // 生成缩略图
                        FileUtils.createOrExistsFile(Constants.MAP_PATH_THUMB + "/" + mapName + ".jpg");
                        // 写入BMP图像
                        int width = map.getDimension().getWidth();
                        int height = map.getDimension().getHeight();
                        Bitmap bitmap = createImage(map.getData(), width, height);
                        byte[] bytes = ImageUtils.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG);
                        FileIOUtils.writeFileFromBytesByStream(
                                FileUtils.getFileByPath(
                                        Constants.MAP_PATH_THUMB + "/" + mapName + ".jpg"), bytes);
                    } else {
                        e.onNext(false);
                    }
                    e.onNext(true);
                } catch (Throwable throwable) {
                    e.onNext(false);
                }
            }
        }).subscribe(new BaseIoSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    callBack.onSuccess();
                } else {
                    callBack.onFailed();
                }
            }

            @Override
            public void onFailed(Throwable e) {
                callBack.onFailed();
            }
        });
    }

    /**
     * 保存地图
     *
     * @param mapName  要保存的地图名字
     * @param isNewMap 是否为新地图
     * @param callBack 回调函数
     */
    public void saveMap(final String mapName, boolean isNewMap, final MapCallBack callBack) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) {
                try {
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
                } catch (Throwable throwable) {
                    e.onNext(false);
                }
            }
        }).subscribe(new BaseIoSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                if (aBoolean) {
                    callBack.onSuccess();
                } else {
                    callBack.onFailed();
                }
            }

            @Override
            public void onFailed(Throwable e) {
                callBack.onFailed();
            }
        });
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
//        Bitmap result = Bitmap.createBitmap(rawData, width, height, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
//        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(result);
//        Bitmap temp = ImageUtils.getBitmap(android.R.mipmap.sym_def_app_icon);
//        canvas.drawBitmap(temp, 0, 0, null);
//        canvas.save();
//        canvas.restore();
//        return result;
        return Bitmap.createBitmap(rawData, width, height, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
    }

    /**
     * 根据地图名字加载地图
     *
     * @param mapNamePath 地图文件路径
     * @param callBack    回调函数
     */
    public void loadMap(final String mapNamePath, final MapCallBack callBack) {
        loadMap(mapNamePath, true, callBack);
    }

    /**
     * 根据地图名字加载地图
     *
     * @param mapNamePath 地图文件路径
     * @param isNewMap    是否为新格式的地图
     * @param callBack    回调函数
     */
    public void loadMap(final String mapNamePath, final boolean isNewMap, final MapCallBack callBack) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) throws Exception {
                try {
                    // 新地图加载逻辑
                    if (isNewMap) {
                        // 地图加载帮助对象
                        CompositeMapHelper helper = new CompositeMapHelper();
                        CompositeMap map = helper.loadFile(mapNamePath);
                        // 地图不为空的话加载地图
                        if (mRobotPlatform != null && map != null) {
                            mRobotPlatform.setCompositeMap(map, new Pose());
                            e.onNext(true);
                        } else {
                            e.onNext(false);
                        }
                    }
                    // 旧地图加载逻辑
                    else {
                        // 获取地图文件
                        File file = new File(mapNamePath);
                        // 地图文件不存在直接返回
                        if (!FileUtils.isFileExists(file)) {
                            e.onNext(false);
                        }
                        // 地图文件存在才继续做事情
                        else {
                            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file.getAbsolutePath()));
                            // 生成Origin对象
                            float ox = ois.readFloat();
                            float oy = ois.readFloat();
                            PointF origin = new PointF(ox, oy);
                            // 生成Dimension对象
                            int width = ois.readInt();
                            int height = ois.readInt();
                            Size size = new Size(width, height);
                            // 生成Resolution对象
                            float rx = ois.readFloat();
                            float ry = ois.readFloat();
                            PointF resolution = new PointF(rx, ry);
                            // 赋值时间戳
                            long timeStamp = ois.readLong();
                            // 生成二进制数组
                            byte[] data = (byte[]) ois.readObject();
                            // 生成地图对象
                            Map map = new Map(origin, size, resolution, timeStamp, data);
                            // 设置地图
                            setMap(map);
                            // 关闭流
                            ois.close();
                            e.onNext(true);
                        }
                    }
                } catch (Throwable throwable) {
                    e.onNext(false);
                }
            }
        }).subscribe(new BaseIoSubscriber<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                callBack.onSuccess();
            }

            @Override
            public void onFailed(Throwable e) {
                callBack.onFailed();
            }
        });
    }


    /**
     * 获取地图
     *
     * @return 返回地图
     */
    private Map getMap() {
        if (mRobotPlatform != null) {
            // 地图类型为8位位图
            MapType mapType = MapType.BITMAP_8BIT;
            // 地图种类为扫描建图
            MapKind mapKind = MapKind.EXPLORE_MAP;
            try {
                return mRobotPlatform.getMap(mapType, mapKind, mRobotPlatform.getKnownArea(mapType));
            } catch (Throwable throwable) {
                onRequestError();
                return null;
            }

        } else {
            onRequestError();
            return null;
        }
    }

    /**
     * 更新地图
     *
     * @param map 需要设置的地图
     */
    private void setMap(Map map) {
        if (mRobotPlatform != null) {
            try {
                mRobotPlatform.setMap(map);
            } catch (Throwable throwable) {
                onRequestError();
            }
        } else {
            onRequestError();
        }
    }

    /**
     * 开启定位计时器
     *
     * @param callBack 回调
     */
    private void startLocationTimer(final float yaw, final MoveCallBack callBack) {
        // 首先停止之前的定时任务
        cancelLocationTimer();
        // 非空判断
        if (mLocationTimer == null) {
            mLocationTimer = new Timer();
        }
        if (mLocationTask == null) {
            mLocationTask = new TimerTask() {
                @Override
                public void run() {
                    checkStatus(yaw, callBack);
                }
            };
        }
        // 开始计时器
        mLocationTimer.schedule(mLocationTask, 0, 100);
    }

    /**
     * 取消定位计时器
     */
    private void cancelLocationTimer() {
        if (mLocationTimer != null) {
            mLocationTimer.cancel();
            mLocationTimer = null;
        }
        if (mLocationTask != null) {
            mLocationTask.cancel();
            mLocationTask = null;
        }
    }

    /**
     * 检查机器人的状态
     *
     * @param callBack 回调
     */
    private void checkStatus(final float yaw, final MoveCallBack callBack) {
        if (mLocationAction != null) {
            try {
                ActionStatus currentStatus = mLocationAction.getStatus();
                Log.i("123456789", "checkStatus:" + currentStatus + ":" + yaw);
                if (currentStatus.equals(ActionStatus.FINISHED) || currentStatus.equals(ActionStatus.STOPPED)
                        || currentStatus.equals(ActionStatus.ERROR)) {
                    if (yaw == -99) {
                        // 回调状态值
                        callBack.onStateChange(currentStatus);
                    } else {
                        if (currentStatus.equals(ActionStatus.FINISHED)) {
                            rotateto(yaw, callBack);
                        } else {
                            callBack.onStateChange(currentStatus);
                        }
                    }
                    // 停止定位计时器
                    cancelLocationTimer();
                }
            } catch (Exception e) {
                e.printStackTrace();
/*                if(yaw == -99){
                    // 回调状态值
                    callBack.onStateChange(ActionStatus.ERROR);
                }else{
                    rotateto(yaw,callBack);
                }*/
                callBack.onStateChange(ActionStatus.ERROR);
                // 停止定位计时器
                cancelLocationTimer();
            }
        } else {
            // 发生意外停止监听状态计时器
            cancelLocationTimer();
        }
    }

    /**
     * 根据偏移量转动角度
     *
     * @param yaw 偏移量
     */
    public void rotateto(float yaw, final MoveCallBack callBack) {
        final Rotation rotation = new Rotation(yaw);
        Log.i("123456789", "rotateto:" + yaw);
        if (mRobotPlatform != null) {
            Observable.create(new ObservableOnSubscribe<Boolean>() {
                @Override
                public void subscribe(@NonNull ObservableEmitter<Boolean> e) throws Exception {
                    try {
                        moveAction = mRobotPlatform.rotateTo(rotation);
                        Log.i("123456789", "rotateto:end");
                        try {
                            Thread.sleep(4000);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                        e.onNext(true);
                    } catch (Throwable throwable) {
                        e.onNext(false);
                    }
                }
            }).subscribeOn(Schedulers.from(mThreadPool)).observeOn(Schedulers.from(mThreadPool)).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(@NonNull Boolean aBoolean) throws Exception {
                    Log.i("123456789", "rotateto:accept:" + aBoolean);
                    if (!aBoolean) {
                        onRequestError();
                    } else {
                        while (true) {
                            if (moveAction != null) {
                                ActionStatus status = moveAction.getStatus();
                                Log.i("987654321", "rotateto:moveAction :" + status);
                                if (status.equals(ActionStatus.FINISHED) || status.equals(ActionStatus.STOPPED)
                                        || status.equals(ActionStatus.ERROR)) {
                                    callBack.onStateChange(status);
                                    break;
                                }
                            } else {
                                Log.i("123456789", "rotateto:moveAction is null");
                                break;
                            }
                        }
                    }
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(@NonNull Throwable throwable) throws Exception {
                    onRequestError();
                    Log.i("123456789", "Consumer:error");
                }
            });
        } else {
            onRequestError();
            Log.i("123456789", "rotateto:error");
        }
    }

    /**
     * 开启移动定时器
     *
     * @param direction 方向
     * @param period    周期
     */
    private void startMoveTimer(final MoveDirection direction, Long period) {
        // 首先停止之前的移动定时器
        cancelMoveTimer();
        // 非空判断
        if (mMoveTimer == null) {
            mMoveTimer = new Timer();
        }
        if (mMoveTask == null) {
            mMoveTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        mRobotPlatform.moveBy(direction);
                    } catch (RequestFailException e) {
                        e.printStackTrace();
                    } catch (ConnectionFailException e) {
                        e.printStackTrace();
                    } catch (ConnectionTimeOutException e) {
                        e.printStackTrace();
                    } catch (UnauthorizedRequestException e) {
                        e.printStackTrace();
                    } catch (UnsupportedCommandException e) {
                        e.printStackTrace();
                    } catch (ParseInvalidException e) {
                        e.printStackTrace();
                    } catch (InvalidArgumentException e) {
                        e.printStackTrace();
                    } catch (OperationFailException e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        // 开启计时器移动
        mMoveTimer.schedule(mMoveTask, 0, period);
    }

    /**
     * 取消移动计时器
     */
    private void cancelMoveTimer() {
        if (mMoveTimer != null) {
            mMoveTimer.cancel();
            mMoveTimer = null;
        }
        if (mMoveTask != null) {
            mMoveTask.cancel();
            mMoveTask = null;
        }
    }

    /**
     * 开启重连计时器
     */
    private void startReconnectTimer() {
        // 取消重连计时器
        cancelReconnectTimer();
        // 非空判断
        if (mReconnectTimer == null) {
            mReconnectTimer = new Timer();
        }
        if (mReconnectTask == null) {
            mReconnectTask = new TimerTask() {
                @Override
                public void run() {
                    connectRobot(mIp, mPort, mCallBack);
                }
            };
        }
        // 开启计时器
        mReconnectTimer.schedule(mReconnectTask, 0, 3000);
    }

    /**
     * 取消重连计时器
     */
    private void cancelReconnectTimer() {
        if (mReconnectTimer != null) {
            mReconnectTimer.cancel();
            mReconnectTimer = null;
        }
        if (mReconnectTask != null) {
            mReconnectTask.cancel();
            mReconnectTask = null;
        }
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

}
