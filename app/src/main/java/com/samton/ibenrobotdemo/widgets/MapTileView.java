package com.samton.ibenrobotdemo.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import com.samton.IBenRobotSDK.utils.LogUtils;
import com.samton.ibenrobotdemo.map.ExecutorUtil;
import com.samton.ibenrobotdemo.map.ImageUtil;
import com.samton.ibenrobotdemo.map.RobotAgent;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/19 11:36
 *   desc    : 瓦片地图中的每个瓦片
 *   version : 1.0
 * </pre>
 */
public class MapTileView extends View {
    /**
     * 思岚SDK
     */
    private WeakReference<RobotAgent> mRobotAgent;
    /**
     * 瓦片下标
     */
    private Point mIndex;
    /**
     * 瓦片区域
     */
    private Rect mArea;
    /**
     * 瓦片区域图像
     */
    private Bitmap mBitmap;
    /**
     * 画笔
     */
    private Paint mPaint;
    /**
     * 缩放比例
     */
    private float mScale;
    /**
     * 更新地图线程
     */
    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            // 初始化要更新的数据
            byte[] buffer = new byte[Math.abs(mArea.width() * mArea.height())];
            // 更新地图
            if (mRobotAgent.get() != null) {
                mRobotAgent.get().getMapData().fetch(new Rect(mArea), buffer);
            }
            // 中间变量
            Bitmap tempBitmap = ImageUtil.createImage(buffer, new Point(Math.abs(mArea.width()),
                    Math.abs(mArea.height())));
            // 同步锁,更新地图数据
            synchronized (MapTileView.this) {
                mBitmap = tempBitmap;
            }
            // 更新View
            postInvalidate();
        }
    };

    /**
     * 默认构造
     *
     * @param context 上下文
     */
    public MapTileView(Context context) {
        super(context);
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param agent   思岚SDK
     * @param index   瓦片的下标
     * @param area    瓦片的区域
     */
    public MapTileView(Context context, WeakReference<RobotAgent> agent, Point index,
                       Rect area) {
        super(context);
        // 初始化数据
        mRobotAgent = agent;
        mIndex = index;
        mArea = area;
        mScale = 1f;
        // 初始化画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画布缩放
        canvas.scale(mScale, mScale);
        // 缩放之后画图片
        synchronized (this) {
            if (mBitmap != null) {
                canvas.drawBitmap(mBitmap, 0f, 0f, mPaint);
            }
        }
        super.onDraw(canvas);
    }

    /**
     * 更新瓦片区域
     *
     * @param area 要更新的瓦片区域
     */
    public void updateArea(Rect area) {
        // 取交汇值
        if (area.intersect(mArea)) {
            LogUtils.d("没有相交点,地图区域未被更改");
        }
        // 如果要更新的区域是空的,直接返回
        if (area.isEmpty()) {
            return;
        }
        // 开启线程更新思岚SDK中的地图数据
        ExecutorUtil.getInstance().execute(mUpdateRunnable);
    }

    /**
     * 更新地图缩放比例
     *
     * @param scale 想要更改的缩放比例
     */
    public void updateScale(float scale) {
        mScale = scale;
    }

    /**
     * 获取当前瓦片地图的下标
     *
     * @return 下标
     */
    public Point getIndex() {
        return mIndex;
    }
}
