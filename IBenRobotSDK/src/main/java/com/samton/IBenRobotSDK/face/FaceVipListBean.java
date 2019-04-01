package com.samton.IBenRobotSDK.face;

import com.arcsoft.face.FaceFeature;

import java.util.List;

/**
 * 本地VIP人脸信息库
 */
public class FaceVipListBean {
    /**
     * enterpriseId : 202
     * id : 138
     * name : 会议测试
     * number : 4
     * faceinfoList : [{"createDate":1552103360822,"enterpriseId":202,"fid":1446,"fimagepath":"/img/face/daf303aeedaa4b8bb1b3ccdd0ad0bd77.jpg","state":1,"fname":"侯拓鹏","objectId":"967ca3459883c2f382740371fb941fc8","robotWorkmod":0,"isModify":1,"positionName":"测试工程师","groupId":138,"sex":0,"oldImgName":"侯拓鹏，男，测试工程师.jpg"},{"createDate":1552269142301,"enterpriseId":202,"fid":1509,"fimagepath":"/img/face/741ec75bb4a841c89b2869b901b11c7c.jpg","state":1,"fname":"冯巩","objectId":"0a167da8c559833c5e4c748aabd8bd47","robotWorkmod":0,"isModify":1,"positionName":"经理","groupId":138,"sex":0,"oldImgName":"冯巩，男，经理.jpg"},{"createDate":1552269168371,"enterpriseId":202,"fid":1510,"fimagepath":"/img/face/dadb6bb03b0c4384b32497a4c5ef2490.jpg","state":1,"fname":"刘德华","objectId":"2b947c7c4f263186d8d9a2ed579b2d45","robotWorkmod":0,"isModify":1,"positionName":"明星","groupId":138,"sex":0,"oldImgName":"刘德华，男，明星.jpg"},{"createDate":1553061631246,"enterpriseId":202,"fid":1740,"fimagepath":"/img/face/12b70faec9a744ee8e1563998eb32ec4.jpg","state":1,"fname":"赵本山","objectId":"6e0f3a13d92672dc1595f7315b0923d1","robotWorkmod":0,"isModify":1,"positionName":"经理","groupId":138,"sex":0,"oldImgName":"赵本山，男，经理.jpg"}]
     */

    private int enterpriseId;
    private int id;
    private String name;
    private int number;
    private List<FaceinfoListBean> faceinfoList;

    public int getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<FaceinfoListBean> getFaceinfoList() {
        return faceinfoList;
    }

    public void setFaceinfoList(List<FaceinfoListBean> faceinfoList) {
        this.faceinfoList = faceinfoList;
    }

    public static class FaceinfoListBean {
        /**
         * createDate : 1552103360822
         * enterpriseId : 202
         * fid : 1446
         * fimagepath : /img/face/daf303aeedaa4b8bb1b3ccdd0ad0bd77.jpg
         * state : 1
         * fname : 侯拓鹏
         * objectId : 967ca3459883c2f382740371fb941fc8
         * robotWorkmod : 0
         * isModify : 1
         * positionName : 测试工程师
         * groupId : 138
         * sex : 0
         * oldImgName : 侯拓鹏，男，测试工程师.jpg
         */
        private long createDate;
        private int enterpriseId;
        private int fid;
        private String fimagepath;
        private int state;
        private String fname;
        private String objectId;
        private int robotWorkmod;
        private int isModify;
        private String positionName;
        private int groupId;
        private int sex;
        private String oldImgName;
        private String imgMdfive;
        // 人脸特征信息
        private FaceFeature faceFeature;
        // 本地存储位置
        private String localFilePath;

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public int getEnterpriseId() {
            return enterpriseId;
        }

        public void setEnterpriseId(int enterpriseId) {
            this.enterpriseId = enterpriseId;
        }

        public int getFid() {
            return fid;
        }

        public void setFid(int fid) {
            this.fid = fid;
        }

        public String getFimagepath() {
            return fimagepath;
        }

        public void setFimagepath(String fimagepath) {
            this.fimagepath = fimagepath;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getFname() {
            return fname;
        }

        public void setFname(String fname) {
            this.fname = fname;
        }

        public String getObjectId() {
            return objectId;
        }

        public void setObjectId(String objectId) {
            this.objectId = objectId;
        }

        public int getRobotWorkmod() {
            return robotWorkmod;
        }

        public void setRobotWorkmod(int robotWorkmod) {
            this.robotWorkmod = robotWorkmod;
        }

        public int getIsModify() {
            return isModify;
        }

        public void setIsModify(int isModify) {
            this.isModify = isModify;
        }

        public String getPositionName() {
            return positionName;
        }

        public void setPositionName(String positionName) {
            this.positionName = positionName;
        }

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public String getOldImgName() {
            return oldImgName;
        }

        public void setOldImgName(String oldImgName) {
            this.oldImgName = oldImgName;
        }

        public String getImgMdfive() {
            return imgMdfive;
        }

        public void setImgMdfive(String imgMdfive) {
            this.imgMdfive = imgMdfive;
        }

        public FaceFeature getFaceFeature() {
            return faceFeature;
        }

        public void setFaceFeature(FaceFeature faceFeature) {
            this.faceFeature = faceFeature;
        }

        public String getLocalFilePath() {
            return localFilePath;
        }

        public void setLocalFilePath(String localFilePath) {
            this.localFilePath = localFilePath;
        }

        @Override
        public String toString() {
            return "FaceinfoListBean{" +
                    "createDate=" + createDate +
                    ", enterpriseId=" + enterpriseId +
                    ", fid=" + fid +
                    ", fimagepath='" + fimagepath + '\'' +
                    ", state=" + state +
                    ", fname='" + fname + '\'' +
                    ", objectId='" + objectId + '\'' +
                    ", robotWorkmod=" + robotWorkmod +
                    ", isModify=" + isModify +
                    ", positionName='" + positionName + '\'' +
                    ", groupId=" + groupId +
                    ", sex=" + sex +
                    ", oldImgName='" + oldImgName + '\'' +
                    ", imgMdfive='" + imgMdfive + '\'' +
                    ", faceFeature=" + faceFeature +
                    ", localFilePath='" + localFilePath + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "FaceVipListBean{" +
                "enterpriseId=" + enterpriseId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", number=" + number +
                ", faceinfoList=" + faceinfoList +
                '}';
    }
}
