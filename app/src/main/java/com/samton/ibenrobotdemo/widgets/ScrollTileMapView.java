package com.samton.ibenrobotdemo.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;

import com.samton.ibenrobotdemo.R;
import com.samton.ibenrobotdemo.map.RobotAgent;
import com.slamtec.slamware.geometry.PointF;
import com.slamtec.slamware.robot.Location;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/19 11:30
 *   desc    : 可缩放的瓦片地图
 *   version : 1.0
 * </pre>
 */
public class ScrollTileMapView extends SlamBaseView {
    /**
     * 瓦片大小
     */
    private final static int kRPScrollTileMapViewTileSize = 512;
    /**
     * 瓦片集合
     */
    private ArrayList<MapTileView> mTiles;
    /**
     * 点位集合
     */
    private Vector<ImageView> mMarkerImages;
    /**
     * 定位集合
     */
    private List<Location> mLocations;
    /**
     * 标注点位图片
     */
    private Bitmap mMarkerImage;
    /**
     * 充电桩图片
     */
    private Bitmap mHomeImage;
    /**
     * 点位图片偏移量X
     */
    private int mMarkerOffsetX;
    /**
     * 点位图片偏移量Y
     */
    private int mMarkerOffsetY;

    /**
     * 默认构造函数
     */
    public ScrollTileMapView(Context context) {
        super(context);
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param sdk     思岚SDK
     */
    public ScrollTileMapView(Context context, WeakReference<RobotAgent> sdk) {
        super(context, sdk);
        // 瓦片集合
        mTiles = new ArrayList<>();
        // 初始化点位集合
        mMarkerImages = new Vector<>();
        // 初始化点位集合
        mLocations = new ArrayList<>();
        // 解析充电桩图片
        mHomeImage = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_marker_home);
        // 解析点位图片
        mMarkerImage = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_marker_point);
        // X偏移量
        mMarkerOffsetX = mHomeImage.getWidth() / 2;
        // Y偏移量
        mMarkerOffsetY = mHomeImage.getHeight() / 2;
        // 背景色透明
        setBackgroundColor(Color.TRANSPARENT);
    }

    /**
     * 更新Marker
     */
    public void updateMarker(List<Location> locations) {
        // 清空本地数据
        mLocations.clear();
        // 充电桩位置
        Location origin = moveSdk.get().getOrigin();
        // 添加充电桩位置
        mLocations.add(origin);
        // 将其他的数据放入到集合中
        mLocations.addAll(locations);
        // 刷新Marker
        refreshMarker();
    }

    /**
     * 刷新Marker
     */
    private void refreshMarker() {
        // 移除之前的所有点位信息
        removeImageView();
        // 偏移量
        Point offset = getLayoutOffset();
        // 遍历点位,画标注
        for (int i = 0; i < mLocations.size(); i++) {
            // 定位信息
            Location location = mLocations.get(i);
            // 约束
            PointF coordinate = new PointF(location.getX(), location.getY());
            // 中心点
            Point center = layoutRotatedCoordinateForPhysicalCoordinate(coordinate, offset);
            // 要绘制的图片
            ImageView markerView = new ImageView(getContext());
            // 充电桩位置
            if (i == 0) {
                markerView.setImageBitmap(mHomeImage);
            } else {
                markerView.setImageBitmap(mMarkerImage);
            }
            // 添加到布局中
            addView(markerView);
            // 手动测量位置
            markerView.layout(center.x - mMarkerOffsetX, center.y - mMarkerOffsetY,
                    center.x + mMarkerOffsetX, center.y + mMarkerOffsetY);
            // 将点位加入到数组中
            mMarkerImages.add(markerView);
        }
    }

    /**
     * 移除之前的所有点位信息
     */
    private void removeImageView() {
        for (ImageView markerImage : mMarkerImages) {
            removeView(markerImage);
        }
        mMarkerImages.clear();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        // 刷新UI
        refreshUI();
    }

    /**
     * 刷新UI
     */
    private void refreshUI() {
        // 获取偏移量
        Point offset = getLayoutOffset();
        // 遍历瓦片
        for (MapTileView tileView : mTiles) {
            // 为空的话跳出本次循环
            if (tileView == null) {
                continue;
            }
            // 获取该瓦片的下标
            Point index = tileView.getIndex();
            // 根据规则生成屏幕矩形
            int left = index.x * kRPScrollTileMapViewTileSize;
            int top = index.y * kRPScrollTileMapViewTileSize;
            int right = left + kRPScrollTileMapViewTileSize;
            int bottom = top + kRPScrollTileMapViewTileSize;
            Rect logicalRect = new Rect(left, top, right, bottom);
            // 生成高精度矩形
            RectF layoutRect = layoutRectForLogicalRect(logicalRect, offset);
            // 将该瓦片从新布局
            tileView.layout(Math.round(layoutRect.left), Math.round(layoutRect.top),
                    Math.round(layoutRect.right), Math.round(layoutRect.bottom));
            // 旋转的情况
            float rotationPivotX = getWidth() / 2 - tileView.getX() + getTransition().x;
            float rotationPivotY = getHeight() / 2 - tileView.getY() + getTransition().y;
            // 设置锚点的X坐标值，以像素为单位。默认是View的中心。
            tileView.setPivotX(rotationPivotX);
            // 设置锚点的Y坐标值，以像素为单位。默认是View的中心。
            tileView.setPivotY(rotationPivotY);
            // 设置View在Z轴上的旋转角度
            tileView.setRotation(this.getRotation());
            // 设置缩放比例
            tileView.updateScale(getMapScale());
        }
    }

    /**
     * 更新区域
     *
     * @param area 要更新的区域
     */
    public void updateArea(Rect area) {
        // 获取该区域的瓦片开始下标
        Point startIndex = getTileIndexForPixel(new Point(area.left, area.top));
        // 获取该区域的瓦片数量
        Point count = getTileCountForSize(new Point(area.width(), area.height()));
        // 计算所有的瓦片数
        if (area.right >= ((startIndex.x + count.x) * kRPScrollTileMapViewTileSize)) {
            count.x++;
        }
        if (area.bottom >= ((startIndex.y + count.y) * kRPScrollTileMapViewTileSize)) {
            count.y++;
        }
        // 遍历瓦片,更新每个瓦片的区域
        for (int y = 0; y < count.y; y++) {
            int j = y + startIndex.y;
            for (int x = 0; x < count.x; x++) {
                int i = x + startIndex.x;
                MapTileView tileView = lookupOrCreateTileForIndex(new Point(i, j));
                tileView.updateArea(area);
            }
        }
    }

    /**
     * 获取该区域的瓦片开始下标
     *
     * @param pixel 瓦片起始点
     * @return 瓦片开始下标
     */
    private Point getTileIndexForPixel(Point pixel) {
        int x = (int) Math.floor(pixel.x / (double) kRPScrollTileMapViewTileSize);
        int y = (int) Math.floor(pixel.y / (double) kRPScrollTileMapViewTileSize);
        return new Point(x, y);
    }

    /**
     * 获取瓦片数
     *
     * @param size 瓦片区域
     * @return 瓦片数
     */
    private Point getTileCountForSize(Point size) {
        int x = (int) Math.ceil(size.x / (double) kRPScrollTileMapViewTileSize);
        int y = (int) Math.ceil(size.y / (double) kRPScrollTileMapViewTileSize);
        return new Point(x, y);
    }

    /**
     * 根据瓦片下标查找瓦片
     *
     * @param index 下标
     * @return 瓦片
     */
    private MapTileView lookupOrCreateTileForIndex(Point index) {
        // 遍历现有瓦片集合
        for (MapTileView view : mTiles) {
            // 没有的话跳出本次循环
            if (view == null) {
                continue;
            }
            // 如果找到了则直接返回
            if (view.getIndex().equals(index)) {
                return view;
            }
        }
        // 这个index在现有瓦片集合中没有,则创建新的瓦片并放入本地数组中
        int left = index.x * kRPScrollTileMapViewTileSize;
        int top = index.y * kRPScrollTileMapViewTileSize;
        int right = left + kRPScrollTileMapViewTileSize;
        int bottom = top + kRPScrollTileMapViewTileSize;
        Rect tileArea = new Rect(left, top, right, bottom);
        // 创建新瓦片
        MapTileView tile = new MapTileView(this.getContext(), moveSdk, index, tileArea);
        mTiles.add(tile);
        // 将新的瓦片加到地图中
        addView(tile);
        // 布局坐标转逻辑坐标
        RectF frame = layoutRectForLogicalRect(tileArea);
        // 进行瓦片布局
        tile.layout(Math.round(frame.left), Math.round(frame.top), Math.round(frame.right),
                Math.round(frame.bottom));
        // 将新创建的瓦片返回
        return tile;
    }

    /**
     * 更新旋转值
     */
    public void applyRotation() {
        // 遍历现有瓦片
        for (MapTileView tileView : mTiles) {
            tileView.setRotation(getRotation());
        }
    }

    /**
     * 行走至指定点
     *
     * @param rawPoint 指定点
     */
    public void moveTo(Point rawPoint) {
        // 转换为逻辑坐标
        PointF physicalLocation = physicalPixelRotatedForLayoutCoordinate(rawPoint);
        // 调用SDK方法行走至指定点
        moveSdk.get().goToLocation(new Location(physicalLocation.getX(),
                physicalLocation.getY(), 0));
    }

    /**
     * 地图变化之后更新Marker
     */
    public void refreshMarkerAfterTransition() {
        refreshMarker();
    }
}
