package com.samton.ibenrobotdemo.widgets;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.ViewGroup;

import com.samton.ibenrobotdemo.map.RobotAgent;
import com.slamtec.slamware.geometry.PointF;

import java.lang.ref.WeakReference;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/08 13:57
 *   desc    : 思岚基类View
 *   version : 1.0
 * </pre>
 */
public class SlamBaseView extends ViewGroup {

    protected WeakReference<RobotAgent> moveSdk;
    private PointF centerLocation;
    private float mapScale;
    private Point transition;
    private float rotation;

    public SlamBaseView(Context context) {
        super(context);
    }

    public SlamBaseView(Context context, WeakReference<RobotAgent> sdk) {
        super(context);
        // 控制底盘移动SDK
        moveSdk = sdk;
        // 中心点
        centerLocation = new PointF(0f, 0f);
        // 地图缩放比例
        mapScale = 1.0f;
        transition = new Point(0, 0);
        rotation = 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    protected Point layoutCoordinateForLogicalPixel(Point pixel) {
        return layoutCoordinateForLogicalPixel(pixel, this.getLayoutOffset());
    }

    protected Point layoutCoordinateForLogicalPixel(Point pixel, Point offset) {
        float x = pixel.x * mapScale + offset.x;
        float y = pixel.y * mapScale + offset.y;
        return new Point(Math.round(x), Math.round(y));
    }

    protected Point getLayoutOffset() {
        int width = getWidth();
        int height = getHeight();

        float offsetX = width / 2 - centerLocation.getX() * mapScale + transition.x;
        float offsetY = height / 2 - centerLocation.getY() * mapScale + transition.y;

        return new Point(Math.round(offsetX), Math.round(offsetY));
    }

    protected Point layoutRotatedCoordinateForLogicalPixel(Point pixel) {
        return layoutRotatedCoordinateForLogicalPixel(pixel, this.getLayoutOffset());
    }

    protected Point layoutRotatedCoordinateForLogicalPixel(Point pixel, Point offset) {
        float x = pixel.x * mapScale + offset.x;
        float y = pixel.y * mapScale + offset.y;
        return getRotatedCoordinateForLogicalPixel(new Point(Math.round(x), Math.round(y)));
    }

    private Point getRotatedCoordinateForLogicalPixel(Point origin) {
        int centerX = Math.round(getWidth() / 2);
        int centerY = Math.round(getHeight() / 2);
        Point center = new Point(centerX + transition.x, centerY + transition.y);
        return getRotatedCoordinateForLogicalPixel(origin, center);
    }

    private Point getRotatedCoordinateForLogicalPixel(Point origin, Point center) {
        double radius = Math.sqrt(Math.pow(center.x - origin.x, 2) + Math.pow(center.y - origin.y, 2));
        double theta = Math.acos((origin.x - center.x) / radius);

        if (Double.isNaN(theta)) {
            return origin;
        }

        double x, y;
        if (origin.y < center.y) {
            x = radius * Math.cos(theta - rotation);
            y = radius * Math.sin(theta - rotation);
        } else {
            x = radius * Math.cos(theta + rotation);
            y = radius * Math.sin(theta + rotation);
        }

        if (origin.y < center.y) {
            x = center.x + x;
            y = center.y - y;
        } else {
            x = center.x + x;
            y = center.y + y;
        }

        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    protected Point layoutCoordinateForPhysicalCoordinate(PointF physicalCoordinate) {
        return layoutCoordinateForPhysicalCoordinate(physicalCoordinate, this.getLayoutOffset());
    }

    protected Point layoutCoordinateForPhysicalCoordinate(PointF physicalCoordinate, Point offset) {
        Point pixel = moveSdk.get().getMapData().coordinateToPixel(physicalCoordinate.getX(), physicalCoordinate.getY());
        return layoutCoordinateForLogicalPixel(pixel, offset);
    }

    protected Point layoutRotatedCoordinateForLogicalPixel(Point pixel, Point offset, Point center) {
        float x = pixel.x * mapScale + offset.x;
        float y = pixel.y * mapScale + offset.y;
        return getRotatedCoordinateForLogicalPixel(new Point(Math.round(x), Math.round(y)), center);
    }

    protected Point layoutRotatedCoordinateForPhysicalCoordinate(PointF physicalCoordinate) {
        return layoutCoordinateForPhysicalCoordinate(physicalCoordinate, this.getLayoutOffset());
    }

    protected Point layoutRotatedCoordinateForPhysicalCoordinate(PointF physicalCoordinate, Point offset) {
        Point pixel = moveSdk.get().getMapData().coordinateToPixel(physicalCoordinate.getX(), physicalCoordinate.getY());
        return layoutRotatedCoordinateForLogicalPixel(pixel, offset);
    }

    protected RectF layoutRectForLogicalRect(Rect logicalRect) {
        return layoutRectForLogicalRect(logicalRect, this.getLayoutOffset());
    }

    protected RectF layoutRectForLogicalRect(Rect logicalRect, Point offset) {
        Point origin = layoutCoordinateForLogicalPixel(new Point(logicalRect.left, logicalRect.top), offset);
        float width = (float) Math.ceil(Math.abs(logicalRect.width()) * mapScale);
        float height = (float) Math.ceil(Math.abs(logicalRect.height()) * mapScale);
        return new RectF(origin.x, origin.y, origin.x + width, origin.y + height);
    }

    protected Point logicalPixelForLayoutCoordinate(Point layoutCoordinate) {
        return logicalPixelForLayoutCoordinate(layoutCoordinate, this.getLayoutOffset());
    }

    protected Point logicalPixelForLayoutCoordinate(Point layoutCoordinate, Point offset) {
        int x = Math.round((layoutCoordinate.x - offset.x) / mapScale);
        int y = Math.round((layoutCoordinate.y - offset.y) / mapScale);
        return new Point(x, y);
    }

    protected Point logicalPixelRotatedForLayoutCoordinate(Point layoutCoordinate) {
        return logicalPixelRotatedForLayoutCoordinate(layoutCoordinate, this.getLayoutOffset());
    }

    protected Point logicalPixelRotatedForLayoutCoordinate(Point layoutCoordinate, Point offset) {
        Point rotatedCoordinate = getPhysicalPixelForRotatedLayoutCoordinate(new Point(layoutCoordinate.x, layoutCoordinate.y));
        int x = Math.round((rotatedCoordinate.x - offset.x) / mapScale);
        int y = Math.round((rotatedCoordinate.y - offset.y) / mapScale);
        return new Point(x, y);
    }

    private Point getPhysicalPixelForRotatedLayoutCoordinate(Point origin) {
        int centerX = Math.round(getWidth() / 2);
        int centerY = Math.round(getHeight() / 2);
        Point center = new Point(centerX + transition.x, centerY + transition.y);
        double radius = Math.sqrt(Math.pow(center.x - origin.x, 2) + Math.pow(center.y - origin.y, 2));
        double theta = Math.acos((origin.x - center.x) / radius);

        if (Double.isNaN(theta)) {
            return origin;
        }

        double x, y;
        if (origin.y < center.y) {
            x = radius * Math.cos(theta + rotation);
            y = radius * Math.sin(theta + rotation);
        } else {
            x = radius * Math.cos(theta - rotation);
            y = radius * Math.sin(theta - rotation);
        }

        if (origin.y < center.y) {
            x = center.x + x;
            y = center.y - y;
        } else {
            x = center.x + x;
            y = center.y + y;
        }

        return new Point((int) Math.round(x), (int) Math.round(y));
    }

    protected PointF physicalPixelForLayoutCoordinate(Point layoutCoordinate) {
        return physicalPixelForLayoutCoordinate(layoutCoordinate, this.getLayoutOffset());
    }

    protected PointF physicalPixelForLayoutCoordinate(Point layoutCoordinate, Point offset) {
        Point logicalPixel = logicalPixelForLayoutCoordinate(layoutCoordinate, offset);
        return moveSdk.get().getMapData().pixelToCoordinate(logicalPixel);
    }

    protected PointF physicalPixelRotatedForLayoutCoordinate(Point layoutCoordinate) {
        return physicalPixelRotatedForLayoutCoordinate(layoutCoordinate, this.getLayoutOffset());
    }

    protected PointF physicalPixelRotatedForLayoutCoordinate(Point layoutCoordinate, Point offset) {
        Point logicalPixel = logicalPixelRotatedForLayoutCoordinate(layoutCoordinate, offset);
        return moveSdk.get().getMapData().pixelToCoordinate(logicalPixel);
    }

    public PointF getCenterLocation() {
        return centerLocation;
    }

    public void setCenterLocation(PointF centerLocation) {
        if (this.centerLocation == centerLocation) {
            return;
        }
        this.centerLocation = centerLocation;
        invalidate();
    }

    public float getMapScale() {
        return mapScale;
    }

    public void setMapScale(float mapScale) {
        float diff = mapScale - this.mapScale;

        if (diff < 0.0001f && diff > -0.0001f) {
            return;
        }

        if (mapScale <= 0) {
            return;
        }

        this.mapScale = mapScale;
        invalidate();
    }

    public Point getTransition() {
        return transition;
    }

    public void setTransition(Point transition) {
        this.transition = new Point(this.transition.x + transition.x, this.transition.y + transition.y);
        invalidate();
    }

    public float getRotation() {
        return (float) (rotation * 180 / Math.PI);
    }

    public void setRotation(float rotation) {
        this.rotation += rotation;
        invalidate();
    }
}
