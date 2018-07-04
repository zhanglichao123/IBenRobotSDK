package com.samton.ibenrobotdemo.widgets;

import android.content.Context;
import android.graphics.Paint;
import android.widget.ImageView;

import com.samton.ibenrobotdemo.map.RobotAgent;
import com.slamtec.slamware.geometry.PointF;

import java.lang.ref.WeakReference;
import java.util.Vector;

/**
 * <pre>
 *   @author  : syk
 *   e-mail  : shenyukun1024@gmail.com
 *   time    : 2018/07/03 18:01
 *   desc    : 地图标注自定义View
 *   version : 1.0
 * </pre>
 */
public class MarkerView extends SlamBaseView {
    /**
     * 点位集合
     */
    private Vector<ImageView> mMarkerImages;
    /**
     *
     */
    private Paint paint;
    private PointF coordinate;

    /**
     * 构造函数
     *
     * @param context 上下文对象
     */
    public MarkerView(Context context) {
        super(context);
    }

    /**
     * 构造函数
     *
     * @param context 上下文
     * @param sdk     sdk对象
     */
    public MarkerView(Context context, WeakReference<RobotAgent> sdk) {
        super(context, sdk);
        // 初始化点位集合
        mMarkerImages = new Vector<>();
    }
}
