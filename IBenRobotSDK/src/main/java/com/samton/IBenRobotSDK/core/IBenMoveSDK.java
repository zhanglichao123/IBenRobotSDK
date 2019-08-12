package com.samton.IBenRobotSDK.core;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;

import com.samton.AppConfig;
import com.samton.IBenRobotSDK.utils.FileIOUtils;
import com.samton.IBenRobotSDK.utils.FileUtils;
import com.samton.IBenRobotSDK.utils.ImageUtils;
import com.samton.IBenRobotSDK.utils.LogUtils;
import com.slamtec.slamware.SlamwareCorePlatform;
import com.slamtec.slamware.action.ActionStatus;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.exceptions.ConnectionFailException;
import com.slamtec.slamware.exceptions.ConnectionTimeOutException;
import com.slamtec.slamware.exceptions.InvalidArgumentException;
import com.slamtec.slamware.exceptions.ParseInvalidException;
import com.slamtec.slamware.exceptions.RequestFailException;
import com.slamtec.slamware.exceptions.UnauthorizedRequestException;
import com.slamtec.slamware.exceptions.UnsupportedCommandException;
import com.slamtec.slamware.robot.CompositeMap;
import com.slamtec.slamware.robot.DockingStatus;
import com.slamtec.slamware.robot.GridMap;
import com.slamtec.slamware.robot.HealthInfo;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
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

public class IBenMoveSDK {
    // IBenMoveSDK单例
    private static IBenMoveSDK mInstance = null;
    // 底盘连接失败信息
    private static final String ERR_MSG = "底盘未连接";
    // 思岚SDK平台对象
    private SlamwareCorePlatform mRobotPlatform;
    // 是否连接成功
    private volatile boolean isConnect = false;
    // 是否低电量模式
    private boolean isWarnPower = false;
    // 点位的Disposable
    private Disposable mLocationSubscribe;
    // 连接底盘的Disposable
    private Disposable mConnectSubscribe;
    // 重连底盘的Disposable
    private Disposable mReconnectSubscribe;
    // 移动的Disposable
    private Disposable mMoveSubscribe;
    // 旋转的Disposable
    private Disposable mRotateSubscribe;
    // 急停按钮状态轮询
    private Disposable mStopStateSubscribe;
    // 延迟开启继续运动
    private Disposable mStartReGoSubscribe;
    // 当前去往的点位信息
    private Location mCurrentLocation;
    // 当前的点位角度信息
    private float mCurrentYaw;
    // 当前动作的回调
    private MoveCallBack mCurrentCallBack;
    // 当前急停回调
    private StopBtnState mCurrentStopState;
    // 连接IP地址
    private String mIp;
    // 连接端口号
    private int mPort;
    // 当前连接状态的回调
    private ConnectCallBack mConnectCallBack;

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
        isConnect = false;
        // 判断ip和端口是否合法
        if (TextUtils.isEmpty(ip) || port <= 0 || port > 65535) {
            if (callBack != null) {
                callBack.onConnectFailed();
            }
        } else { // 进行全局状态赋值
            mIp = ip;
            mPort = port;
            mConnectCallBack = callBack;
            mConnectSubscribe = Observable.create((ObservableOnSubscribe<Boolean>) e -> {
                LogUtils.d("思岚底盘--当前正在进行连接底盘操作");
                // 设置机器人连接监听
                mRobotPlatform = SlamwareCorePlatform.connect(ip, port);
                e.onNext(true);
                e.onComplete();
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aBoolean -> connectSuccess(),
                            throwable -> connectFail());
        }
    }

    /**
     * 机器人底盘连接成功
     */
    private void connectSuccess() {
        LogUtils.d("思岚底盘--连接底盘成功");
        // 将连接状态置为已连接
        isConnect = true;
        // 回调机器人连接成功
        if (mConnectCallBack != null) {
            mConnectCallBack.onConnectSuccess();
        }
        // 取消重连计时器
        cancelReconnectTimer();
    }

    /**
     * 机器人底盘连接失败
     */
    private void connectFail() {
        // 将连接状态置为未连接
        isConnect = false;
        // 如果正在重连或者连接则返回
        if ((mConnectSubscribe != null && !mConnectSubscribe.isDisposed())
                || (mConnectSubscribe != null && !mConnectSubscribe.isDisposed()))
            return;
        LogUtils.d("思岚底盘--连接底盘失败");
        if (mConnectCallBack != null) {
            mConnectCallBack.onConnectFailed();
        }
        // 这里进行三秒重连
        startReconnectTimer();
    }

    /**
     * 获取底盘连接状态
     *
     * @return 底盘连接状态
     */
    public boolean isConnect() {
        return isConnect;
    }

    /**
     * 开启重连计时器
     */
    private void startReconnectTimer() {
        if (mReconnectSubscribe != null && !mReconnectSubscribe.isDisposed()) return;
        // 开始三秒后进行重连
        mReconnectSubscribe = Observable.timer(3, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    LogUtils.d("思岚底盘--当前正在进行重新连接底盘操作");
                    connectRobot(mIp, mPort, mConnectCallBack);
                }, Throwable::printStackTrace);
    }

    /**
     * 取消重连计时器
     */
    private void cancelReconnectTimer() {
        if (mReconnectSubscribe != null && !mReconnectSubscribe.isDisposed()) {
            mReconnectSubscribe.dispose();
            mReconnectSubscribe = null;
        }
    }

    /**
     * 断开机器人连接
     */
    @SuppressLint("CheckResult")
    public void disConnectRobot() {
        LogUtils.d("思岚底盘--当前正在进行底盘断开连接操作");
        mIp = "";
        mPort = 0;
        mConnectCallBack = null;
        // 取消重连计时器
        cancelReconnectTimer();
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            mRobotPlatform.disconnect();
            e.onNext(true);
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("思岚底盘--与机器人断开连接成功"),
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
            LogUtils.d("思岚底盘--设置是否开启地图更新");
            if (isConnect) {
                mRobotPlatform.setMapUpdate(isUpdate);
                e.onNext(true);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("思岚底盘--设置地图更新状态成功"),
                        throwable -> {
                            throwable.printStackTrace();
                            LogUtils.d("思岚底盘--设置地图更新状态失败:" + throwable.getMessage());
                            connectFail();
                        });
    }

    /**
     * 清除地图
     */
    @SuppressLint("CheckResult")
    public void removeMap(ResultCallBack<Boolean> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            LogUtils.d("思岚底盘--清除地图");
            if (isConnect) {
                mRobotPlatform.clearMap();
                e.onNext(true);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    LogUtils.d("思岚底盘--清除地图状态成功");
                    callBack.onResult(true);
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--清除地图状态失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onResult(false);
                });
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
            LogUtils.d("思岚底盘--获取电池信息");
            if (isConnect) {
                JSONObject jsonObject = new JSONObject();
                int percent = mRobotPlatform.getBatteryPercentage();
                boolean isCharging = mRobotPlatform.getBatteryIsCharging();
                jsonObject.put("batteryPercent", percent);
                jsonObject.put("isCharging", isCharging);
                e.onNext(jsonObject.toString());
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LogUtils.d("思岚底盘--获取电池信息成功:" + result);
                    callBack.onSuccess(result);
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--获取电池信息失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onFailed();
                });
    }

    /**
     * 设置是否低电量模式
     *
     * @param warnPower 是否是低电量
     */
    public void setWarnPower(boolean warnPower) {
        this.isWarnPower = warnPower;
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
    public void getLocation(ResultCallBack<Location> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Location>) e -> {
            LogUtils.d("思岚底盘--获取当前点坐标");
            if (isConnect) {
                Location location = mRobotPlatform.getLocation();
                e.onNext(location);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LogUtils.d("思岚底盘--获取当前点位坐标成功:" + result.toString());
                    callBack.onResult(result);
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--获取当前点位坐标失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onResult(null);
                });
    }

    /**
     * 获取机器人姿态
     *
     * @return 当前点姿态
     */
    @SuppressLint("CheckResult")
    public void getPose(ResultCallBack<Pose> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Pose>) e -> {
            LogUtils.d("思岚底盘--获取机器人姿态");
            if (isConnect) {
                Pose pose = mRobotPlatform.getPose();
                e.onNext(pose);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LogUtils.d("思岚底盘--获取当前姿态成功:" + result.toString());
                    callBack.onResult(result);
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--获取当前姿态失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onResult(null);
                });
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
                    LogUtils.d("思岚底盘--设置机器人的姿态");
                    if (isConnect) {
                        mRobotPlatform.setPose(pose);
                        e.onNext(true);
                        e.onComplete();
                    } else {
                        e.onError(new Throwable(ERR_MSG));
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(aBoolean -> LogUtils.d("思岚底盘--设置当前姿态成功"),
                                throwable -> {
                                    throwable.printStackTrace();
                                    LogUtils.d("思岚底盘--设置当前姿态失败");
                                    connectFail();
                                });
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
            LogUtils.d("思岚底盘--开启移动定时器");
            mMoveSubscribe = Observable.interval(0, period, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .flatMap((Function<Long, ObservableSource<IMoveAction>>) aLong ->
                            Observable.create((ObservableOnSubscribe<IMoveAction>) e -> {
                                if (isConnect) {
                                    LogUtils.d("思岚底盘--当前正在移动");
                                    IMoveAction action = mRobotPlatform.moveBy(direction);
                                    e.onNext(action);
                                } else {
                                    e.onError(new Throwable(ERR_MSG));
                                }
                            }).subscribeOn(Schedulers.io()))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(action -> LogUtils.d("思岚底盘--当前移动成功"),
                            throwable -> {
                                throwable.printStackTrace();
                                LogUtils.d("思岚底盘--当前移动失败:" + throwable.getMessage());
                                connectFail();
                            });
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
            LogUtils.d("思岚底盘--当前旋转是否在充电桩:" + isHome);
            if (isHome) return;
            //取消所有动作
            cancelAllActions();
            //判断机器人当前急停状态
            hasSystemEmergencyStop(isStop -> {
                if (isStop) {
                    btnState.isOnEmergencyStop(true);
                } else {
                    Observable.create((ObservableOnSubscribe<IMoveAction>) e -> {
                        LogUtils.d("思岚底盘--开启旋转机器人");
                        if (isConnect) {
                            float tempAngle = (float) (angle / 180 * Math.PI);
                            Rotation rotation = new Rotation(tempAngle);
                            IMoveAction action = mRobotPlatform.rotate(rotation);
                            e.onNext(action);
                            e.onComplete();
                        } else {
                            e.onError(new Throwable(ERR_MSG));
                        }
                    })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(action -> LogUtils.d("思岚底盘--旋转角度成功"),
                                    throwable -> {
                                        throwable.printStackTrace();
                                        LogUtils.d("思岚底盘--旋转角度失败:" + throwable.getMessage());
                                        connectFail();
                                    });
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
        //记录当前动作的所有信息
        this.mCurrentLocation = null;
        this.mCurrentYaw = -999;
        this.mCurrentCallBack = callBack;
        this.mCurrentStopState = btnState;
        //取消所有动作
        cancelAllActions();
        //判断当前机器人的急停状态
        hasSystemEmergencyStop(isStop -> {
            if (isStop) {
                // 开启急停轮询
                startStopStateTimer();
                btnState.isOnEmergencyStop(true);
            } else {
                Observable.create((ObservableOnSubscribe<IMoveAction>) e -> {
                    LogUtils.d("思岚底盘--开启回充电桩操作");
                    if (isConnect) {
                        DockingStatus dockingStatus = mRobotPlatform.getPowerStatus().getDockingStatus();
                        boolean isHome = dockingStatus == DockingStatus.OnDock;
                        LogUtils.d("思岚底盘--当前执行回充电桩操作是否在充电桩:" + isHome);
                        if (isHome) {
                            callBack.onFinish(true);
                            e.onComplete();
                        } else {
                            IMoveAction action = mRobotPlatform.goHome();
                            e.onNext(action);
                            e.onComplete();
                        }
                    } else {
                        e.onError(new Throwable(ERR_MSG));
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(action -> startLocationTimer(action, -999, callBack, btnState)
                                , throwable -> {
                                    throwable.printStackTrace();
                                    LogUtils.d("思岚底盘--当前回桩操作失败:" + throwable.getMessage());
                                    connectFail();
                                    callBack.onFinish(false);
                                });
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
        if (isWarnPower || location == null || callBack == null || btnState == null) return;
        //记录当前动作的所有信息
        this.mCurrentLocation = location;
        this.mCurrentYaw = yaw;
        this.mCurrentCallBack = callBack;
        this.mCurrentStopState = btnState;
        //取消所有动作
        cancelAllActions();
        //如果开启急停返回
        hasSystemEmergencyStop(isStop -> {
            if (isStop) {
                // 开启急停轮询
                startStopStateTimer();
                btnState.isOnEmergencyStop(true);
            } else {
                // 然后执行行走至定点操作
                Observable.create((ObservableOnSubscribe<IMoveAction>) e -> {
                    LogUtils.d("思岚底盘--开启行走到指定点操作");
                    if (isConnect) {
                        // 判断机器人当前是否正在充电桩
                        DockingStatus dockingStatus = mRobotPlatform.getPowerStatus().getDockingStatus();
                        boolean isHome = dockingStatus == DockingStatus.OnDock;
                        LogUtils.d("思岚底盘--当前去定点状态是否在充电桩:" + isHome);
                        if (isHome) {
                            moveByDirection(MoveDirection.FORWARD, 300, btnState);
                            SystemClock.sleep(1000);
                            cancelMoveTimer();
                        }
                        MoveOption option = new MoveOption();
                        option.setSpeedRatio(0.4);
                        option.setWithYaw(false);
                        option.setPrecise(true);
                        option.setMilestone(true);
                        // 执行行走指令
                        IMoveAction action = mRobotPlatform.moveTo(location, option, yaw);
                        e.onNext(action);
                        e.onComplete();
                    } else {
                        e.onError(new Throwable(ERR_MSG));
                    }
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(action -> startLocationTimer(action, yaw, callBack, btnState)
                                , throwable -> {
                                    throwable.printStackTrace();
                                    LogUtils.d("思岚底盘--当前走定点操作失败:" + throwable.getMessage());
                                    connectFail();
                                    callBack.onFinish(false);
                                });
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
        // 取消急停按钮轮询
        cancelStopStateTimer();
        // 取消重新前往
        cancelReGoPoint();
        // 调用底盘取消当前动作
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            LogUtils.d("思岚底盘--停止所有动作");
            if (isConnect) {
                mRobotPlatform.getCurrentAction().cancel();
                e.onNext(true);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("思岚底盘--停止所有动作成功"),
                        throwable -> {
                            throwable.printStackTrace();
                            LogUtils.d("思岚底盘--停止所有动作失败:" + throwable.getMessage());
                            connectFail();
                        });
    }

    /**
     * 清除所有虚拟墙
     */
    @SuppressLint("CheckResult")
    public void clearAllWalls() {
        if (mRobotPlatform == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            LogUtils.d("思岚底盘--清除所有虚拟墙");
            if (isConnect) {
                mRobotPlatform.clearWalls();
                e.onNext(true);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> LogUtils.d("思岚底盘--清除虚拟墙成功"),
                        throwable -> {
                            throwable.printStackTrace();
                            LogUtils.d("思岚底盘--清除虚拟墙失败:" + throwable.getMessage());
                            connectFail();
                        });
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
            LogUtils.d("思岚底盘--判断机器人是否是无线充电状态");
            if (isConnect) {
                DockingStatus dockingStatus = mRobotPlatform.getPowerStatus().getDockingStatus();
                boolean isHome = dockingStatus == DockingStatus.OnDock;
                e.onNext(isHome);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LogUtils.d("思岚底盘--获取当前是否在充电桩成功:" + result);
                    callBack.onResult(result);
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--获取当前是否在充电桩失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onResult(true);
                });
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
            LogUtils.d("思岚底盘--查询机器人电池状态");
            if (isConnect) {
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
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribe(callBack::onResult,
                throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--查询电池状态失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onResult(0);
                });
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
            LogUtils.d("思岚底盘--保存地图");
            if (isConnect) {
                // 生成保存路径
                String path = AppConfig.MAP_PATH + "/" + mapName;
                // 获取完全版地图并保存到指定路径中
                CompositeMap compositeMap = mRobotPlatform.getCompositeMap();
                CompositeMapHelper mapHelper = new CompositeMapHelper();
                mapHelper.saveFile(path, compositeMap);
                // 生成缩略图
                FileUtils.createOrExistsFile(AppConfig.MAP_PATH_THUMB + "/" + mapName + ".jpg");
                // 写入BMP图像
                for (MapLayer layer : compositeMap.getMaps()) {
                    if (layer instanceof GridMap) {
                        GridMap map = ((GridMap) layer);
                        int width = map.getDimension().getWidth();
                        int height = map.getDimension().getHeight();
                        Bitmap bitmap = createImage(map.getMapData(), width, height);
                        byte[] bytes = ImageUtils.bitmap2Bytes(bitmap, Bitmap.CompressFormat.JPEG);
                        FileIOUtils.writeFileFromBytesByStream(FileUtils.getFileByPath(
                                AppConfig.MAP_PATH_THUMB + "/" + mapName + ".jpg"), bytes);
                    }
                }
                e.onNext(true);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    LogUtils.d("思岚底盘--保存地图成功");
                    callBack.onSuccess();
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--保存地图失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onFailed();
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
        return Bitmap.createBitmap(rawData, width, height, Bitmap.Config.ARGB_8888).copy(Bitmap.Config.ARGB_8888, true);
    }

    /**
     * 根据地图名字加载地图
     *
     * @param mapNamePath 保存的地图文件路径
     * @param cachePose   创建地图时的Pose
     * @param callBack    回调函数
     */
    @SuppressLint("CheckResult")
    public void loadMap(String mapNamePath, Pose cachePose, MapCallBack callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            LogUtils.d("思岚底盘--加载地图");
            if (isConnect) {
                // 地图加载帮助对象
                CompositeMapHelper helper = new CompositeMapHelper();
                CompositeMap map = helper.loadFile(mapNamePath);
                // 地图不为空的话加载地图
                mRobotPlatform.setCompositeMap(map, cachePose == null ? new Pose() : cachePose);
                e.onNext(true);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aBoolean -> {
                    LogUtils.d("思岚底盘--地图加载成功");
                    callBack.onSuccess();
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--地图加载失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onFailed();
                });
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
            LogUtils.d("思岚底盘--获取地图");
            if (isConnect) {
                // 地图类型为8位位图
                MapType mapType = MapType.BITMAP_8BIT;
                // 地图种类为扫描建图
                MapKind mapKind = MapKind.EXPLORE_MAP;
                // 获取地图
                Map map = mRobotPlatform.getMap(mapType, mapKind, mRobotPlatform.getKnownArea(mapType));
                e.onNext(map);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onResult,
                        throwable -> {
                            throwable.printStackTrace();
                            LogUtils.d("思岚底盘--获取地图失败:" + throwable.getMessage());
                            connectFail();
                            callBack.onResult(null);
                        });
    }

    /**
     * 开启定位计时器
     *
     * @param callBack 回调
     */
    private void startLocationTimer(IMoveAction action, float yaw, MoveCallBack callBack, StopBtnState btnState) {
        // 首先停止之前的定时任务
        cancelLocationTimer();
        // 停止旋转指令
        cancelRotate();
        mLocationSubscribe = Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            boolean isNeedCallBackStart = true;
            while (true) {
                if (!isConnect) {
                    e.onError(new Throwable(ERR_MSG));
                    break;
                }
                //判断当前的急停状态
                boolean isEmergencyStop = mRobotPlatform.getRobotHealth().getHasSystemEmergencyStop();
                if (isEmergencyStop) {
                    // 停止定位计时器
                    cancelLocationTimer();
                    // 开启急停轮询
                    startStopStateTimer();
                    btnState.isOnEmergencyStop(true);
                    break;
                } else {
                    ActionStatus status = action.getStatus();
                    if (status == ActionStatus.RUNNING) {
                        // 回调开始行走
                        if (isNeedCallBackStart) {
                            callBack.onStart();
                            isNeedCallBackStart = false;
                        }
                    } else if (status == ActionStatus.ERROR) {
                        // 判断返回的错误码是否是急停
                        if (isErrorToEmergencyStop()) {
                            startReGoPoint();
                        } else {
                            LogUtils.d("思岚底盘--查询移动中状态为失败:" + action.getReason());
                            e.onNext(false);
                            e.onComplete();
                        }
                        break;
                    } else if (status == ActionStatus.FINISHED) {
                        if (yaw == -999) {
                            // 回调状态值
                            e.onNext(true);
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
                .subscribe(callBack::onFinish
                        , throwable -> {
                            throwable.printStackTrace();
                            LogUtils.d("思岚底盘--去定点状态查询失败:" + throwable.getMessage());
                            connectFail();
                            callBack.onFinish(false);
                        });
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
        // 停止旋转指令
        cancelRotate();
        mRotateSubscribe = Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            IMoveAction action = mRobotPlatform.rotateTo(new Rotation(yaw));
            while (true) {
                if (!isConnect) {
                    e.onError(new Throwable(ERR_MSG));
                    break;
                }
                //判断当前的急停状态
                boolean isEmergencyStop = mRobotPlatform.getRobotHealth().getHasSystemEmergencyStop();
                if (isEmergencyStop) {
                    // 停止旋转计时器
                    cancelRotate();
                    // 开启急停轮询
                    startStopStateTimer();
                    btnState.isOnEmergencyStop(true);
                    break;
                } else {
                    if (action == null) break;
                    ActionStatus status = action.getStatus();
                    if (status == ActionStatus.ERROR) {
                        if (isErrorToEmergencyStop()) {
                            startReGoPoint();
                        } else {
                            e.onNext(false);
                            e.onComplete();
                        }
                        break;
                    } else if (status == ActionStatus.FINISHED) {
                        e.onNext(true);
                        e.onComplete();
                        break;
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onFinish
                        , throwable -> {
                            throwable.printStackTrace();
                            LogUtils.d("思岚底盘--旋转角度状态查询失败:" + throwable.getMessage());
                            connectFail();
                            callBack.onFinish(false);
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
            LogUtils.d("思岚底盘--开始获取急停状态");
            if (isConnect) {
                boolean isEmergencyStop = mRobotPlatform.getRobotHealth().getHasSystemEmergencyStop();
                e.onNext(isEmergencyStop);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LogUtils.d("思岚底盘--获取急停状态成功:" + result);
                    callBack.onResult(result);
                }, throwable -> {
                    throwable.printStackTrace();
                    LogUtils.d("思岚底盘--获取急停状态失败:" + throwable.getMessage());
                    connectFail();
                    callBack.onResult(true);
                });
    }

    /**
     * 判断底盘是否正在运动
     */
    @SuppressLint("CheckResult")
    public void isMoveing(ResultCallBack<Boolean> callBack) {
        if (callBack == null) return;
        Observable.create((ObservableOnSubscribe<Boolean>) e -> {
            LogUtils.d("思岚底盘--开始判断底盘是否正在运动");
            if (isConnect) {
                ActionStatus status = mRobotPlatform.getCurrentAction().getStatus();
                boolean isMove = status == ActionStatus.WAITING_FOR_START || status == ActionStatus.RUNNING;
                e.onNext(isMove);
                e.onComplete();
            } else {
                e.onError(new Throwable(ERR_MSG));
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(callBack::onResult,
                        throwable -> {
                            throwable.printStackTrace();
                            LogUtils.d("思岚底盘--获取移动状态失败:" + throwable.getMessage());
                            connectFail();
                            callBack.onResult(false);
                        });
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
        // 开始行走定点
        default void onStart() {
        }

        // 机器人是否成功到达定点
        void onFinish(boolean isSuccess);
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

    /**
     * 由于底盘MoveTo时的急停状态存在延迟,所以需要特别处理,判断返回的错误码是否是急停,是急停的话重新开始流程
     */
    private boolean isErrorToEmergencyStop() throws RequestFailException, ConnectionTimeOutException, InvalidArgumentException, ParseInvalidException, ConnectionFailException, UnauthorizedRequestException, UnsupportedCommandException {
        ArrayList<HealthInfo.BaseError> errors = mRobotPlatform.getRobotHealth().getErrors();
        if (errors != null && !errors.isEmpty()) {
            for (HealthInfo.BaseError error : errors) {
                LogUtils.d("IBenMoveSDK:EoorrCode-" + error.getComponentErrorType());
                if (error.getComponentErrorType() == 2055) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 关闭急停按钮状态轮询
     */
    private void cancelStopStateTimer() {
        if (mStopStateSubscribe != null && !mStopStateSubscribe.isDisposed()) {
            mStopStateSubscribe.dispose();
            mStopStateSubscribe = null;
        }
    }

    /**
     * 轮询是否关闭急停
     */
    private void startStopStateTimer() {
        cancelStopStateTimer();
        cancelReGoPoint();
        mStopStateSubscribe = Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    //判断当前的急停状态
                    boolean isEmergencyStop = mRobotPlatform.getRobotHealth().getHasSystemEmergencyStop();
                    if (isEmergencyStop) {
                        startStopStateTimer();
                    } else {//急停按钮已关闭
                        cancelStopStateTimer();
                        startReGoPoint();
                    }
                }, Throwable::printStackTrace);
    }

    /**
     * 停止前往定点或充电桩
     */
    private void cancelReGoPoint() {
        if (mStartReGoSubscribe != null && !mStartReGoSubscribe.isDisposed()) {
            mStartReGoSubscribe.dispose();
            mStartReGoSubscribe = null;
        }
    }

    /**
     * 重新前往定点或充电桩
     */
    private void startReGoPoint() {
        cancelReGoPoint();
        mStartReGoSubscribe = Observable.timer(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (mCurrentLocation == null && mCurrentYaw == -999) {
                        goHome(mCurrentCallBack, mCurrentStopState);
                    } else {
                        goLocation(mCurrentLocation, mCurrentYaw, mCurrentCallBack, mCurrentStopState);
                    }
                    cancelReGoPoint();
                }, Throwable::printStackTrace);
    }
}
