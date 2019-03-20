package com.samton.IBenRobotSDK.face;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储VIP人脸信息
 */
public class VipFaceBank {
    private static List<VipFaceBean> vips;

    /**
     * 新增vip
     */
    public static void addVipFace(VipFaceBean bean) {
        if (vips == null) {
            vips = new ArrayList<>();
        }
        vips.add(bean);
    }

    /**
     * 移除vip
     */
    public static void removeVipFace(int index) {
        if (vips != null) {
            vips.remove(index);
        }
    }

    public static void removeVipFace(VipFaceBean bean) {
        if (vips != null) {
            vips.remove(bean);
        }
    }

    /**
     * 获取vip列表
     */
    public static List<VipFaceBean> getVipFaces() {
        if (vips == null) {
            vips = new ArrayList<>();
        }
        return vips;
    }
}
