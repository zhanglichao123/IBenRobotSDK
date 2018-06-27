package com.samton.ibenrobotdemo.map;

import android.graphics.PointF;
import android.os.SystemClock;
import android.view.MotionEvent;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/06/20 14:22
 *   desc    : 手势识别
 *   version : 1.0
 * </pre>
 */
public class GestureDetector {
    private final static int MODE_NONE = 0;
    private final static int MODE_TAP = 1;
    private final static int MODE_PINCH_ROTATE = 2;
    private final static int MODE_MOVE = 3;
    private final static long TAP_TOUCH_TIME_LIMIT = 500L;
    private static int currTouchCount;
    private static long currTouchTime;
    private static long touchBeginTime;
    /**
     * 监听函数
     */
    private OnGestureListener mListener;
    private PointF currPrimaryPosition;
    private PointF prevPrimaryPosition;
    private PointF currSecondaryPosition;
    private PointF prevSecondaryPosition;
    private int primaryPointerIndex;
    private int secondaryPointerIndex;
    private int touchMode = MODE_NONE;
    private boolean multiFingers = false;

    /**
     * 构造函数
     *
     * @param listener 监听函数
     */
    public GestureDetector(OnGestureListener listener) {
        mListener = listener;
        currPrimaryPosition = new PointF(0f, 0f);
        prevPrimaryPosition = new PointF(0f, 0f);
        currSecondaryPosition = new PointF(0f, 0f);
        prevSecondaryPosition = new PointF(0f, 0f);
    }

    public void onTouchEvent(MotionEvent event) {
        touchMode = getTouchMode(MotionEvent.obtain(event));

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // 第一个手指按下
                multiFingers = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                // 更多手指按下
                multiFingers = true;
                break;
            case MotionEvent.ACTION_MOVE:
                // 手指移动
                if (touchMode == MODE_MOVE) {
                    // 移动地图
                    int distanceX = Math.round(currPrimaryPosition.x - prevPrimaryPosition.x);
                    int distanceY = Math.round(currPrimaryPosition.y - prevPrimaryPosition.y);
                    mListener.onMapMove(distanceX, distanceY);
                } else if (touchMode == MODE_PINCH_ROTATE) {
                    // 缩放地图
                    float prevDistance = getDistance(prevPrimaryPosition, prevSecondaryPosition);
                    float currDistance = getDistance(currPrimaryPosition, currSecondaryPosition);
                    float scale = currDistance / prevDistance;
                    mListener.onMapPinch(scale);

                    // 旋转地图
                    PointF na = getNormalized(currPrimaryPosition, currSecondaryPosition);
                    PointF nb = getNormalized(prevPrimaryPosition, prevSecondaryPosition);
                    float rotate = (float) (Math.atan2(na.y, na.x) - Math.atan2(nb.y, nb.x));
                    mListener.onMapRotate(rotate);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                // 非第一根放下的手指抬起
                break;
            case MotionEvent.ACTION_UP:
                // 第一根放下的手指抬起
                if (touchMode == MODE_TAP) {
                    mListener.onMapTap(event);
                }
                multiFingers = false;
                clear();
                break;
        }
    }

    private int getTouchMode(MotionEvent event) {
        final int prevTouchCount = currTouchCount;
        currTouchCount = event.getPointerCount();

        currTouchTime = SystemClock.uptimeMillis();

        // 释放所有触控点
        if (currTouchCount == 0) {
            return MODE_NONE;
        }

        // 只有一个触控点
        if (currTouchCount == 1) {
            // 首次触碰
            if (prevTouchCount == 0) {
                primaryPointerIndex = event.findPointerIndex(event.getPointerId(0));

                touchBeginTime = currTouchTime;
            }

            prevPrimaryPosition.set(currPrimaryPosition.x, currPrimaryPosition.y);
            currPrimaryPosition.set(event.getX(primaryPointerIndex), event.getY(primaryPointerIndex));

            if (prevTouchCount == 0) {
                prevPrimaryPosition.set(currPrimaryPosition.x, currPrimaryPosition.y);
            }

            if (touchMode == MODE_MOVE) {
                return MODE_MOVE;
            }

            float distance = getDistance(currPrimaryPosition, prevPrimaryPosition);

            // 已经触碰过
            if (prevTouchCount == 1 && Math.abs(distance) <= 10f & !multiFingers) {
                return currTouchTime - touchBeginTime <= TAP_TOUCH_TIME_LIMIT ? MODE_TAP : MODE_NONE;
            } else if (prevTouchCount == 1) {
                return MODE_MOVE;
            } else {
                return MODE_NONE;
            }
        } else if (currTouchCount == 2) {
            if (prevTouchCount == 1) {
                secondaryPointerIndex = event.findPointerIndex(event.getPointerId(1));
            }

            prevPrimaryPosition.set(currPrimaryPosition.x, currPrimaryPosition.y);
            prevSecondaryPosition.set(currSecondaryPosition.x, currSecondaryPosition.y);

            currPrimaryPosition.set(event.getX(primaryPointerIndex), event.getY(primaryPointerIndex));
            currSecondaryPosition.set(event.getX(secondaryPointerIndex), event.getY(secondaryPointerIndex));

            if (prevTouchCount == 1) {
                prevSecondaryPosition.set(currSecondaryPosition.x, currSecondaryPosition.y);
            }

            return MODE_PINCH_ROTATE;
        }

        return MODE_NONE;
    }

    private float getDistance(PointF p1, PointF p2) {
        return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private PointF getNormalized(PointF v1, PointF v2) {
        float length = getDistance(v1, v2);
        if (length == 0) {
            return new PointF();
        } else {
            return new PointF((v1.x - v2.x) / length, (v1.y - v2.y) / length);
        }
    }

    private void clear() {
        touchMode = MODE_NONE;
        currPrimaryPosition.set(0f, 0f);
        prevPrimaryPosition.set(0f, 0f);
        currSecondaryPosition.set(0f, 0f);
        prevSecondaryPosition.set(0f, 0f);
        primaryPointerIndex = 0;
        secondaryPointerIndex = 0;
        currTouchCount = 0;
        currTouchTime = 0L;
        touchBeginTime = 0L;
    }

    /**
     * 手势监听回调
     */
    public interface OnGestureListener {
        /**
         * 地图点击
         *
         * @param event 点击事件
         */
        void onMapTap(MotionEvent event);

        /**
         * 地图缩放
         *
         * @param factor 缩放值
         */
        void onMapPinch(float factor);

        /**
         * 地图移动
         *
         * @param distanceX X轴距离
         * @param distanceY Y轴距离
         */
        void onMapMove(int distanceX, int distanceY);

        /**
         * 地图旋转
         *
         * @param factor 旋转值
         */
        void onMapRotate(float factor);
    }
}
