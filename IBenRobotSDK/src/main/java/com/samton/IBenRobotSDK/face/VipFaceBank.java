package com.samton.IBenRobotSDK.face;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储VIP人脸信息
 */
public class VipFaceBank {
    private static List<FaceVipListBean> vips;

    /**
     * 新增vip
     */
    public static void addVipFace(FaceVipListBean bean) {
        if (vips == null) {
            vips = new ArrayList<>();
        }
        vips.add(bean);
    }

    /**
     * 新增vip
     */
    public static void addVipFace(List<FaceVipListBean> beans) {
        if (vips == null) {
            vips = new ArrayList<>();
        }
        vips.addAll(beans);
    }

    /**
     * 移除vip
     */
    public static void removeVipFace(int index) {
        if (vips != null) {
            vips.remove(index);
        }
    }

    public static void removeVipFace(FaceVipListBean bean) {
        if (vips != null) {
            vips.remove(bean);
        }
    }

    /**
     * 获取vip列表
     */
    public static List<FaceVipListBean> getVipFaces() {
        if (vips == null) {
            vips = new ArrayList<>();
        }
        return vips;
    }
}
