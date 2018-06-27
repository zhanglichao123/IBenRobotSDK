package com.samton.ibenrobotdemo.widgets;

import android.content.Context;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.samton.ibenrobotdemo.R;
import com.samton.ibenrobotdemo.map.RobotAgent;
import com.slamtec.slamware.action.Path;
import com.slamtec.slamware.geometry.Line;
import com.slamtec.slamware.geometry.PointF;

import java.lang.ref.WeakReference;
import java.util.Vector;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/19 10:21
 *   desc    : 思岚地图控件
 *   version : 1.0
 * </pre>
 */
public class MapView extends FrameLayout {
    /**
     * 地图控件比例
     */
    private final static float kRPMapViewControllerEasingRate = 0.1f;
    /**
     * 屏幕宽度
     */
    private static int SCREEN_WIDTH = 0;
    /**
     * 屏幕高度
     */
    private static int SCREEN_HEIGHT = 0;
    /**
     * 思岚SDK
     */
    private WeakReference<RobotAgent> mRobotAgent;
    /**
     * 是否手动进行了地图缩放
     */
    private boolean isMapScaleSet = false;
    /**
     * 是否手动设置了中心点坐标
     */
    private boolean isCenterLocationSet = false;
    /**
     * 想要放大的倍数
     */
    private float mTargetMapScale;
    /**
     * 当前的放大倍数
     */
    private float mCurrentMapScale;
    /**
     * 想要变换的中间点
     */
    private PointF mTargetCenterLocation;
    /**
     * 当前中间点
     */
    private PointF mCurrentCenterLocation;
    /**
     * 地图变换
     */
    private Point mMapTransition;
    /**
     * 地图旋转角度
     */
    private float mMapRotation;
    /**
     * 机器人实际旋转角度
     */
    private float mRobotExtraRotation;
    /**
     * 瓦片地图
     */
    private ScrollTileMapView mScrollMapView;
    /**
     * 虚拟墙
     */
    private VirtualWallView mVirtualWallView;
    /**
     * 行走轨迹图片
     */
    private MoveActionView mMoveActionView;
    /**
     * 机器人图片空间
     */
    private ImageView mRobotIndicatorView;

    /**
     * 构造函数
     */
    public MapView(@NonNull Context context) {
        super(context);
    }

    /**
     * 构造函数
     */
    public MapView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造函数
     */
    public MapView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 初始化地图
     *
     * @param robotAgent 思岚SDK
     * @param width      屏幕宽度
     * @param height     屏幕高度
     */
    public void init(WeakReference<RobotAgent> robotAgent, int width, int height) {
        // 设置背景颜色
        setBackgroundColor(getContext().getResources().getColor(R.color.MapViewBackground));
        // 思岚SDK
        mRobotAgent = robotAgent;
        // 屏幕宽高,用来等比缩放逻辑地图
        SCREEN_WIDTH = width;
        SCREEN_HEIGHT = height;
        // 缩放相关
        mTargetMapScale = 1f;
        mCurrentMapScale = 1f;
        // 中心点相关
        mTargetCenterLocation = new PointF();
        mCurrentCenterLocation = new PointF();
        // 变换和角度相关
        mMapTransition = new Point();
        mMapRotation = 0f;
        mRobotExtraRotation = 0f;
        // 初始化地图控件
        initView();
    }

    /**
     * 初始化地图控件
     */
    private void initView() {
        // 默认占满控件所有控件
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        // 瓦片地图
        mScrollMapView = new ScrollTileMapView(getContext(), mRobotAgent);
        // 虚拟墙
        mVirtualWallView = new VirtualWallView(getContext(), mRobotAgent);
        // 行动轨迹
        mMoveActionView = new MoveActionView(getContext(),mRobotAgent);
        // 把各种控件都放入地图中
        addView(mScrollMapView, params);
        addView(mVirtualWallView, params);
        addView(mMoveActionView, params);
        // 代表机器人的图片
        mRobotIndicatorView = new ImageView(getContext());
        mRobotIndicatorView.setImageResource(R.mipmap.robotindicator);
        // 更改布局参数,把机器人图片放在最中间
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        // 添加机器人图片到地图中
        addView(mRobotIndicatorView, params);
        // 让地图中的控件默认左上角布局
        mScrollMapView.layout(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        mMoveActionView.layout(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    /**
     * 获取当前地图缩放比
     *
     * @return 当前地图缩放比
     */
    public float getMapScale() {
        return mCurrentMapScale;
    }

    /**
     * 设置地图缩放比
     *
     * @param scale    缩放比
     * @param animated 是否有动画
     */
    public void setMapScale(float scale, boolean animated) {
        if (!animated || !isMapScaleSet) {
            updateCurrentMapScale(scale);
            mTargetMapScale = scale;
            isMapScaleSet = true;
        } else {
            mTargetMapScale = scale;
        }
    }

    /**
     * 根据缩放比例进行地图缩放
     *
     * @param mapScale 缩放比例
     */
    private void updateCurrentMapScale(float mapScale) {
        // 保存缩放比
        mCurrentMapScale = mapScale;
        // 更新地图中的瓦片地图缩放比
        mScrollMapView.setMapScale(mapScale);
        // 更新地图中的虚拟墙缩放比
        mVirtualWallView.setMapScale(mapScale);
        // 更新行走轨迹的缩放比
        mMoveActionView.setMapScale(mapScale);
    }

    /**
     * 获取当前中心点
     *
     * @return 当前中心点
     */
    public PointF getCenterLocation() {
        return mCurrentCenterLocation;
    }

    /**
     * 设置中心点
     *
     * @param location 中心点
     * @param animated 是否有动画
     */
    public void setCenterLocation(PointF location, boolean animated) {
        if (!animated || !isCenterLocationSet) {
            updateCurrentCenterLocation(location);
            mTargetCenterLocation = location;
            isCenterLocationSet = true;
        } else {
            mTargetCenterLocation = location;
        }
    }

    /**
     * 根据中心点更改当前中心点信息
     *
     * @param centerLocation 中心点
     */
    private void updateCurrentCenterLocation(PointF centerLocation) {
        // 保存中心点
        mCurrentCenterLocation = centerLocation;
        // 更新地图中的瓦片地图中心点
        mScrollMapView.setCenterLocation(centerLocation);
        // 更新地图中的虚拟墙中心点
        mVirtualWallView.setCenterLocation(centerLocation);
        // 更新行动轨迹的中心点
        mMoveActionView.setCenterLocation(centerLocation);
    }

    /**
     * 获取地图变换值
     *
     * @return 地图变换值
     */
    public Point getMapTransition() {
        return mMapTransition;
    }

    /**
     * 设置地图变换值
     *
     * @param mapTransition 地图变换值
     */
    public void setMapTransition(Point mapTransition) {
        // 非空判断
        if (mapTransition == null) {
            return;
        }
        // 变换值更新
        Point trans = new Point(mMapTransition.x + mapTransition.x,
                mMapTransition.y + mapTransition.y);
        // 如果变换值超出屏幕宽或者高的一半则退出
        if (Math.abs(trans.x) > SCREEN_WIDTH / 2 || Math.abs(trans.y) > SCREEN_HEIGHT / 2) {
            return;
        }
        // 保存地图变换
        mMapTransition = trans;
        // 设置机器人图片位置
        mRobotIndicatorView.setX(mRobotIndicatorView.getX() + mapTransition.x);
        mRobotIndicatorView.setY(mRobotIndicatorView.getY() + mapTransition.y);
        // 设置瓦片地图变换值
        mScrollMapView.setTransition(mapTransition);
        // 设置虚拟墙变换值
        mVirtualWallView.setTransition(mapTransition);
        // 设置行动轨迹
        mMoveActionView.setTransition(mapTransition);
        // 刷新虚拟墙的点
        mVirtualWallView.refreshIndicatorAfterZoomAndRotate();
        // 刷新行走轨迹
        mMoveActionView.refreshMilestonesAfterTransition();
    }

    /**
     * 获取地图旋转值
     *
     * @return 地图旋转值
     */
    public float getMapRotation() {
        return mMapRotation;
    }

    /**
     * 设置地图旋转
     *
     * @param mapRotation 地图旋转值
     */
    public void setMapRotation(float mapRotation) {
        // 旋转值相加
        mMapRotation += mapRotation;
        // 计算角度
        float degree = (float) ((mRobotExtraRotation + mMapRotation) * 180 / Math.PI);
        // 设置机器人图片旋转值
        mRobotIndicatorView.setRotation(degree);
        // 设置瓦片地图旋转值
        mScrollMapView.setRotation(mapRotation);
        // 设置虚拟墙旋转值
        mVirtualWallView.setRotation(mapRotation);
        // 设置行走轨迹的旋转值
        mMoveActionView.setRotation(mapRotation);
        // 瓦片地图旋转后进行更新UI
        mScrollMapView.applyRotation();
        // 虚拟墙旋转后更新点
        mVirtualWallView.refreshIndicatorAfterZoomAndRotate();
    }

    /**
     * 设置机器人点旋转值
     *
     * @param robotExtraRotation 旋转值
     */
    public void setRobotIndicatorRotation(float robotExtraRotation) {
        mRobotExtraRotation = robotExtraRotation;
        float rotation = robotExtraRotation + mMapRotation;
        float degree = (float) (rotation * 180 / Math.PI);
        // 设置机器人图片旋转点
        mRobotIndicatorView.setRotation(degree);
    }

    /**
     * 更新虚拟墙数据
     *
     * @param walls 要更新的虚拟墙
     */
    public void updateWalls(Vector<Line> walls) {
        mVirtualWallView.updateWalls(walls);
    }

    /**
     * 设置虚拟墙的点
     *
     * @param touchPoint 点
     */
    public void setVirtualWallIndicator(Point touchPoint) {
        mVirtualWallView.setWallIndicator(touchPoint);
    }

    /**
     * 根据点移除虚拟墙
     *
     * @param touchPoint 点
     */
    public void removeWall(Point touchPoint) {
        mVirtualWallView.removeWall(touchPoint);
    }

    /**
     * 移除虚拟墙的点
     */
    public void clearWallIndicator() {
        mVirtualWallView.clearIndicators();
    }

    /**
     * 更新地图区域
     *
     * @param area 地图区域
     */
    public void updateMapArea(RectF area) {
        mScrollMapView.updateArea(mRobotAgent.get().getMapData().physicalAreaToLogicalArea(area));
    }

    /**
     * 更新地图
     */
    public void update() {
        // 如果已经进行了手动更改地图缩放并且现在的缩放比和想要更改的缩放比不相等
        // 进行当前地图的缩放操作
        if (isMapScaleSet && mCurrentMapScale != mTargetMapScale) {
            updateCurrentMapScale(ease(mCurrentMapScale, mTargetMapScale));
        }
        // 如果已经进行了手动设置中心店并且当前的中心点和要更改的中心点不一样
        // 进行当前地图的更新中心点操作
        if (isCenterLocationSet && (mCurrentCenterLocation.getX() != mTargetCenterLocation.getX() ||
                mCurrentCenterLocation.getY() != mTargetCenterLocation.getY())) {
            float newX = ease(mCurrentCenterLocation.getX(), mTargetCenterLocation.getX());
            float newY = ease(mCurrentCenterLocation.getY(), mTargetCenterLocation.getY());
            updateCurrentCenterLocation(new PointF(newX, newY));
        }
    }

    /**
     * 做减法
     *
     * @param old    减数
     * @param target 被减数
     * @return 结果
     */
    private float ease(float old, float target) {
        float diff = target - old;
        float absDiff = Math.abs(diff);

        if (absDiff < 0.01f) {
            return target;
        }

        return old + diff * kRPMapViewControllerEasingRate;
    }

    /**
     * 行走至指定点
     * @param rawPoint 指定点
     */
    public void moveTo(Point rawPoint) {
        mScrollMapView.moveTo(rawPoint);
    }

    /**
     * 更新要行走的点
     * @param remainingMilestones 行走的点
     * @param remainingPath 行走的路线
     */
    public void updateRemainingMilestones(Path remainingMilestones, Path remainingPath) {
        mMoveActionView.updateRemainingMilestones(remainingMilestones, remainingPath);
    }
}
