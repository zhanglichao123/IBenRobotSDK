package com.samton.ibenrobotdemo.map;

import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.samton.ibenrobotdemo.events.BatteryEvent;
import com.samton.ibenrobotdemo.events.ConnectedEvent;
import com.samton.ibenrobotdemo.events.ConnectionLostEvent;
import com.samton.ibenrobotdemo.events.LaserScanUpdateEvent;
import com.samton.ibenrobotdemo.events.LocationEvent;
import com.samton.ibenrobotdemo.events.MapOperationEvent;
import com.samton.ibenrobotdemo.events.MapUpdateEvent;
import com.samton.ibenrobotdemo.events.MoveActionUpdateEvent;
import com.samton.ibenrobotdemo.events.RobotPoseUpdateEvent;
import com.samton.ibenrobotdemo.events.WallUpdateEvent;
import com.slamtec.slamware.SlamwareCorePlatform;
import com.slamtec.slamware.action.IMoveAction;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.action.Path;
import com.slamtec.slamware.geometry.Line;
import com.slamtec.slamware.robot.CompositeMap;
import com.slamtec.slamware.robot.HealthInfo;
import com.slamtec.slamware.robot.LaserScan;
import com.slamtec.slamware.robot.Location;
import com.slamtec.slamware.robot.Map;
import com.slamtec.slamware.robot.MapKind;
import com.slamtec.slamware.robot.MapType;
import com.slamtec.slamware.robot.Pose;
import com.slamtec.slamware.robot.Rotation;
import com.slamtec.slamware.sdp.CompositeMapHelper;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/14 10:10
 *   desc    : 控制机器人的工具
 *   version : 1.0
 * </pre>
 */
public final class RobotAgent {

    /**
     * 最大缩放比例
     */
    private final static float UPDATE_MAP_RADIX = 7.0f;
    /**
     * 定位错误
     */
    private final static int RE_LOCALIZATION =
            HealthInfo.BaseError.BaseComponentErrorTypeSystemSlamwareRelocalizationFailed;
    /**
     * 连接机器人任务
     */
    private static JobConnect mJobConnect;
    /**
     * 断开与机器人的连接任务
     */
    private static JobDisconnect mJobDisconnect;
    /**
     * 更新地图任务
     */
    private static JobUpdateMap mJobUpdateMap;
    private static JobUpdateWholeMap mJobUpdateWholeMap;
    /**
     * 更新机器人姿态任务
     */
    private static JobUpdatePose mJobUpdatePose;
    /**
     * 更新机器人雷达数据任务
     */
    private static JobUpdateLaserScan mJobUpdateLaserScan;
    /**
     * 更新机器人电量信息任务
     */
    private static JobGetPowerStatus mJobGetPowerStatus;
    /**
     * 行走至指定点任务
     */
    private static JobMoveToLocation mJobMoveToLocation;
    /**
     * 清除地图任务
     */
    private static JobClearMap mJobClearMap;
    /**
     * 回桩任务
     */
    private static JobGoHome mJobGoHome;
    /**
     * 加载地图任务
     */
    private static JobLoadMap mJobLoadMap;
    /**
     * 保存地图任务
     */
    private static JobSaveMap mJobSaveMap;
    /**
     * 停止所有任务
     */
    private static JobCancelAllActions mJobCancelAllActions;
    /**
     * 获取当前坐标点信息
     */
    private static JobGetLocation mJobGetLocation;
    /**
     * 更新虚拟墙信息任务
     */
    private static JobUpdateWalls mJobUpdateWalls;
    /**
     * 添加虚拟墙任务
     */
    private static JobAddWalls mJobAddWalls;
    /**
     * 根据ID移除虚拟墙任务
     */
    private static JobRemoveWallById mJobRemoveWallById;
    /**
     * 更新动作任务
     */
    private static JobUpdateMoveAction mJobUpdateMoveAction;
    /**
     * 工作线程
     */
    private Worker mWorker;
    /**
     * 思岚SDK平台对象
     */
    private SlamwareCorePlatform mRobotPlatform;
    /**
     * 机器人IP地址
     */
    private String mIp;
    /**
     * 机器人端口
     */
    private int mPort;
    /**
     * 机器人姿态
     */
    private Pose mRobotPose;
    /**
     * 地图区域
     */
    private RectF mMergedMapUpdateArea;
    /**
     * 地图类型
     */
    private MapType mMapType;
    /**
     * 需要行走到的指定点
     */
    private Location mLocation;
    /**
     * 雷达扫描信息
     */
    private LaserScan mLaserScan;
    /**
     * 机器人是否连接
     */
    private boolean isConnected;
    /**
     * 是否正在更新机器人姿态
     */
    private boolean isUpdatingPose;
    /**
     * 是否在更新机器人雷达数据
     */
    private boolean isUpdatingLaserScan;
    /**
     * 是否在更新机器人电量信息
     */
    private boolean isUpdatingPowerStatus;
    /**
     * 是否在更新虚拟墙信息
     */
    private boolean isUpdatingWalls;
    /**
     * 是否正在更新运动状态
     */
    private boolean isUpdatingMoveAction;
    /**
     * 移动计时器
     */
    private Timer mMoveTimer;
    private TimerTask mMoveTask;
    /**
     * 当前缓存的地图
     */
    private MapDataCache mMapData;
    /**
     * 虚拟墙数据
     */
    private Vector<Line> mWalls;
    /**
     * 需要添加的虚拟墙数据
     */
    private Vector<Line> mWallsToAdd;
    /**
     * 加载地图路径
     */
    private String mLoadPath;
    /**
     * 保存地图路径
     */
    private String mSavePath;
    /**
     * 需要移除的虚拟墙ID
     */
    private int mWallId;

    /**
     * 私有构造
     */
    private RobotAgent() {
        // 初始化需要添加的虚拟墙数组
        mWallsToAdd = new Vector<>();
        // 初始化缓存地图
        mMapData = new MapDataCache();
        // 初始化混合后的区域
        mMergedMapUpdateArea = new RectF(0, 0, 0, 0);
        // 初始化机器人姿态
        mRobotPose = new Pose(new Location(0, 0, 0), new Rotation(0, 0, 0));
        // 初始化端口
        mPort = 0;
        // 默认机器没有连接
        isConnected = false;
        // 默认没有更新机器人姿态
        isUpdatingPose = false;
        // 默认没有更新机器人雷达数据
        isUpdatingLaserScan = false;
        // 默认没有更新机器人电量信息
        isUpdatingPowerStatus = false;
        // 默认没有在更新虚拟墙信息
        isUpdatingWalls = false;
        // 默认没有在更新动作
        isUpdatingMoveAction = false;
        // 初始化工作线程
        mWorker = new Worker();
        // 初始化连接机器人任务
        mJobConnect = new JobConnect();
        // 初始化断开机器人连接任务
        mJobDisconnect = new JobDisconnect();
        // 初始化更新机器人地图任务
        mJobUpdateMap = new JobUpdateMap();
        mJobUpdateWholeMap = new JobUpdateWholeMap();
        // 初始化更新机器人姿态任务
        mJobUpdatePose = new JobUpdatePose();
        // 初始化更新机器人雷达数据任务
        mJobUpdateLaserScan = new JobUpdateLaserScan();
        // 初始化更新机器人电量信息任务
        mJobGetPowerStatus = new JobGetPowerStatus();
        // 初始化行走至指定点任务
        mJobMoveToLocation = new JobMoveToLocation();
        // 初始化清除地图任务
        mJobClearMap = new JobClearMap();
        // 初始化回桩任务
        mJobGoHome = new JobGoHome();
        // 停止所有工作任务
        mJobCancelAllActions = new JobCancelAllActions();
        // 初始化加载地图任务
        mJobLoadMap = new JobLoadMap();
        // 初始化保存地图任务
        mJobSaveMap = new JobSaveMap();
        // 初始化获取当前坐标点任务
        mJobGetLocation = new JobGetLocation();
        // 初始化添加虚拟墙任务
        mJobAddWalls = new JobAddWalls();
        // 初始化更新虚拟墙任务
        mJobUpdateWalls = new JobUpdateWalls();
        // 初始化根据ID移除虚拟墙任务
        mJobRemoveWallById = new JobRemoveWallById();
        // 初始化更新动作任务
        mJobUpdateMoveAction = new JobUpdateMoveAction();
    }

    /**
     * 获取控制机器人工具单例
     *
     * @return 机器人工具单例
     */
    public static RobotAgent getInstance() {
        return SingleHolder.instance;
    }

    /**
     * 获取当前地图
     *
     * @return 地图信息
     */
    public MapDataCache getMapData() {
        return mMapData;
    }

    /**
     * 获取机器人姿态信息
     *
     * @return 机器人姿态信息
     */
    public synchronized Pose getRobotPose() {
        return mRobotPose;
    }

    /**
     * 获取机器人雷达数据
     *
     * @return 机器人雷达数据
     */
    public synchronized LaserScan getLaserScan() {
        return mLaserScan;
    }

    /**
     * 获取虚拟墙信息
     *
     * @return 所有的虚拟墙
     */
    public synchronized Vector<Line> getWalls() {
        return mWalls;
    }

    /**
     * 从新连接机器人
     */
    public void reconnect() {
        // 声明ip和端口局部变量
        String ip;
        int port;
        // 同步锁
        synchronized (this) {
            ip = mIp;
            port = mPort;
        }
        // 如果ip和端口不合法的话直接返回
        if (ip.isEmpty() || port == 0) {
            return;
        }
        // 连接机器人
        connectRobot(ip, port);
    }

    /**
     * 连接机器人
     *
     * @param ip   机器人IP
     * @param port 机器人端口
     */
    public void connectRobot(String ip, int port) {
        mIp = ip;
        mPort = port;
        // 开启连接机器人任务
        pushJob(mJobConnect);
    }

    /**
     * 提交任务
     *
     * @param job 需要执行的任务
     */
    private void pushJob(Runnable job) {
        mWorker.push(job);
    }

    /**
     * 根据方向进行移动和间隔持续移动
     *
     * @param direction 方向
     * @param period    间隔
     */
    public void moveByDirection(@NonNull MoveDirection direction, long period) {
        // 如果机器人没有连接的话退出
        if (!isConnected) {
            return;
        }
        // 如果思岚SDK为空,返回
        if (mRobotPlatform != null) {
            // 如果当前有运动则放弃之前的运动
            cancelAllActions();
            // 开启运动计时器，定时移动
            startMoveTimer(direction, period);
        } else {
            // 如果思岚SDK为空回调连接超时
            onRequestError();
        }
    }

    /**
     * 停止所有动作
     */
    public void cancelAllActions() {
        // 发布停止所有动作任务
        pushJob(mJobCancelAllActions);
    }

    /**
     * 开启移动定时器
     *
     * @param direction 方向
     * @param period    周期
     */
    private void startMoveTimer(final MoveDirection direction, long period) {
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
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            };
        }
        // 开启计时器移动
        mMoveTimer.schedule(mMoveTask, 0, period);
    }

    /**
     * 连接超时
     */
    private void onRequestError() {
        // 同步锁,重置状态
        synchronized (this) {
            mWorker.clear();
            mMapData.clear();
            mRobotPlatform = null;
            isConnected = false;
        }
        // 回调连接超时
        EventBus.getDefault().post(new ConnectionLostEvent());
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
     * 机器人是否已经连接
     *
     * @return 是否连接
     */
    public boolean isConnected() {
        return isConnected;
    }

    /**
     * 清除地图
     */
    public void clearMap() {
        pushJob(mJobClearMap);
    }

    /**
     * 加载地图
     *
     * @param path 地图路径(绝对)
     */
    public void loadMap(String path) {
        mLoadPath = path;
        pushJob(mJobLoadMap);
    }

    /**
     * 保存地图
     *
     * @param path 地图路径(绝对)
     */
    public void saveMap(String path) {
        mSavePath = path;
        pushJob(mJobSaveMap);
    }

    /**
     * 回充电桩
     */
    public void goHome() {
        pushJob(mJobGoHome);
    }

    /**
     * 行走至指定点
     *
     * @param location 指定点
     */
    public void goToLocation(Location location) {
        mLocation = location;
        pushJob(mJobMoveToLocation);
    }

    /**
     * 更新地图
     */
    public void updateMap() {
        pushJob(mJobUpdateWholeMap);
    }

    /**
     * 更新机器人姿态
     */
    public void updatePose() {
        synchronized (this) {
            if (isUpdatingPose) {
                return;
            }
            isUpdatingPose = true;
        }
        pushJob(mJobUpdatePose);
    }

    /**
     * 更新机器人雷达数据
     */
    public void updateLaserScan() {
        synchronized (this) {
            if (isUpdatingLaserScan) {
                return;
            }
            isUpdatingLaserScan = true;
        }
        pushJob(mJobUpdateLaserScan);
    }

    /**
     * 获取电量信息
     */
    public void getPowerStatus() {
        synchronized (this) {
            if (isUpdatingPowerStatus) {
                return;
            }
            isUpdatingPowerStatus = true;
        }
        pushJob(mJobGetPowerStatus);
    }

    /**
     * 获取当前位置点信息
     */
    public void getLocation() {
        pushJob(mJobGetLocation);
    }

    /**
     * 根据机器人的可见区域更新地图
     *
     * @param area 可见区域
     */
    private void updateMap(RectF area) {
        // 可见区域为空直接返回
        if (area.isEmpty()) {
            return;
        }
        // 同步锁
        synchronized (this) {
            // 如果当前混合区域不为空的话混合,否则直接赋值
            if (mMergedMapUpdateArea != null && !mMergedMapUpdateArea.isEmpty()) {
                mMergedMapUpdateArea.union(area);
                return;
            } else {
                mMergedMapUpdateArea = area;
            }
        }
        // 开启更新地图任务
        pushJob(mJobUpdateMap);
    }

    /**
     * 断开与机器人的连接
     */
    public void disconnectRobot() {
        // 开启断开任务
        pushJob(mJobDisconnect);
    }

    /**
     * 添加虚拟墙
     *
     * @param line 需要添加的虚拟墙
     */
    public void addWall(Line line) {
        // 同步锁,保证添加的墙不重复
        synchronized (this) {
            mWallsToAdd.add(line);
            // 双重判断保证添加的是一个墙
            if (mWallsToAdd.size() != 1) {
                return;
            }
        }
        pushJob(mJobAddWalls);
    }

    /**
     * 根据虚拟墙ID移除虚拟墙
     *
     * @param wallId 虚拟墙id
     */
    public void removeWallById(int wallId) {
        synchronized (this) {
            mWallId = wallId;
        }
        pushJob(mJobRemoveWallById);
    }

    /**
     * 更新虚拟墙信息
     */
    public void updateWalls() {
        // 同步锁
        synchronized (RobotAgent.this) {
            if (isUpdatingWalls) {
                return;
            }
            isUpdatingWalls = true;
        }
        pushJob(mJobUpdateWalls);
    }

    /**
     * 更新动作
     */
    public void updateMoveAction() {
        synchronized (RobotAgent.this) {
            if (isUpdatingMoveAction) {
                return;
            }
            isUpdatingMoveAction = true;
        }
        pushJob(mJobUpdateMoveAction);
    }

    /**
     * 内部静态类
     */
    private static class SingleHolder {
        /**
         * 单例
         */
        private static RobotAgent instance = new RobotAgent();
    }

    /**
     * 连接机器人任务
     */
    private class JobConnect implements Runnable {
        @Override
        public void run() {
            try {
                // 声明局部变量保存IP和端口
                String ip;
                int port;
                // 同步锁,防止多次调用ip和端口发生改变
                synchronized (RobotAgent.this) {
                    ip = mIp;
                    port = mPort;
                }
                // 判断IP和端口的合法性
                if (ip == null || ip.isEmpty() || port <= 0 || port > 65535) {
                    onRequestError();
                    return;
                }
                // 连接机器人
                SlamwareCorePlatform robotPlatform = SlamwareCorePlatform.connect(ip, port);
                // 获取当前机器人姿态
                Pose robotPose = robotPlatform.getPose();
                // 获取当前机器人地图
                List<MapType> mapTypes = robotPlatform.getAvailableMaps();
                MapType mapType = mapTypes.get(0);
                RectF knownArea = robotPlatform.getKnownArea(mapType);
                // 获取当前机器人雷达扫描信息
                LaserScan currentLaserScan = robotPlatform.getLaserScan();
                // 同步锁,设置类变量
                synchronized (RobotAgent.this) {
                    // 机器人姿态
                    mRobotPose = robotPose;
                    // 机器人控制器
                    mRobotPlatform = robotPlatform;
                    // 地图类型
                    mMapType = mapType;
                    // 雷达扫描信息
                    mLaserScan = currentLaserScan;
                    // 机器人是否连接
                    isConnected = true;
                }
                // 创建当前扫描的可见区域
                RectF clipArea = new RectF(
                        robotPose.getLocation().getX() - UPDATE_MAP_RADIX * 2,
                        robotPose.getLocation().getY() - UPDATE_MAP_RADIX * 2,
                        robotPose.getLocation().getX() + UPDATE_MAP_RADIX * 2,
                        robotPose.getLocation().getY() + UPDATE_MAP_RADIX * 2
                );
                knownArea.intersect(clipArea);
                // 更新地图
                updateMap(knownArea);
            } catch (Throwable throwable) {
                onRequestError();
            }
            // 回调连接成功事件
            EventBus.getDefault().post(new ConnectedEvent());
        }
    }

    /**
     * 断开与机器人的连接任务
     */
    private class JobDisconnect implements Runnable {
        @Override
        public void run() {
            synchronized (RobotAgent.this) {
                // 如果还没初始化机器人的话直接放弃
                if (mRobotPlatform == null) {
                    return;
                }
                // 清空工作线程
                mWorker.clear();
                // 清空地图数据
                mMapData.clear();
                // 调用SDK方法断开连接机器人
                mRobotPlatform.disconnect();
                // 置空
                mRobotPlatform = null;
                // 重置连接状态
                isConnected = false;
            }
        }
    }

    /**
     * 更新地图任务
     */
    private class JobUpdateMap implements Runnable {
        @Override
        public void run() {
            // 声明局部变量
            SlamwareCorePlatform platform;
            RectF area = new RectF();
            MapType mapType;
            // 同步锁,保证变量唯一操作
            synchronized (RobotAgent.this) {
                // 中间变量
                RectF tmp = new RectF(mMergedMapUpdateArea);
                // 将现有的地图清空
                mMergedMapUpdateArea.set(area);
                // 将中间变量赋值给局部变量
                area.set(tmp);
                // 局部变量赋值
                platform = mRobotPlatform;
                mapType = mMapType;
            }
            // 思岚SDK平台对象为空的话返回
            if (platform == null) {
                return;
            }
            // 如果需要更新的地图为空,直接回调不用走之后的流程
            if (area.isEmpty()) {
                // 回调更新地图
                EventBus.getDefault().post(new MapUpdateEvent(area));
                return;
            }
            // 初始化局部地图变量
            Map map;
            try {
                // 获取当前地图
                map = platform.getMap(mapType, MapKind.EXPLORE_MAP, area);
            } catch (Throwable throwable) {
                onRequestError();
                return;
            }
            // 同步锁
            synchronized (RobotAgent.this) {
                // 更新当前保存的地图信息
                mMapData.update(map);
            }
            // 回调更新地图
            EventBus.getDefault().post(new MapUpdateEvent(area));
        }
    }

    /**
     * 更新可见地图任务
     */
    private class JobUpdateWholeMap implements Runnable {
        @Override
        public void run() {
            // 声明局部变量
            SlamwareCorePlatform platform;
            MapType mapType;
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
                mapType = mMapType;
            }
            // 思岚SDK为空的话返回
            if (platform == null) {
                return;
            }
            // 获取当前机器人可见区域,并更新地图
            try {
                RectF area = platform.getKnownArea(mapType);
                updateMap(area);
            } catch (Throwable throwable) {
                onRequestError();
            }
        }
    }

    /**
     * 更新机器人姿态任务
     */
    private class JobUpdatePose implements Runnable {
        @Override
        public void run() {
            // 声明SDK局部变量
            SlamwareCorePlatform platform;
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
                // 如果思岚SDK为空,重置标识位
                if (platform == null) {
                    isUpdatingPose = false;
                    return;
                }
            }
            // 声明机器人姿态局部变量
            Pose pose;
            try {
                // 获取当前机器人姿态
                pose = platform.getPose();
                // 同步锁
                synchronized (RobotAgent.this) {
                    // 获取到当前机器人姿态后赋值并重置标识位
                    mRobotPose = pose;
                    isUpdatingPose = false;
                }
            } catch (Exception e) {
                onRequestError();
                // 重置标识位
                isUpdatingPose = false;
                return;
            }
            // 回调更新机器人姿态
            EventBus.getDefault().post(new RobotPoseUpdateEvent(pose));
        }
    }

    /**
     * 更新机器人雷达扫描数据任务
     */
    private class JobUpdateLaserScan implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK局部变量
            SlamwareCorePlatform platform;
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
                // 如果思岚SDK为空,返回并重置标识位
                if (platform == null) {
                    isConnected = false;
                    return;
                }
            }
            // 声明雷达扫描局部变量
            LaserScan laserScan;
            Pose pose = null;
            try {
                // 获取当前雷达扫描数据
                laserScan = platform.getLaserScan();
                if (laserScan.getPose() != null) {
                    pose = laserScan.getPose();
                }
            } catch (Throwable throwable) {
                onRequestError();
                // 重置标识位
                isUpdatingLaserScan = false;
                return;
            }
            // 同步锁,更新当前雷达和姿态数据
            synchronized (RobotAgent.this) {
                mLaserScan = laserScan;
                if (laserScan.getPose() != null && pose != null) {
                    mRobotPose = pose;
                }
                // 重置标识位
                isUpdatingLaserScan = false;
            }
            // 回调更新雷达数据
            EventBus.getDefault().post(new LaserScanUpdateEvent(laserScan));
        }
    }

    /**
     * 更新机器人状态
     */
    private class JobGetPowerStatus implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK局部变量
            SlamwareCorePlatform platform;
            // 同步锁
            synchronized (RobotAgent.this) {
                // 赋值思岚SDK,如果为空的话返回
                platform = mRobotPlatform;
                if (platform == null) {
                    // 重置标识位
                    isUpdatingPowerStatus = false;
                    return;
                }
            }
            // 声明电量以及是否在充电局部变量
            int batteryPercentage;
            boolean isCharging;
            String batteryInfo;
            try {
                // 获取当前机器人电量和是否在充电状态
                batteryPercentage = platform.getBatteryPercentage();
                isCharging = platform.getBatteryIsCharging();
                // 组建Json
                JSONObject batteryJson = new JSONObject();
                batteryJson.put("batteryPercent", batteryPercentage);
                batteryJson.put("isCharging", isCharging);
                // 转换成字符串回调
                batteryInfo = batteryJson.toString();
            } catch (Throwable e) {
                onRequestError();
                // 重置标识位
                isUpdatingPowerStatus = false;
                return;
            }
            // 同步锁
            synchronized (RobotAgent.this) {
                isUpdatingPowerStatus = false;
            }
            // 回调机器人电量信息
            EventBus.getDefault().post(new BatteryEvent(batteryInfo));
        }
    }

    /**
     * 行走至指定点任务
     */
    private class JobMoveToLocation implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK局部变量,目标点变量
            SlamwareCorePlatform platform;
            Location location;
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
                location = mLocation;
                // 如果SDK为空或者目标点为空的话返回
                if (platform == null || location == null)
                    return;
            }
            try {
                // 行走至指定点
                platform.moveTo(location);
            } catch (Exception e) {
                onRequestError();
            }
        }
    }

    /**
     * 清除地图任务
     */
    private class JobClearMap implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK局部变量
            SlamwareCorePlatform platform;
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
            }
            // 如果思岚SDK为空的话直接返回
            if (platform == null)
                return;
            try {
                // 进行清除地图操作
                platform.clearMap();
            } catch (Exception e) {
                onRequestError();
                return;
            }
            // 同步锁
            synchronized (RobotAgent.this) {
                // 清空本地保存的地图信息
                mMapData.clear();
            }
            // 回调地图更新
            EventBus.getDefault().post(new MapUpdateEvent(new RectF(-60, -60, 60, 60)));
        }
    }

    /**
     * 加载地图任务
     */
    private class JobLoadMap implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK局部变量
            SlamwareCorePlatform platform;
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
            }
            // 如果思岚SDK为空的话直接返回
            if (platform == null) {
                return;
            }
            // 声明局部变量
            String path;
            // 同步锁
            synchronized (RobotAgent.this) {
                path = mLoadPath;
            }
            // 如果地图路径为空的话返回
            if (TextUtils.isEmpty(path)) {
                return;
            }
            // 置空地图路径
            mLoadPath = "";
            try {
                // 进行加载地图操作
                CompositeMapHelper mapHelper = new CompositeMapHelper();
                CompositeMap map = mapHelper.loadFile(path);
                if (map != null) {
                    // 加载地图
                    platform.setCompositeMap(map, new Pose());
                    // 获取当前机器人姿态
                    Pose robotPose = platform.getPose();
                    // 获取当前机器人地图
                    List<MapType> mapTypes = platform.getAvailableMaps();
                    MapType mapType = mapTypes.get(0);
                    RectF knownArea = platform.getKnownArea(mapType);
                    // 获取当前机器人雷达扫描信息
                    LaserScan currentLaserScan = platform.getLaserScan();
                    // 同步锁,设置类变量
                    synchronized (RobotAgent.this) {
                        // 机器人姿态
                        mRobotPose = robotPose;
                        // 地图类型
                        mMapType = mapType;
                        // 雷达扫描信息
                        mLaserScan = currentLaserScan;
                    }
                    // 创建当前扫描的可见区域
                    RectF clipArea = new RectF(
                            robotPose.getLocation().getX() - UPDATE_MAP_RADIX * 2,
                            robotPose.getLocation().getY() - UPDATE_MAP_RADIX * 2,
                            robotPose.getLocation().getX() + UPDATE_MAP_RADIX * 2,
                            robotPose.getLocation().getY() + UPDATE_MAP_RADIX * 2
                    );
                    knownArea.intersect(clipArea);
                    // 更新地图
                    updateMap(knownArea);
                }
            } catch (Exception e) {
                onRequestError();
            }
        }
    }

    /**
     * 保存地图任务
     */
    private class JobSaveMap implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK局部变量
            SlamwareCorePlatform platform;
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
            }
            // 如果思岚SDK为空的话直接返回
            if (platform == null) {
                return;
            }
            // 声明路径临时变量
            String path;
            // 同步锁
            synchronized (RobotAgent.this) {
                path = mSavePath;
            }
            // 如果路径为空的话直接返回
            if (TextUtils.isEmpty(path)) {
                return;
            }
            try {
                // 获取完全版地图并保存到指定路径中
                CompositeMap compositeMap = platform.getCompositeMap();
                CompositeMapHelper mapHelper = new CompositeMapHelper();
                mapHelper.saveFile(path, compositeMap);
            } catch (Throwable throwable) {
                onRequestError();
            }
            // 回调保存地图成功,清空本地路径
            mSavePath = "";
            EventBus.getDefault().post(new MapOperationEvent(MapOperationEvent.TYPE_SAVE_MAP));
        }
    }

    /**
     * 回充电桩任务
     */
    private class JobGoHome implements Runnable {
        @Override
        public void run() {
            try {
                // 停止所有动作定时器
                cancelMoveTimer();
                // 如果当前有运动则放弃之前的运动
                try {
                    IMoveAction action = mRobotPlatform.getCurrentAction();
                    if (action != null) {
                        action.cancel();
                    }
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                // 执行回桩操作
                mRobotPlatform.goHome();
            } catch (Throwable throwable) {
                onRequestError();
            }
        }
    }

    /**
     * 停止机器人所有动作任务
     */
    private class JobCancelAllActions implements Runnable {
        @Override
        public void run() {
            // 停止所有动作定时器
            cancelMoveTimer();
            // 如果当前有运动则放弃之前的运动
            try {
                IMoveAction action = mRobotPlatform.getCurrentAction();
                if (action != null) {
                    action.cancel();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            // 重置所有标识位
            isUpdatingPowerStatus = false;
            isUpdatingLaserScan = false;
            isUpdatingPose = false;
            isUpdatingMoveAction = false;
            // 回调更新地图
            EventBus.getDefault().post(new MapUpdateEvent(new RectF()));
        }
    }

    /**
     * 获取当前坐标点信息任务
     */
    private class JobGetLocation implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK局部变量
            SlamwareCorePlatform platform;
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
            }
            // 如果思岚SDK为空直接返回
            if (platform == null) {
                return;
            }
            // 声明坐标点局部变量
            Location location;
            try {
                // 获取当前点信息
                location = platform.getLocation();
                // 回调位置点信息
                EventBus.getDefault().post(new LocationEvent(location));
            } catch (Throwable throwable) {
                onRequestError();
            }
        }
    }

    /**
     * 添加虚拟墙任务
     */
    private class JobAddWalls implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK和需要添加的虚拟墙集合变量
            SlamwareCorePlatform platform;
            Vector<Line> wallsToAdd = new Vector<>();
            // 同步锁
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
                // 如果SDK为空的话返回
                if (platform == null) {
                    return;
                }
                // 中间变量
                Vector<Line> tmp = new Vector<>(mWallsToAdd);
                // 清空类变量和局部变量
                mWallsToAdd.clear();
                wallsToAdd.clear();
                // 赋值局部变量
                wallsToAdd.addAll(tmp);
            }
            // 非空判断
            if (wallsToAdd.isEmpty()) {
                return;
            }
            try {
                // 调用SDK方法添加虚拟墙
                platform.addWalls(wallsToAdd);
            } catch (Exception e) {
                onRequestError();
            }
            // 更新虚拟墙信息
            updateWalls();
        }
    }

    /**
     * 更新虚拟墙信息任务
     */
    private class JobUpdateWalls implements Runnable {
        @Override
        public void run() {
            // 声明思岚SDK和需要添加的虚拟墙集合变量
            SlamwareCorePlatform platform;
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
                if (platform == null) {
                    isUpdatingWalls = false;
                    return;
                }
            }
            // 声明虚拟墙局部变量
            Vector<Line> walls;
            try {
                // 获取所有的虚拟墙数据
                walls = platform.getWalls();
            } catch (Exception e) {
                onRequestError();
                isUpdatingWalls = false;
                return;
            }
            // 同步锁,保证记录的虚拟墙数据唯一
            synchronized (RobotAgent.this) {
                mWalls = walls;
            }
            // 重置标识位
            isUpdatingWalls = false;
            // 回调更新虚拟墙
            EventBus.getDefault().post(new WallUpdateEvent(walls));
        }
    }

    /**
     * 根据ID移除虚拟墙任务
     */
    private class JobRemoveWallById implements Runnable {
        @Override
        public void run() {
            // 声明SDK局部变量和虚拟墙ID局部变量
            SlamwareCorePlatform platform;
            int wallId;
            // 同步锁确保ID唯一
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
                if (platform == null) {
                    return;
                }
                wallId = mWallId;
            }
            try {
                // 调用SDK清除该虚拟墙
                platform.clearWallById(wallId);
            } catch (Exception e) {
                onRequestError();
            }
            // 更新虚拟墙信息
            updateWalls();
        }
    }

    /**
     * 更新运动轨迹工作线程
     */
    private class JobUpdateMoveAction implements Runnable {
        @Override
        public void run() {
            // 思岚SDK局部变量
            SlamwareCorePlatform platform;
            synchronized (RobotAgent.this) {
                platform = mRobotPlatform;
                if (platform == null) {
                    isUpdatingMoveAction = false;
                    return;
                }
            }
            // 声明运动局部变量
            IMoveAction moveAction;
            Path remainingMilestones = null;
            Path remainingPath = null;
            // 获取当前运动状态
            try {
                moveAction = platform.getCurrentAction();
                if (moveAction != null) {
                    remainingMilestones = moveAction.getRemainingMilestones();
                    remainingPath = moveAction.getRemainingPath();
                }
            } catch (Throwable e) {
                onRequestError();
                isUpdatingMoveAction = false;
                return;
            }
            // 重置标识位
            isUpdatingMoveAction = false;
            // 回调动作更新事件
            EventBus.getDefault().post(new MoveActionUpdateEvent(remainingMilestones, remainingPath));
        }
    }
}
