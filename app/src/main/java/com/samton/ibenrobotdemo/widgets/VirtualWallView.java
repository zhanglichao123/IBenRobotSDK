package com.samton.ibenrobotdemo.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.widget.ImageView;

import com.samton.ibenrobotdemo.R;
import com.samton.ibenrobotdemo.map.RobotAgent;
import com.slamtec.slamware.geometry.Line;
import com.slamtec.slamware.geometry.PointF;

import java.lang.ref.WeakReference;
import java.util.Vector;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/08 14:23
 *   desc    : 思岚虚拟墙自定义View
 *   version : 1.0
 * </pre>
 */
public class VirtualWallView extends SlamBaseView {
    private final static int TouchDeviation = 5;
    private final static int Slope = 5;
    private static int indicatorOffsetX = 0;
    private static int indicatorOffsetY = 0;
    private ImageView wallIndicator_;
    private Vector<Line> walls_;
    private Point startPoint_;
    private Point endPoint_;
    private boolean isSetStart_;
    private Paint paint = new Paint();

    public VirtualWallView(Context context, WeakReference<RobotAgent> sdk) {
        super(context, sdk);
        // 初始化数据
        walls_ = moveSdk.get().getWalls();
        if (walls_ == null) {
            walls_ = new Vector<>();
        }
        isSetStart_ = false;

        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(android.R.color.holo_orange_light));
        paint.setStrokeWidth(4);
        // 允许ViewGroup回调onDraw方法
        setWillNotDraw(false);
    }

    public void updateWalls(Vector<Line> walls) {
        walls_ = walls;
        invalidate();
    }

    public void setWallIndicator(Point point) {
        if (!isSetStart_) {
            isSetStart_ = true;
            startPoint_ = point;
            refreshIndicator();
        } else {
            endPoint_ = point;
            addWall();
        }
        invalidate();
    }

    private void refreshIndicator() {
        if (isSetStart_) {
            if (wallIndicator_ == null) {
                wallIndicator_ = new ImageView(getContext());
                Bitmap indicator = BitmapFactory.decodeResource(getResources(), R.mipmap.virtual_wall_point);
                wallIndicator_.setImageBitmap(indicator);
                indicatorOffsetX = indicator.getWidth() / 2;
                indicatorOffsetY = indicator.getHeight() / 2;
                addView(wallIndicator_);
            }
            wallIndicator_.layout(startPoint_.x - indicatorOffsetX, startPoint_.y - indicatorOffsetX,
                    startPoint_.x + indicatorOffsetX, startPoint_.y + indicatorOffsetY);
            wallIndicator_.setVisibility(VISIBLE);
        }
    }

    private void addWall() {
        PointF startP = physicalPixelRotatedForLayoutCoordinate(startPoint_);
        PointF endP = physicalPixelRotatedForLayoutCoordinate(endPoint_);

        PointF startPoint = new PointF(startP.getX(), startP.getY());
        PointF endPoint = new PointF(endP.getX(), endP.getY());
        Line line = new Line(startPoint, endPoint);
        moveSdk.get().addWall(line);

        clearIndicators();
    }

    public void clearIndicators() {
        isSetStart_ = false;
        if (wallIndicator_ != null) {
            wallIndicator_.setVisibility(INVISIBLE);
        }
    }

    public void removeWall(Point point) {
        Point offset = getLayoutOffset();

        int wallId = -1;
        float bestFactor = Float.MAX_VALUE;

        for (Line wall : walls_) {
            PointF startPoint = wall.getStartPoint();
            PointF endPoint = wall.getEndPoint();

            Point startP = layoutRotatedCoordinateForPhysicalCoordinate(startPoint, offset);
            Point endP = layoutRotatedCoordinateForPhysicalCoordinate(endPoint, offset);

            float minX = Math.min(startP.x, endP.x) - TouchDeviation;
            float maxX = Math.max(startP.x, endP.x) + TouchDeviation;

            float minY = Math.min(startP.y, endP.y) - TouchDeviation;
            float maxY = Math.max(startP.y, endP.y) + TouchDeviation;

            if (point.x < minX || point.x > maxX || point.y < minY || point.y > maxY) {
                continue;
            }

            float factor1;
            float factor2;

            if ((endP.y - startP.y) > (endP.x - startP.x) * 10) {
                factor1 = (point.y == startP.y) ? 0 : (point.x - startP.x) / (point.y - startP.y);
                factor2 = (endP.y == startP.y) ? 0 : (endP.x - startP.x) / (endP.y - startP.y);
            } else {
                factor1 = (point.x == startP.x) ? 0 : (point.y - startP.y) / (point.x - startP.x);
                factor2 = (endP.x == startP.x) ? 0 : (endP.y - startP.y) / (endP.x - startP.x);
            }

            float factor = Math.abs(factor1 - factor2);

            if (factor < Slope) {
                if (wallId == -1) {
                    bestFactor = factor;
                    wallId = wall.getSegmentId();
                } else if (factor < bestFactor) {
                    bestFactor = factor;
                    wallId = wall.getSegmentId();
                }
            }
        }

        if (wallId != -1) {
            moveSdk.get().removeWallById(wallId);
        }
    }

    public void refreshIndicatorAfterZoomAndRotate() {
        if (wallIndicator_ == null) {
            return;
        }
        int isVisible = wallIndicator_.getVisibility();
        Point center = layoutRotatedCoordinateForPhysicalCoordinate(new PointF(wallIndicator_.getX(), wallIndicator_.getY()));
        wallIndicator_.layout(center.x - indicatorOffsetX, center.y - indicatorOffsetY,
                center.x + indicatorOffsetX, center.y + indicatorOffsetY);
        wallIndicator_.setVisibility(isVisible);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (walls_ != null && walls_.size() > 0) {
            Point offset = getLayoutOffset();

            for (Line wall : walls_) {
                PointF startPoint = wall.getStartPoint();
                PointF endPoint = wall.getEndPoint();

                Point startP = layoutRotatedCoordinateForPhysicalCoordinate(startPoint, offset);
                Point endP = layoutRotatedCoordinateForPhysicalCoordinate(endPoint, offset);

                canvas.drawLine(startP.x, startP.y, endP.x, endP.y, paint);
            }
        }
    }
}
