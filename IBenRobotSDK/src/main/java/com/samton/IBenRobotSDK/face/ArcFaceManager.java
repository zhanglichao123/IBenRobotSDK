package com.samton.IBenRobotSDK.face;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.arcsoft.face.AgeInfo;
import com.arcsoft.face.ErrorInfo;
import com.arcsoft.face.Face3DAngle;
import com.arcsoft.face.FaceEngine;
import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.FaceInfo;
import com.arcsoft.face.FaceSimilar;
import com.arcsoft.face.GenderInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.samton.IBenRobotSDK.utils.ImageUtils;
import com.samton.IBenRobotSDK.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author: xkbai
 * e-mail: xkkbai@163.com
 * time: 2019/3/15
 * desc: 虹软人脸识别、检测、检索工具类
 */
public class ArcFaceManager {
    private String TAG = "IBenFaceUtil";
    private static ArcFaceManager instance;
    private Context context;
    private FaceEngine faceEngine;
    // sdk回调结果
    private int active, initCode;

    public ArcFaceManager(Context context) {
        this.context = context;
    }

    public static ArcFaceManager getInstance(Context context) {
        if (instance == null) {
            instance = new ArcFaceManager(context);
        }
        return instance;
    }

    /**
     * sdk是否初始化成功
     */
    public boolean isRegister() {
        return (active == ErrorInfo.MERR_ASF_ALREADY_ACTIVATED
                || active == ErrorInfo.MOK) && initCode == ErrorInfo.MOK;
    }

    /**
     * 初始化ArcSoftFace
     */
    public void initArcSoftFace(String appId, String appKey) {
        if (faceEngine == null) {
            faceEngine = new FaceEngine();
        }
        active = faceEngine.active(context, appId, appKey);
        initCode = faceEngine.init(context, FaceEngine.ASF_DETECT_MODE_IMAGE,// 检测模式
                FaceEngine.ASF_OP_0_HIGHER_EXT,// 检测角度
                32,// 人脸相对于所在图片的长边的占比
                10,// 引擎最多能检测出的人脸数
                FaceEngine.ASF_FACE_RECOGNITION | FaceEngine.ASF_FACE_DETECT | FaceEngine.ASF_AGE |
                        FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE | FaceEngine.ASF_LIVENESS);
        LogUtils.e("虹软sdk初始化结果:" + "active-" + active + ",init-" + initCode);
    }

    /**
     * 人脸检测
     *
     * @param data
     * @param width
     * @param height
     * @return 人脸列表
     */
    public List<ArcFaceInfo> detectFaces(byte[] data, int width, int height) {
        // 原始人脸数据
        List<FaceInfo> faceInfos = new ArrayList<>();
        // 返回人脸数据
        List<ArcFaceInfo> arcFaceInfos = new ArrayList<>();
        faceEngine.detectFaces(data, width, height, FaceEngine.CP_PAF_BGR24, faceInfos);
        faceEngine.process(data, width, height, FaceEngine.CP_PAF_BGR24, faceInfos,
                FaceEngine.ASF_AGE | FaceEngine.ASF_GENDER | FaceEngine.ASF_FACE3DANGLE);
        // 人脸年龄信息
        List<AgeInfo> ageInfos = new ArrayList<>();
        faceEngine.getAge(ageInfos);
        // 人脸姓别信息
        List<GenderInfo> genderInfos = new ArrayList<>();
        faceEngine.getGender(genderInfos);
        // 人脸3D角度信息
        List<Face3DAngle> face3DAngles = new ArrayList<>();
        faceEngine.getFace3DAngle(face3DAngles);
        // 初始化要返回的数据
        int faceInfosSize = faceInfos.size();
        if (faceInfosSize != ageInfos.size() || faceInfosSize != genderInfos.size()
                || faceInfosSize != face3DAngles.size()) {
            LogUtils.e("数据不对称，无法返回人脸数据");
        } else {
            for (int i = 0; i < faceInfosSize; i++) {
                ArcFaceInfo arcFaceInfo = new ArcFaceInfo();
                // 序号
                arcFaceInfo.setIndex(i);
                // 年龄
                arcFaceInfo.setAge(ageInfos.get(i).getAge());
                // 姓别
                arcFaceInfo.setGender(genderInfos.get(i).getGender());
                // 位置
                arcFaceInfo.setRect(faceInfos.get(i).getRect());
                // 角度
                Face3DAngle face3DAngle = face3DAngles.get(i);
                arcFaceInfo.setPitch(face3DAngle.getPitch());
                arcFaceInfo.setRoll(face3DAngle.getRoll());
                arcFaceInfo.setYaw(face3DAngle.getYaw());
                arcFaceInfo.setStatus(face3DAngle.getStatus());
                arcFaceInfos.add(arcFaceInfo);
            }
        }
        return arcFaceInfos;
    }

    /**
     * 获取人脸特征信息
     *
     * @param data
     * @param width
     * @param height
     * @param faceInfo
     * @return 人脸特征信息
     */
    public FaceFeature extractFaceFeature(byte[] data, int width, int height, FaceInfo faceInfo) {
        FaceFeature faceFeature = new FaceFeature();
        faceEngine.extractFaceFeature(data, width, height, FaceEngine.CP_PAF_BGR24, faceInfo, faceFeature);
        return faceFeature;
    }

    /**
     * 对比人脸相似度
     *
     * @param feature1
     * @param feature2
     * @return 相似度信息
     */
    public FaceSimilar compareFaceFeature(FaceFeature feature1, FaceFeature feature2) {
        FaceSimilar faceSimilar = new FaceSimilar();
        faceEngine.compareFaceFeature(feature1, feature2, faceSimilar);
        return faceSimilar;
    }

    /**
     * 人脸检索
     *
     * @param infos
     * @param bitmap
     * @return
     */
    public List<ArcFaceInfo> searchFaceFeature(List<ArcFaceInfo> infos, byte[] bytes, Bitmap bitmap) {
        // 返回VIP数据列表
        List<ArcFaceInfo> returnArcFaceInfos = new ArrayList<>();
        // VIP人脸库
        List<VipFaceBean> localVipFaceBeans = VipFaceBank.getVipFaces();
        if (localVipFaceBeans == null || localVipFaceBeans.size() <= 0
                || infos == null || infos.size() <= 0) {
            LogUtils.e("人脸库为空或者传入人脸数据为空");
            return returnArcFaceInfos;
        }
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        for (ArcFaceInfo info : infos) {
            FaceFeature feature = extractFaceFeature(bytes, width, height, info);
            for (VipFaceBean bean : localVipFaceBeans) {
                FaceSimilar faceSimilar = compareFaceFeature(feature, bean.getFaceFeature());
                if (faceSimilar.getScore() >= 0.8) {
                    info.setVipFaceBean(bean);
                    returnArcFaceInfos.add(info);
                }
                LogUtils.e("检索相似度：" + faceSimilar.getScore());
            }
        }
        return returnArcFaceInfos;
    }

    /**
     * 注册到本地人脸库
     *
     * @param faceBeans
     */
    public void registerFaceData(final List<VipFaceBean> faceBeans) {
        Log.e(TAG, "初始数据：" + faceBeans.get(0).toString());
        RequestManager with = Glide.with(context);
        for (final VipFaceBean bean : faceBeans) {
//            Bitmap resource = bean.getBitmap();
//            if (resource != null) {
//                int width = resource.getWidth(), height = resource.getHeight();
//                byte[] bytes = ImageUtil.bitmapToNv21(resource, width, height);
//                List<FaceInfo> faceInfos = detectFaces(bytes, width, height);
//                if (faceInfos.size() != 1) {
//                    // 多张人脸或没有人脸
//                    faceBeans.remove(bean);
//                    Log.e(TAG, "没有人脸:" + faceInfos.size());
//                    continue;
//                } else {
//                    bean.setFaceFeature(extractFaceFeature(bytes, width, height, faceInfos.get(0)));
//                    Log.e(TAG, "人脸注册成功");
//                }
//            }
            // 依次获取bitmap
            try {
                Bitmap bitmap = Glide.with(context).load(bean.getFaceUrl()).asBitmap()
                        .centerCrop().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                int width = bitmap.getWidth(), height = bitmap.getHeight();
                byte[] bytes = ImageUtils.bitmapToBgr(bitmap);
                List<ArcFaceInfo> infos = detectFaces(bytes, width, height);
                if (infos.size() != 1) {
                    // 多张人脸或没有人脸
                    faceBeans.remove(bean);
                } else {
                    // 只有一张人脸
                    bean.setFaceFeature(extractFaceFeature(bytes, width, height, infos.get(0)));
                    // 存储进入VIP库
                    VipFaceBank.addVipFace(bean);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
//            with.load(bean.getFaceUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> animation) {
//                    int width = bitmap.getWidth(), height = bitmap.getHeight();
//                    byte[] bytes = ImageUtils.bitmapToBgr(bitmap);
//                    List<ArcFaceInfo> infos = detectFaces(bytes, width, height);
//                    if (infos.size() != 1) {
//                        // 多张人脸或没有人脸
//                        faceBeans.remove(bean);
//                    } else {
//                        // 只有一张人脸
//                        bean.setFaceFeature(extractFaceFeature(bytes, width, height, infos.get(0)));
//                        // 存储进入VIP库
//                        VipFaceBank.addVipFace(bean);
//                    }
//                }
//            });
        }
    }
}
