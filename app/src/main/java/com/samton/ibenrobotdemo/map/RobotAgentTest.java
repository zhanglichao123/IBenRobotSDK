package com.samton.ibenrobotdemo.map;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.samton.IBenRobotSDK.utils.ToastUtils;
import com.samton.ibenrobotdemo.R;
import com.samton.ibenrobotdemo.events.BatteryEvent;
import com.samton.ibenrobotdemo.events.ConnectedEvent;
import com.samton.ibenrobotdemo.events.ConnectionLostEvent;
import com.samton.ibenrobotdemo.events.MapOperationEvent;
import com.samton.ibenrobotdemo.events.MapUpdateEvent;
import com.samton.ibenrobotdemo.events.MoveActionUpdateEvent;
import com.samton.ibenrobotdemo.events.RobotPoseUpdateEvent;
import com.samton.ibenrobotdemo.events.WallUpdateEvent;
import com.samton.ibenrobotdemo.widgets.MapView;
import com.slamtec.slamware.action.MoveDirection;
import com.slamtec.slamware.geometry.PointF;
import com.slamtec.slamware.robot.Pose;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import static com.samton.ibenrobotdemo.events.MapOperationEvent.TYPE_LOAD_MAP;
import static com.samton.ibenrobotdemo.events.MapOperationEvent.TYPE_SAVE_MAP;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/15 15:36
 *   desc    : 测试动态地图
 *   version : 1.0
 * </pre>
 */
public class RobotAgentTest extends AppCompatActivity
        implements View.OnClickListener {
    /**
     * 持续运动间隔
     */
    private static final int mMovePeriod = 20;
    /**
     * 最小缩放
     */
    private final static float kRPMapViewMinScale = 0.5f;
    /**
     * 最大缩放
     */
    private final static float kRPMapViewMaxScale = 16.0f;
    /**
     * 虚拟墙编辑状态 - 无
     */
    private final static int MODE_WALL_NONE = 0;
    /**
     * 虚拟墙编辑状态 - 添加
     */
    private final static int MODE_WALL_ADD = 1;
    /**
     * 虚拟墙编辑状态 - 移除
     */
    private final static int MODE_WALL_REMOVE = 2;
    /**
     * 状态栏高度
     */
    private static int mStatusBarHeight = 0;
    /**
     * 地图缩放
     */
    private Float mMapScale = 2.0f;
    /**
     * 地图旋转
     */
    private Float mMapRotation = 0.0f;
    /**
     * 地图变换
     */
    private Point mMapTransition = new Point(0, 0);
    /**
     * 思岚SDK
     */
    private RobotAgent mRobotAgent;
    /**
     * 机器人连接状态
     */
    private TextView mRobotStatus;
    private TextView mRobotPower;
    /**
     * 连接按钮
     */
    private Button mConnectBtn;
    /**
     * 地图
     */
    private MapView mMapView;
    /**
     * 更新机器人状态计时器
     */
    private Timer mUpdateTimer;
    private TimerTask mUpdateTask;
    /**
     * 手势
     */
    private GestureDetector mGestureDetector;
    /**
     * 手势监听
     */
    private GestureDetector.OnGestureListener mGestureListener
            = new GestureDetector.OnGestureListener() {
        @Override
        public void onMapTap(MotionEvent event) {
            // 地图点击
            int x = Math.round(event.getX());
            int y = Math.round(event.getY()) - mStatusBarHeight;
            Point rawPoint = new Point(x, y);
//            if (isWallEditMode) {
//                if (wallEditMode == MODE_WALL_ADD) {
//                    mapView.setVirtualWallIndicator(rawPoint);
//                } else if (wallEditMode == MODE_WALL_REMOVE) {
//                    mapView.removeWall(rawPoint);
//                    wallEditMode = MODE_WALL_NONE;
//                }
//            } else {
//                mMapView.moveTo(rawPoint);
//            }
            mMapView.moveTo(rawPoint);
        }

        @Override
        public void onMapPinch(float factor) {
            // 地图缩放
            mMapScale *= factor;
            if (mMapScale < kRPMapViewMinScale) {
                mMapScale = kRPMapViewMinScale;
            } else if (mMapScale > kRPMapViewMaxScale) {
                mMapScale = kRPMapViewMaxScale;
            }
            mMapView.setMapScale(mMapScale, false);
        }

        @Override
        public void onMapMove(int distanceX, int distanceY) {
            mMapView.setMapTransition(new Point(Math.round(distanceX), Math.round(distanceY)));
        }

        @Override
        public void onMapRotate(float factor) {
            mMapView.setMapRotation(factor);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_test);
        // 初始化布局
        initView();
        // 初始化数据
        initData();
        // 初始化地图
        initMapView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        // 获取状态栏高度
        int resId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resId > 0) {
            mStatusBarHeight = getResources().getDimensionPixelSize(resId);
        }
        // 控件
        mMapView = (MapView) findViewById(R.id.mMapView);
        mRobotStatus = (TextView) findViewById(R.id.mRobotStatus);
        mRobotPower = (TextView) findViewById(R.id.mRobotPower);
        mConnectBtn = (Button) findViewById(R.id.mConnectBtn);
        // 点击事件
        mConnectBtn.setOnClickListener(this);
        findViewById(R.id.mForwardBtn).setOnClickListener(this);
        findViewById(R.id.mLeftBtn).setOnClickListener(this);
        findViewById(R.id.mStopBtn).setOnClickListener(this);
        findViewById(R.id.mRightBtn).setOnClickListener(this);
        findViewById(R.id.mBackwardBtn).setOnClickListener(this);
        findViewById(R.id.mGoHomeBtn).setOnClickListener(this);
        findViewById(R.id.mSaveBtn).setOnClickListener(this);
        findViewById(R.id.mLoadBtn).setOnClickListener(this);
        findViewById(R.id.mClearBtn).setOnClickListener(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 思岚SDK
        mRobotAgent = RobotAgent.getInstance();
        // 手势监听
        mGestureDetector = new GestureDetector(mGestureListener);
    }

    /**
     * 初始化地图
     */
    private void initMapView() {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        // mMapView.init(new WeakReference<>(mRobotAgent), size.x, size.y);
        mMapView.init(new WeakReference<>(mRobotAgent), size.y, size.x);
        mMapView.setMapScale(mMapScale, false);
        mMapView.setMapTransition(mMapTransition);
        mMapView.setMapRotation(mMapRotation);
    }

    /**
     * 连接成功事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectedEvent event) {
        mRobotStatus.setText("已经连接");
        mConnectBtn.setEnabled(false);
        // 获取电量信息
        mRobotAgent.getPowerStatus();
        // 更新机器人状态
        startUpdateTimer();
    }

    /**
     * 开启更新机器人状态计时器
     */
    private void startUpdateTimer() {
        // 稳妥操作
        stopUpdateTimer();
        // 非空判断
        if (mUpdateTimer == null) {
            mUpdateTimer = new Timer();
        }
        if (mUpdateTask == null) {
            mUpdateTask = new TimerTask() {
                @Override
                public void run() {
                    // 调用SDK更新姿态
                    mRobotAgent.updatePose();
                    // 调用SDK更新移动轨迹
                    mRobotAgent.updateMoveAction();
                    // 调用SDK更新地图
                    mRobotAgent.updateMap();
                    // 调用SDK更新虚拟墙信息
                    mRobotAgent.updateWalls();
                    // 更新地图View
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mMapView.update();
                        }
                    });
                }
            };
        }
        // 开启计时器更新机器人状态
        mUpdateTimer.schedule(mUpdateTask, 0, 1000);
    }

    /**
     * 停止更新机器人状态计时器
     */
    private void stopUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
        if (mUpdateTask != null) {
            mUpdateTask.cancel();
            mUpdateTask = null;
        }
    }

    /**
     * 连接超时事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ConnectionLostEvent event) {
        mRobotStatus.setText("断开连接--->连接超时,请从新连接");
        mConnectBtn.setEnabled(true);
    }

    /**
     * 电量事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BatteryEvent event) {
        try {
            JSONObject jsonObject = new JSONObject(event.getBatteryInfo());
            String percent = "当前电量" + jsonObject.optString("batteryPercent") + "%";
            mRobotPower.setText(percent);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 更新地图事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MapUpdateEvent event) {
        // 如果需要更新的区域为空的话放弃
        if (event.getArea().isEmpty()) {
            return;
        }
        // 更新地图
        mMapView.updateMapArea(event.getArea());
    }

    /**
     * 机器人姿态更新事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(RobotPoseUpdateEvent event) {
        Pose pose = event.getPose();
        if (pose == null) {
            return;
        }
        Point logicalLocation = mRobotAgent.getMapData()
                .coordinateToPixel(pose.getX(), pose.getY());

        mMapView.setCenterLocation(new PointF(logicalLocation.x, logicalLocation.y), false);
        mMapView.setRobotIndicatorRotation((float) (-pose.getYaw() + (Math.PI / 2)));
    }

    /**
     * 更新虚拟墙事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(WallUpdateEvent event) {
        mMapView.updateWalls(event.getWalls());
    }

    /**
     * 动作更新事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MoveActionUpdateEvent event) {
        mMapView.updateRemainingMilestones(event.getRemainingMilestones(),
                event.getRemainingPath());
    }

    /**
     * 保存地图事件
     *
     * @param event 事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(MapOperationEvent event) {
        if (event.getOperationType() == TYPE_SAVE_MAP) {
            ToastUtils.showShort("保存地图成功");
        } else if (event.getOperationType() == TYPE_LOAD_MAP) {
            // 开启计时器更新地图显示
            startUpdateTimer();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 连接按钮
            case R.id.mConnectBtn:
                // 不做重复连接
                if (!mRobotAgent.isConnected()) {
                    connectRobot();
                } else {
                    ToastUtils.showShort("机器人已经连接了……");
                }
                break;
            // 前进
            case R.id.mForwardBtn:
                mRobotAgent.moveByDirection(MoveDirection.FORWARD, mMovePeriod);
                break;
            // 左转
            case R.id.mLeftBtn:
                mRobotAgent.moveByDirection(MoveDirection.TURN_LEFT, mMovePeriod);
                break;
            // 停止
            case R.id.mStopBtn:
                mRobotAgent.cancelAllActions();
                break;
            // 右转
            case R.id.mRightBtn:
                mRobotAgent.moveByDirection(MoveDirection.TURN_RIGHT, mMovePeriod);
                break;
            // 后退
            case R.id.mBackwardBtn:
                mRobotAgent.moveByDirection(MoveDirection.BACKWARD, mMovePeriod);
                break;
            // 回充电桩
            case R.id.mGoHomeBtn:
                if (mRobotAgent.isConnected()) {
                    mRobotAgent.goHome();
                } else {
                    ToastUtils.showShort("请先连接机器人！");
                }
                break;
            // 保存地图按钮
            case R.id.mSaveBtn:
                mRobotAgent.saveMap(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/map");
                break;
            // 加载地图按钮
            case R.id.mLoadBtn:
                stopUpdateTimer();
                mRobotAgent.loadMap(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/map");
                break;
            // 清空地图
            case R.id.mClearBtn:
                mRobotAgent.clearMap();
                break;
        }
    }

    /**
     * 连接机器人
     */
    private void connectRobot() {
        mRobotAgent.connectRobot("192.168.11.1", 1445);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMapScale = mMapView.getMapScale();
                break;
        }
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
