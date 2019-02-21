package com.samton.IBenRobotSDK.data;

/**
 * 主动唤醒实体
 */
public class RouseBean {
    /**
     * rs : 1
     * _token_iben : null
     * faceRectangle : {"width":163,"top":108,"left":47,"height":163}
     * speach : 先生您好小笨很高兴为您服务
     * isExist : false
     */

    private String rs;
    private Object _token_iben;
    private FaceRectangleBean faceRectangle;
    private String speach;
    private boolean isExist;

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public Object get_token_iben() {
        return _token_iben;
    }

    public void set_token_iben(Object _token_iben) {
        this._token_iben = _token_iben;
    }

    public FaceRectangleBean getFaceRectangle() {
        return faceRectangle;
    }

    public void setFaceRectangle(FaceRectangleBean faceRectangle) {
        this.faceRectangle = faceRectangle;
    }

    public String getSpeach() {
        return speach;
    }

    public void setSpeach(String speach) {
        this.speach = speach;
    }

    public boolean isIsExist() {
        return isExist;
    }

    public void setIsExist(boolean isExist) {
        this.isExist = isExist;
    }

    public static class FaceRectangleBean {
        /**
         * width : 163
         * top : 108
         * left : 47
         * height : 163
         */

        private int width;
        private int top;
        private int left;
        private int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
