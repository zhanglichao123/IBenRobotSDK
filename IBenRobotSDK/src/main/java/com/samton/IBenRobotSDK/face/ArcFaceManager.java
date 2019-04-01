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
import com.bumptech.glide.request.target.Target;
import com.samton.IBenRobotSDK.utils.ImageUtils;
import com.samton.IBenRobotSDK.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
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
        LogUtils.d("虹软sdk初始化结果:" + "active-" + active + ",init-" + initCode);
    }

    /**
     * 人脸检测(BGR24)
     *
     * @return 人脸列表
     */
    public List<ArcFaceInfo> detectFacesBGR24(byte[] data, int width, int height) {
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
        if (faceInfosSize <= 0 || faceInfosSize != ageInfos.size()
                || faceInfosSize != genderInfos.size() || faceInfosSize != face3DAngles.size()) {
            LogUtils.d("数据不对称，无法返回人脸数据");
        } else {
            for (int i = 0; i < faceInfosSize; i++) {
                ArcFaceInfo arcFaceInfo = new ArcFaceInfo();
                // 序号
                arcFaceInfo.setIndex(i);
                // 年龄
                arcFaceInfo.setAge(ageInfos.get(i).getAge());
                // 姓别
                arcFaceInfo.setGender(genderInfos.get(i).getGender());
                // 位置、方向
                FaceInfo faceInfo = faceInfos.get(i);
                arcFaceInfo.setRect(faceInfo.getRect());
                arcFaceInfo.setOrient(faceInfo.getOrient());
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
     * 人脸检测(NV21)
     *
     * @return 人脸列表
     */
    public List<ArcFaceInfo> detectFacesNV21(byte[] data, int width, int height) {
        // 原始人脸数据
        List<FaceInfo> faceInfos = new ArrayList<>();
        // 返回人脸数据
        List<ArcFaceInfo> arcFaceInfos = new ArrayList<>();
        faceEngine.detectFaces(data, width, height, FaceEngine.CP_PAF_NV21, faceInfos);
        faceEngine.process(data, width, height, FaceEngine.CP_PAF_NV21, faceInfos,
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
        if (faceInfosSize <= 0 || faceInfosSize != ageInfos.size()
                || faceInfosSize != genderInfos.size() || faceInfosSize != face3DAngles.size()) {
            LogUtils.d("数据不对称，无法返回人脸数据");
        } else {
            for (int i = 0; i < faceInfosSize; i++) {
                ArcFaceInfo arcFaceInfo = new ArcFaceInfo();
                // 序号
                arcFaceInfo.setIndex(i);
                // 年龄
                arcFaceInfo.setAge(ageInfos.get(i).getAge());
                // 姓别
                arcFaceInfo.setGender(genderInfos.get(i).getGender());
                // 位置、方向
                FaceInfo faceInfo = faceInfos.get(i);
                arcFaceInfo.setRect(faceInfo.getRect());
                arcFaceInfo.setOrient(faceInfo.getOrient());
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
     * @return 人脸特征信息
     */
    public FaceFeature extractFaceFeatureBGR24(byte[] data, int width, int height, FaceInfo faceInfo) {
        // FaceInfo faceInfo = new FaceInfo(arcFaceInfo.getRect(), arcFaceInfo.getOrient());
        FaceFeature faceFeature = new FaceFeature();
        faceEngine.extractFaceFeature(data, width, height, FaceEngine.CP_PAF_BGR24, faceInfo, faceFeature);
        return faceFeature.clone();
    }

    /**
     * 获取人脸特征信息
     *
     * @return 人脸特征信息
     */
    public FaceFeature extractFaceFeatureNV21(byte[] data, int width, int height, FaceInfo faceInfo) {
        // FaceInfo faceInfo = new FaceInfo(arcFaceInfo.getRect(), arcFaceInfo.getOrient());
        FaceFeature faceFeature = new FaceFeature();
        faceEngine.extractFaceFeature(data, width, height, FaceEngine.CP_PAF_NV21, faceInfo, faceFeature);
        return faceFeature.clone();
    }

    /**
     * 对比人脸相似度
     *
     * @return 相似度信息
     */
    public FaceSimilar compareFaceFeature(FaceFeature feature1, FaceFeature feature2) {
        FaceSimilar faceSimilar = new FaceSimilar();
        faceEngine.compareFaceFeature(feature1, feature2, faceSimilar);
        return faceSimilar;
    }

    /**
     * 人脸检索(BGR24)
     *
     * @return
     */
    public List<ArcFaceInfo> searchFaceFeatureBGR24(List<ArcFaceInfo> infos, byte[] bytes, Bitmap bitmap) {
        // 返回VIP数据列表
        List<ArcFaceInfo> returnArcFaceInfos = new ArrayList<>();
        // VIP人脸库
        List<FaceVipListBean> localFaceVipListBeans = VipFaceBank.getVipFaces();
        if (localFaceVipListBeans == null || localFaceVipListBeans.size() <= 0
                || infos == null || infos.size() <= 0) {
            LogUtils.d("人脸库为空或者传入人脸数据为空:" + localFaceVipListBeans.size());
            return returnArcFaceInfos;
        }
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        for (ArcFaceInfo info : infos) {
            FaceFeature feature = extractFaceFeatureBGR24(bytes, width, height, info);
            // 签到时需要判断使用哪一组VIP库(主动交互判断所有VIP)
            // 取出所有VIP信息，放入同一个列表
            List<FaceVipListBean.FaceinfoListBean> localVipInfoLists = new ArrayList<>();
            for (FaceVipListBean localFaceVipListBean : localFaceVipListBeans) {
                List<FaceVipListBean.FaceinfoListBean> faceInfoList = localFaceVipListBean.getFaceinfoList();
                if (faceInfoList != null && faceInfoList.size() > 0) {
                    localVipInfoLists.addAll(faceInfoList);
                }
            }
            // 开始遍历是否有VIP存在
            for (FaceVipListBean.FaceinfoListBean bean : localVipInfoLists) {
                FaceSimilar faceSimilar = compareFaceFeature(feature, bean.getFaceFeature());
                if (faceSimilar.getScore() >= 0.8) {
                    info.setVipInfoBean(bean);
                    returnArcFaceInfos.add(info);
                }
                LogUtils.d("检索相似度：" + faceSimilar.getScore());
            }
        }
        return returnArcFaceInfos;
    }

    /**
     * 人脸检索(NV21)
     *
     * @return
     */
    public List<ArcFaceInfo> searchFaceFeatureNV21(List<ArcFaceInfo> infos, byte[] bytes, Bitmap bitmap) {
        // 返回VIP数据列表
        List<ArcFaceInfo> returnArcFaceInfos = new ArrayList<>();
        // VIP人脸库
        List<FaceVipListBean> localFaceVipListBeans = VipFaceBank.getVipFaces();
        if (localFaceVipListBeans == null || localFaceVipListBeans.size() <= 0
                || infos == null || infos.size() <= 0) {
            LogUtils.d("人脸库为空或者传入人脸数据为空:" + localFaceVipListBeans.size());
            return returnArcFaceInfos;
        }
        int width = bitmap.getWidth(), height = bitmap.getHeight();
        for (ArcFaceInfo info : infos) {
            FaceFeature feature = extractFaceFeatureNV21(bytes, width, height, info);
            // 签到时需要判断使用哪一组VIP库(主动交互判断所有VIP)
            // 取出所有VIP信息，放入同一个列表
            List<FaceVipListBean.FaceinfoListBean> localVipInfoLists = new ArrayList<>();
            for (FaceVipListBean localFaceVipListBean : localFaceVipListBeans) {
                List<FaceVipListBean.FaceinfoListBean> faceInfoList = localFaceVipListBean.getFaceinfoList();
                if (faceInfoList != null && faceInfoList.size() > 0) {
                    localVipInfoLists.addAll(faceInfoList);
                }
            }
            // 开始遍历是否有VIP存在
            for (FaceVipListBean.FaceinfoListBean bean : localVipInfoLists) {
                FaceSimilar faceSimilar = compareFaceFeature(feature, bean.getFaceFeature());
                if (faceSimilar.getScore() >= 0.8) {
                    info.setVipInfoBean(bean);
                    returnArcFaceInfos.add(info);
                }
                LogUtils.d("检索相似度：" + faceSimilar.getScore());
            }
        }
        return returnArcFaceInfos;
    }

    /**
     * 注册到本地人脸库(其中一组)
     *
     * @param faceVipListBeans
     */
    public void registerFaceData(final List<FaceVipListBean> faceVipListBeans) throws ExecutionException, InterruptedException {
        Log.e(TAG, "初始数据(组)：" + faceVipListBeans.size());
        if (faceVipListBeans.size() <= 0) {
            Log.e(TAG, "VIP人脸数据注册失败:没有组数据");
            return;
        }
        // 遍历所有组数据
        Iterator<FaceVipListBean> faceVipListBeanIterator = faceVipListBeans.iterator();
        while (faceVipListBeanIterator.hasNext()) {
            FaceVipListBean faceVipListBean = faceVipListBeanIterator.next();
            // 取出当前组的所有信息
            List<FaceVipListBean.FaceinfoListBean> faceinfoListBeans = faceVipListBean.getFaceinfoList();
            if (faceinfoListBeans == null || faceinfoListBeans.size() <= 0) {
                Log.e(TAG, "VIP人脸数据注册失败:没有人信息");
                faceVipListBeanIterator.remove();
                continue;
            }
            // 遍历组内所有人数据
            Iterator<FaceVipListBean.FaceinfoListBean> faceinfoListBeanIterator = faceinfoListBeans.iterator();
            while (faceinfoListBeanIterator.hasNext()) {
                FaceVipListBean.FaceinfoListBean faceinfoListBean = faceinfoListBeanIterator.next();
                File fFile = new File(faceinfoListBean.getLocalFilePath());
                // 文件不存在(剔除)
                if (!fFile.exists()) {
                    faceinfoListBeanIterator.remove();
                    continue;
                }
                final Bitmap bitmap = Glide.with(context).load(fFile)
                        .asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                int width = bitmap.getWidth(), height = bitmap.getHeight();
                byte[] bytes = ImageUtils.bitmapToNv21(bitmap, width, height);
                // 获取当前照片所有人脸
                List<ArcFaceInfo> faces = detectFacesNV21(bytes, width, height);
                if (faces == null || faces.size() != 1) {
                    Log.e(TAG, "VIP人脸数据注册失败:没有人脸");
                    // 多张人脸或没有人脸(剔除)
                    faceinfoListBeanIterator.remove();
                    continue;
                }
                // 只有一张人脸(获取人脸特征信息)
                faceinfoListBean.setFaceFeature(extractFaceFeatureNV21(bytes, width, height, faces.get(0)));
            }
            if (faceinfoListBeans.size() <= 0) {
                Log.e(TAG, "VIP人脸数据注册失败:人信息剔除完");
                faceVipListBeanIterator.remove();
            }
        }
        if (faceVipListBeans.size() > 0) {
            Log.e(TAG, "VIP人脸数据注册:存储整组数据");
            // 存储整组进入VIP库
            VipFaceBank.addVipFace(faceVipListBeans);
        }
    }
}
