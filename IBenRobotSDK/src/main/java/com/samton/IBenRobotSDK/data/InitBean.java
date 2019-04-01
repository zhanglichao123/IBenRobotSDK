package com.samton.IBenRobotSDK.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/10/30
 *     desc   : 初始化机器人实体类
 *     version: 1.0
 * </pre>
 */

public final class InitBean {

    /**
     * _token_iben : null
     * set : {"leftHeadImg":"http://121.41.40.145:7080/img/deault/robot_icon.png","rightHeadImg":"http://121.41.40.145:7080/img/deault/user_icon.png","logoImg":"http://121.41.40.145:7080/img/deault/company_logo.png","questionTip":["回充电桩","宣讲模式","今天天气怎么样","打印个名片","跳个舞吧","看看我颜值多少分","讲个笑话吧","北京的景区"]}
     * broadcast : {"broadcastId":164,"name":"十八大会议","describe":"会议有什么要求","state":1,"mainImg":"http://121.41.40.145:7080/img/robotLogo/2018-01-09_f38376.png"}
     * currentMap : {"mapName":"小笨新公司","imgUrl":"http://121.41.40.145:7080/img/mapImgFile/2018-01-09_623bf7.jpg","positionPoints":[{"name":"\b办公区","location":"00","broadcast":""},{"name":"\b办公区2","location":"00","broadcast":""},{"name":"\b办公区3","location":"00","broadcast":""},{"name":"\b办公区4","location":"00","broadcast":""},{"name":"\b办公区5","location":"00","broadcast":""},{"name":"\b饮水间","location":"01","broadcast":""}],"file":"http://121.41.40.145:7080/img/mapFile/2018-01-09_623bf7"}
     * expressionMap : {"awaken":"http://121.41.40.145:7080/img/face/default/awaken.gif","anthomaniac":"http://121.41.40.145:7080/img/face/default/anthomaniac.gif","sad":"http://121.41.40.145:7080/img/face/default/sad.gif","happy":"http://121.41.40.145:7080/img/face/default/happy.gif","speak":"http://121.41.40.145:7080/img/face/default/talk.gif","smile":"http://121.41.40.145:7080/img/face/default/smile.gif"}
     * robotName : 小笨
     * robotMod : 1
     * maps : [{"mapName":"小笨总部","imgUrl":"http://121.41.40.145:7080/img/mapImgFile/2018-01-09_592cbe.jpg","file":"http://121.41.40.145:7080/img/mapFile/2018-01-09_592cbe","positionPoints":[{"name":"\b办公区","location":"00","broadcast":""},{"name":"\b饮水间","location":"01","broadcast":""}]},{"mapName":"小笨新公司","imgUrl":"http://121.41.40.145:7080/img/mapImgFile/2018-01-09_623bf7.jpg","file":"http://121.41.40.145:7080/img/mapFile/2018-01-09_623bf7","positionPoints":[{"name":"\b办公区","location":"00","broadcast":""},{"name":"\b办公区2","location":"00","broadcast":""},{"name":"\b办公区3","location":"00","broadcast":""},{"name":"\b办公区4","location":"00","broadcast":""},{"name":"\b办公区5","location":"00","broadcast":""},{"name":"\b饮水间","location":"01","broadcast":""}]}]
     * meeting : {"meetingId":90,"name":"小笨大会议","meetingDate":1515513600000,"meetingStartTime":3600000,"meetingEndTime":10800000,"newVisitorWords":"你好呀","repeatVisitorWords":"你来过了","vipVisitorWords":"vip你好","state":1}
     * preachGroup : {"groupId":74,"name":"小笨宣讲组","describe":"小笨宣讲描述","sceneId":55,"state":1,"sceneName":"小笨新公司","preachItemList":[{"itemId":372,"groupId":74,"slocId":145,"preachType":1,"imgUrl":"http://121.41.40.145:7080/img/preach/img/2018-01-09_ba9a4c.jpg","content":"我已到达办公区","contentOnTheWay":"带我去办公区","showType":1,"orderNum":1,"state":1,"slocName":"\b办公区"},{"itemId":373,"groupId":74,"slocId":146,"preachType":1,"imgUrl":"http://121.41.40.145:7080/img/preach/img/2018-01-09_580923.jpg","content":"我已到达办公区2","contentOnTheWay":"带我去办公区2","showType":2,"orderNum":2,"state":1,"slocName":"\b办公区2"},{"itemId":374,"groupId":74,"slocId":147,"preachType":2,"imgUrl":"http://121.41.40.145:7080/img/preach/img/2018-01-09_4098a5.jpg","voiceUrl":"http://121.41.40.145:7080/img/preach/voice/2018-01-09_bafc41.mp3","contentOnTheWay":"带我去办公区3","showType":0,"orderNum":3,"state":1,"slocName":"\b办公区3"},{"itemId":375,"groupId":74,"slocId":148,"preachType":3,"videoUrl":"http://121.41.40.145:7080/img/preach/video/2018-01-09_b8fe7c.mp4","contentOnTheWay":"带我去办公区4","showType":0,"orderNum":4,"state":1,"slocName":"\b办公区4"},{"itemId":376,"groupId":74,"slocId":149,"orderNum":5,"state":1,"slocName":"\b办公区5"},{"itemId":377,"groupId":74,"slocId":150,"orderNum":6,"state":1,"slocName":"\b饮水间"}]}
     * printGroup : {"groupId":86,"name":"小笨名片","state":1,"prints":[{"printId":168,"name":"小笨名片","describe":"魂牵梦萦","state":1,"printItems":[{"printItemId":749,"content":"我想打印","style":{"type":"jsonb","value":"{\"size\": 3, \"type\": 1, \"align\": 2, \"width\": null, \"height\": null, \"weight\": null, \"afterParagraph\": 5}"},"printId":168,"orderNum":1,"state":1},{"printItemId":750,"content":"http://121.41.40.145:7080/img/print//2018-01-09_f11edb.jpg","style":{"type":"jsonb","value":"{\"size\": null, \"type\": 2, \"align\": 2, \"width\": 400, \"height\": 0, \"weight\": null, \"afterParagraph\": null}"},"printId":168,"orderNum":2,"state":1},{"printItemId":751,"content":"魂牵梦萦","style":{"type":"jsonb","value":"{\"size\": null, \"type\": 3, \"align\": 2, \"width\": 400, \"height\": 0, \"weight\": null, \"afterParagraph\": 5}"},"printId":168,"orderNum":3,"state":1}]}]}
     * workAttendance : {"workAttendanceId":69,"companyName":"慧闻科技","startTime":3600000,"endTime":5400000,"state":1}
     * cleverPreach : {"id":1,"robUuid":"f72c4e38d0f64a338ea32bbcc0194f99","preachEndMsg":"宣讲结束播报","stopPreachPwd":"终止宣讲口令","halfwayQuizFlag":2,"switchMsg":"切换业务咨询播报语","advisoryMsg":"需要咨询播报语","noAdvisoryMsg":"不需要咨询播报语","beforeMsg":"运动前提示播报语","noPreachMsg":"不需要宣讲播报语","otherMsg":"其它内容回复","preachImg":"http://43.240.248.100:7080/img/preach/img/202/2019-02-22_2e2a1e.jpg","callMsg":"宣讲号召播报","callMsgTime":10,"isleadVip":"欢迎尊敬的#全名#@你好","isleadNotVip":"欢迎尊敬的#性别#@你好","noPreachTime":30,"repeatPointFlag":2,"notPointMsg":"不到点播报","halfwayTime":30,"halfwayOtherMsg":"中途提问-其他内容回复"}
     * voiceTag : xiaoyan
     */
    // 存储是否需要低电量播报状态
    private boolean isSpeakPower30 = true;
    private boolean isSpeakPower15 = true;

    private int rs;
    private DataBean data;
    private SetBean set;
    private BroadcastBean broadcast;
    private CurrentMapBean currentMap;
    private ExpressionMapBean expressionMap;
    private String robotName;
    private int robotMod;
    private MeetingBean meeting;
    private PreachGroupBean preachGroup;
    private PrintGroupBean printGroup;
    private WorkAttendanceBean workAttendance;
    private String voiceTag;
    private String preachWord;
    private List<MapsBean> maps;
    private String broadcastImg;
    private String preachImg;
    private int carouselGroupId;
    private int workModeid;
    private CleverPreachBean cleverPreach;






    public int getWorkModeid() {
        return workModeid;
    }

    public void setWorkModeid(int workModeid) {
        this.workModeid = workModeid;
    }

    public String getPreachWord() {
        return preachWord;
    }

    public void setPreachWord(String preachWord) {
        this.preachWord = preachWord;
    }

    public String getBroadcastImg() {
        return broadcastImg == null ? "" : broadcastImg;
    }

    public void setBroadcastImg(String broadcastImg) {
        this.broadcastImg = broadcastImg;
    }

    public String getPreachImg() {
        return preachImg == null ? "" : preachImg;
    }

    public void setPreachImg(String preachImg) {
        this.preachImg = preachImg;
    }

    public int getCarouselGroupId() {
        return carouselGroupId;
    }

    public void setCarouselGroupId(int carouselGroupId) {
        this.carouselGroupId = carouselGroupId;
    }

    public int getRs() {
        return rs;
    }

    public void setRs(int rs) {
        this.rs = rs;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public SetBean getSet() {
        return set;
    }

    public void setSet(SetBean set) {
        this.set = set;
    }

    public BroadcastBean getBroadcast() {
        return broadcast;
    }

    public void setBroadcast(BroadcastBean broadcast) {
        this.broadcast = broadcast;
    }

    public CurrentMapBean getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(CurrentMapBean currentMap) {
        this.currentMap = currentMap;
    }

    public ExpressionMapBean getExpressionMap() {
        return expressionMap;
    }

    public void setExpressionMap(ExpressionMapBean expressionMap) {
        this.expressionMap = expressionMap;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public int getRobotMod() {
        return robotMod;
    }

    public void setRobotMod(int robotMod) {
        this.robotMod = robotMod;
    }

    public MeetingBean getMeeting() {
        return meeting;
    }

    public void setMeeting(MeetingBean meeting) {
        this.meeting = meeting;
    }

    public PreachGroupBean getPreachGroup() {
        return preachGroup;
    }

    public void setPreachGroup(PreachGroupBean preachGroup) {
        this.preachGroup = preachGroup;
    }

    public PrintGroupBean getPrintGroup() {
        return printGroup;
    }

    public void setPrintGroup(PrintGroupBean printGroup) {
        this.printGroup = printGroup;
    }

    public WorkAttendanceBean getWorkAttendance() {
        return workAttendance;
    }

    public void setWorkAttendance(WorkAttendanceBean workAttendance) {
        this.workAttendance = workAttendance;
    }

    public String getVoiceTag() {
        return voiceTag;
    }

    public void setVoiceTag(String voiceTag) {
        this.voiceTag = voiceTag;
    }

    public List<MapsBean> getMaps() {
        return maps;
    }

    public void setMaps(List<MapsBean> maps) {
        this.maps = maps;
    }

    public CleverPreachBean getCleverPreach() {
        return cleverPreach;
    }

    public void setCleverPreach(CleverPreachBean cleverPreach) {
        this.cleverPreach = cleverPreach;
    }

    public static class SetBean {
        /**
         * leftHeadImg : http://121.41.40.145:7080/img/deault/robot_icon.png
         * rightHeadImg : http://121.41.40.145:7080/img/deault/user_icon.png
         * logoImg : http://121.41.40.145:7080/img/deault/company_logo.png
         * questionTip : ["回充电桩","宣讲模式","今天天气怎么样","打印个名片","跳个舞吧","看看我颜值多少分","讲个笑话吧","北京的景区"]
         */
        private String leftHeadImg;
        private String rightHeadImg;
        private String logoImg;
        private List<String> questionTip;

        /**
         * fristMsg : 首次进去欢迎语
         * contentReply : 首次进去欢迎语
         * anthomaniacPwd : ["花痴"]
         * happyPwd : ["高兴"]
         * sadPwd : ["难过"]
         * levelPwd : ["双手持平"]
         * movePwd : ["双手举高"]
         * rightHandPwd : ["右手举高"]
         */

        private String fristMsg;
        private String contentReply;
        private List<String> anthomaniacPwd;
        private List<String> happyPwd;
        private List<String> sadPwd;
        private List<String> levelPwd;
        private List<String> movePwd;
        private List<String> rightHandPwd;

        public String getLeftHeadImg() {
            return leftHeadImg;
        }

        public void setLeftHeadImg(String leftHeadImg) {
            this.leftHeadImg = leftHeadImg;
        }

        public String getRightHeadImg() {
            return rightHeadImg;
        }

        public void setRightHeadImg(String rightHeadImg) {
            this.rightHeadImg = rightHeadImg;
        }

        public String getLogoImg() {
            return logoImg;
        }

        public void setLogoImg(String logoImg) {
            this.logoImg = logoImg;
        }

        public List<String> getQuestionTip() {
            return questionTip;
        }

        public void setQuestionTip(List<String> questionTip) {
            this.questionTip = questionTip;
        }

        public String getFristMsg() {
            return fristMsg;
        }

        public void setFristMsg(String fristMsg) {
            this.fristMsg = fristMsg;
        }

        public String getContentReply() {
            return contentReply;
        }

        public void setContentReply(String contentReply) {
            this.contentReply = contentReply;
        }

        public List<String> getAnthomaniacPwd() {
            return anthomaniacPwd;
        }

        public void setAnthomaniacPwd(List<String> anthomaniacPwd) {
            this.anthomaniacPwd = anthomaniacPwd;
        }

        public List<String> getHappyPwd() {
            return happyPwd;
        }

        public void setHappyPwd(List<String> happyPwd) {
            this.happyPwd = happyPwd;
        }

        public List<String> getSadPwd() {
            return sadPwd;
        }

        public void setSadPwd(List<String> sadPwd) {
            this.sadPwd = sadPwd;
        }

        public List<String> getLevelPwd() {
            return levelPwd;
        }

        public void setLevelPwd(List<String> levelPwd) {
            this.levelPwd = levelPwd;
        }

        public List<String> getMovePwd() {
            return movePwd;
        }

        public void setMovePwd(List<String> movePwd) {
            this.movePwd = movePwd;
        }

        public List<String> getRightHandPwd() {
            return rightHandPwd;
        }

        public void setRightHandPwd(List<String> rightHandPwd) {
            this.rightHandPwd = rightHandPwd;
        }
    }

    public static class BroadcastBean {
        /**
         * broadcastId : 164
         * name : 十八大会议
         * describe : 会议有什么要求
         * state : 1
         * mainImg : http://121.41.40.145:7080/img/robotLogo/2018-01-09_f38376.png
         */

        private int broadcastId;
        private String name;
        private String describe;
        private int state;
        private String mainImg;
        private int robModelNum;
        private int flag;

        public int getRobModelNum() {
            return robModelNum;
        }

        public void setRobModelNum(int robModelNum) {
            this.robModelNum = robModelNum;
        }

        public int getFlag() {
            return flag;
        }

        public void setFlag(int flag) {
            this.flag = flag;
        }

        public int getBroadcastId() {
            return broadcastId;
        }

        public void setBroadcastId(int broadcastId) {
            this.broadcastId = broadcastId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getMainImg() {
            return mainImg;
        }

        public void setMainImg(String mainImg) {
            this.mainImg = mainImg;
        }
    }

    public static class CurrentMapBean {
        /**
         * mapName : 小笨新公司
         * imgUrl : http://121.41.40.145:7080/img/mapImgFile/2018-01-09_623bf7.jpg
         * positionPoints : [{"name":"\b办公区","location":"00","broadcast":""},{"name":"\b办公区2","location":"00","broadcast":""},{"name":"\b办公区3","location":"00","broadcast":""},{"name":"\b办公区4","location":"00","broadcast":""},{"name":"\b办公区5","location":"00","broadcast":""},{"name":"\b饮水间","location":"01","broadcast":""}]
         * file : http://121.41.40.145:7080/img/mapFile/2018-01-09_623bf7
         */
        private int mapId;
        private String mapName;
        private String imgUrl;
        private String file;
        private List<PositionPointsBean> positionPoints;

        public int getMapId() {
            return mapId;
        }

        public void setMapId(int mapId) {
            this.mapId = mapId;
        }

        public String getMapName() {
            return mapName;
        }

        public void setMapName(String mapName) {
            this.mapName = mapName;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public List<PositionPointsBean> getPositionPoints() {
            return positionPoints;
        }

        public void setPositionPoints(List<PositionPointsBean> positionPoints) {
            this.positionPoints = positionPoints;
        }

        public static class PositionPointsBean {
            /**
             * name : 办公区
             * location : 00
             * broadcast :
             */

            private String name;
            private String location;
            private String broadcast;
            private List<String> otherNames;

            public List<String> getOtherNames() {
                return otherNames;
            }

            public void setOtherNames(List<String> otherNames) {
                this.otherNames = otherNames;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getBroadcast() {
                return broadcast;
            }

            public void setBroadcast(String broadcast) {
                this.broadcast = broadcast;
            }
        }
    }

    public static class ExpressionMapBean {
        /**
         * awaken : http://121.41.40.145:7080/img/face/default/awaken.gif
         * anthomaniac : http://121.41.40.145:7080/img/face/default/anthomaniac.gif
         * sad : http://121.41.40.145:7080/img/face/default/sad.gif
         * happy : http://121.41.40.145:7080/img/face/default/happy.gif
         * speak : http://121.41.40.145:7080/img/face/default/talk.gif
         * smile : http://121.41.40.145:7080/img/face/default/smile.gif
         */

        private String awaken;
        private String anthomaniac;
        private String sad;
        private String happy;
        private String speak;
        private String smile;

        public String getAwaken() {
            return awaken;
        }

        public void setAwaken(String awaken) {
            this.awaken = awaken;
        }

        public String getAnthomaniac() {
            return anthomaniac;
        }

        public void setAnthomaniac(String anthomaniac) {
            this.anthomaniac = anthomaniac;
        }

        public String getSad() {
            return sad;
        }

        public void setSad(String sad) {
            this.sad = sad;
        }

        public String getHappy() {
            return happy;
        }

        public void setHappy(String happy) {
            this.happy = happy;
        }

        public String getSpeak() {
            return speak;
        }

        public void setSpeak(String speak) {
            this.speak = speak;
        }

        public String getSmile() {
            return smile;
        }

        public void setSmile(String smile) {
            this.smile = smile;
        }
    }

    public static class MeetingBean {

        /**
         * meetingId : 121
         * name : V4.0会场签到3
         * meetingStartTime : 1551666600000
         * meetingEndTime : 1551711600000
         * newVisitorWords : 欢迎#性别#入场
         * repeatVisitorWords : 您已签到成功，无需重复签到
         * vipVisitorWords : 欢迎vip#姓氏##性别#入场
         * photoGroupId : 93
         * guideWords : 没有签到的嘉宾请到这里签到
         */

        private int meetingId;
        private String name;
        private long meetingStartTime;
        private long meetingEndTime;
        private String newVisitorWords;
        private String repeatVisitorWords;
        private String vipVisitorWords;
        private int photoGroupId;
        private String guideWords;

        public int getMeetingId() {
            return meetingId;
        }

        public void setMeetingId(int meetingId) {
            this.meetingId = meetingId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getMeetingStartTime() {
            return meetingStartTime;
        }

        public void setMeetingStartTime(long meetingStartTime) {
            this.meetingStartTime = meetingStartTime;
        }

        public long getMeetingEndTime() {
            return meetingEndTime;
        }

        public void setMeetingEndTime(long meetingEndTime) {
            this.meetingEndTime = meetingEndTime;
        }

        public String getNewVisitorWords() {
            return newVisitorWords;
        }

        public void setNewVisitorWords(String newVisitorWords) {
            this.newVisitorWords = newVisitorWords;
        }

        public String getRepeatVisitorWords() {
            return repeatVisitorWords;
        }

        public void setRepeatVisitorWords(String repeatVisitorWords) {
            this.repeatVisitorWords = repeatVisitorWords;
        }

        public String getVipVisitorWords() {
            return vipVisitorWords;
        }

        public void setVipVisitorWords(String vipVisitorWords) {
            this.vipVisitorWords = vipVisitorWords;
        }

        public int getPhotoGroupId() {
            return photoGroupId;
        }

        public void setPhotoGroupId(int photoGroupId) {
            this.photoGroupId = photoGroupId;
        }

        public String getGuideWords() {
            return guideWords;
        }

        public void setGuideWords(String guideWords) {
            this.guideWords = guideWords;
        }
    }

    public static class PreachGroupBean {
        /**
         * groupId : 74
         * name : 小笨宣讲组
         * describe : 小笨宣讲描述
         * sceneId : 55
         * state : 1
         * sceneName : 小笨新公司
         * preachItemList : [{"itemId":372,"groupId":74,"slocId":145,"preachType":1,"imgUrl":"http://121.41.40.145:7080/img/preach/img/2018-01-09_ba9a4c.jpg","content":"我已到达办公区","contentOnTheWay":"带我去办公区","showType":1,"orderNum":1,"state":1,"slocName":"\b办公区"},{"itemId":373,"groupId":74,"slocId":146,"preachType":1,"imgUrl":"http://121.41.40.145:7080/img/preach/img/2018-01-09_580923.jpg","content":"我已到达办公区2","contentOnTheWay":"带我去办公区2","showType":2,"orderNum":2,"state":1,"slocName":"\b办公区2"},{"itemId":374,"groupId":74,"slocId":147,"preachType":2,"imgUrl":"http://121.41.40.145:7080/img/preach/img/2018-01-09_4098a5.jpg","voiceUrl":"http://121.41.40.145:7080/img/preach/voice/2018-01-09_bafc41.mp3","contentOnTheWay":"带我去办公区3","showType":0,"orderNum":3,"state":1,"slocName":"\b办公区3"},{"itemId":375,"groupId":74,"slocId":148,"preachType":3,"videoUrl":"http://121.41.40.145:7080/img/preach/video/2018-01-09_b8fe7c.mp4","contentOnTheWay":"带我去办公区4","showType":0,"orderNum":4,"state":1,"slocName":"\b办公区4"},{"itemId":376,"groupId":74,"slocId":149,"orderNum":5,"state":1,"slocName":"\b办公区5"},{"itemId":377,"groupId":74,"slocId":150,"orderNum":6,"state":1,"slocName":"\b饮水间"}]
         */

        private int groupId;
        private String name;
        private String describe;
        private int sceneId;
        private int state;
        private String sceneName;
        private List<PreachItemListBean> preachItemList;

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescribe() {
            return describe;
        }

        public void setDescribe(String describe) {
            this.describe = describe;
        }

        public int getSceneId() {
            return sceneId;
        }

        public void setSceneId(int sceneId) {
            this.sceneId = sceneId;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getSceneName() {
            return sceneName;
        }

        public void setSceneName(String sceneName) {
            this.sceneName = sceneName;
        }

        public List<PreachItemListBean> getPreachItemList() {
            return preachItemList;
        }

        public void setPreachItemList(List<PreachItemListBean> preachItemList) {
            this.preachItemList = preachItemList;
        }

        public static class PreachItemListBean {
            /**
             * itemId : 372
             * groupId : 74
             * slocId : 145
             * preachType : 1
             * imgUrl : http://121.41.40.145:7080/img/preach/img/2018-01-09_ba9a4c.jpg
             * content : 我已到达办公区
             * contentOnTheWay : 带我去办公区
             * showType : 1
             * orderNum : 1
             * state : 1
             * slocName : 办公区
             * voiceUrl : http://121.41.40.145:7080/img/preach/voice/2018-01-09_bafc41.mp3
             * videoUrl : http://121.41.40.145:7080/img/preach/video/2018-01-09_b8fe7c.mp4
             */

            private int itemId;
            private int groupId;
            private int slocId;
            private int preachType;
            private String imgUrl;
            private String content;
            private String contentOnTheWay;
            private int showType;
            private int orderNum;
            private int state;
            private String slocName;
            private String voiceUrl;
            private String videoUrl;
            private String location;
            private String voiceOldname;
            private String videoOldname;
            private String imgOldname;
            private boolean isReach;

            public boolean isReach() {
                return isReach;
            }

            public void setReach(boolean reach) {
                isReach = reach;
            }

            public String getVoiceOldname() {
                return voiceOldname;
            }

            public void setVoiceOldname(String voiceOldname) {
                this.voiceOldname = voiceOldname;
            }

            public String getVideoOldname() {
                return videoOldname;
            }

            public void setVideoOldname(String videoOldname) {
                this.videoOldname = videoOldname;
            }

            public String getImgOldname() {
                return imgOldname;
            }

            public void setImgOldname(String imgOldname) {
                this.imgOldname = imgOldname;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public int getItemId() {
                return itemId;
            }

            public void setItemId(int itemId) {
                this.itemId = itemId;
            }

            public int getGroupId() {
                return groupId;
            }

            public void setGroupId(int groupId) {
                this.groupId = groupId;
            }

            public int getSlocId() {
                return slocId;
            }

            public void setSlocId(int slocId) {
                this.slocId = slocId;
            }

            public int getPreachType() {
                return preachType;
            }

            public void setPreachType(int preachType) {
                this.preachType = preachType;
            }

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getContentOnTheWay() {
                return contentOnTheWay;
            }

            public void setContentOnTheWay(String contentOnTheWay) {
                this.contentOnTheWay = contentOnTheWay;
            }

            public int getShowType() {
                return showType;
            }

            public void setShowType(int showType) {
                this.showType = showType;
            }

            public int getOrderNum() {
                return orderNum;
            }

            public void setOrderNum(int orderNum) {
                this.orderNum = orderNum;
            }

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public String getSlocName() {
                return slocName;
            }

            public void setSlocName(String slocName) {
                this.slocName = slocName;
            }

            public String getVoiceUrl() {
                return voiceUrl;
            }

            public void setVoiceUrl(String voiceUrl) {
                this.voiceUrl = voiceUrl;
            }

            public String getVideoUrl() {
                return videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }
        }
    }

    public static class PrintGroupBean {
        /**
         * groupId : 86
         * name : 小笨名片
         * state : 1
         * prints : [{"printId":168,"name":"小笨名片","describe":"魂牵梦萦","state":1,"printItems":[{"printItemId":749,"content":"我想打印","style":{"type":"jsonb","value":"{\"size\": 3, \"type\": 1, \"align\": 2, \"width\": null, \"height\": null, \"weight\": null, \"afterParagraph\": 5}"},"printId":168,"orderNum":1,"state":1},{"printItemId":750,"content":"http://121.41.40.145:7080/img/print//2018-01-09_f11edb.jpg","style":{"type":"jsonb","value":"{\"size\": null, \"type\": 2, \"align\": 2, \"width\": 400, \"height\": 0, \"weight\": null, \"afterParagraph\": null}"},"printId":168,"orderNum":2,"state":1},{"printItemId":751,"content":"魂牵梦萦","style":{"type":"jsonb","value":"{\"size\": null, \"type\": 3, \"align\": 2, \"width\": 400, \"height\": 0, \"weight\": null, \"afterParagraph\": 5}"},"printId":168,"orderNum":3,"state":1}]}]
         */

        private int groupId;
        private String name;
        private int state;
        private List<PrintsBean> prints;

        public int getGroupId() {
            return groupId;
        }

        public void setGroupId(int groupId) {
            this.groupId = groupId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public List<PrintsBean> getPrints() {
            return prints;
        }

        public void setPrints(List<PrintsBean> prints) {
            this.prints = prints;
        }

        public static class PrintsBean {
            /**
             * printId : 168
             * name : 小笨名片
             * describe : 魂牵梦萦
             * state : 1
             * printItems : [{"printItemId":749,"content":"我想打印","style":{"type":"jsonb","value":"{\"size\": 3, \"type\": 1, \"align\": 2, \"width\": null, \"height\": null, \"weight\": null, \"afterParagraph\": 5}"},"printId":168,"orderNum":1,"state":1},{"printItemId":750,"content":"http://121.41.40.145:7080/img/print//2018-01-09_f11edb.jpg","style":{"type":"jsonb","value":"{\"size\": null, \"type\": 2, \"align\": 2, \"width\": 400, \"height\": 0, \"weight\": null, \"afterParagraph\": null}"},"printId":168,"orderNum":2,"state":1},{"printItemId":751,"content":"魂牵梦萦","style":{"type":"jsonb","value":"{\"size\": null, \"type\": 3, \"align\": 2, \"width\": 400, \"height\": 0, \"weight\": null, \"afterParagraph\": 5}"},"printId":168,"orderNum":3,"state":1}]
             */

            private int printId;
            private String name;
            private String describe;
            private int state;
            private List<PrintItemsBean> printItems;

            public int getPrintId() {
                return printId;
            }

            public void setPrintId(int printId) {
                this.printId = printId;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getDescribe() {
                return describe;
            }

            public void setDescribe(String describe) {
                this.describe = describe;
            }

            public int getState() {
                return state;
            }

            public void setState(int state) {
                this.state = state;
            }

            public List<PrintItemsBean> getPrintItems() {
                return printItems;
            }

            public void setPrintItems(List<PrintItemsBean> printItems) {
                this.printItems = printItems;
            }

            public static class PrintItemsBean {
                /**
                 * printItemId : 749
                 * content : 我想打印
                 * style : {"type":"jsonb","value":"{\"size\": 3, \"type\": 1, \"align\": 2, \"width\": null, \"height\": null, \"weight\": null, \"afterParagraph\": 5}"}
                 * printId : 168
                 * orderNum : 1
                 * state : 1
                 * "printType":  2,
                 * "dynamicType":  0,
                 * "startNum":  0,
                 * "endNum":  0
                 */

                private int printItemId;
                private String content;
                private StyleBean style;
                private int printId;
                private int orderNum;
                private int state;
                private int printType;
                private int dynamicType;
                private int startNum;
                private int endNum;

                public int getPrintType() {
                    return printType;
                }

                public void setPrintType(int printType) {
                    this.printType = printType;
                }

                public int getDynamicType() {
                    return dynamicType;
                }

                public void setDynamicType(int dynamicType) {
                    this.dynamicType = dynamicType;
                }

                public int getStartNum() {
                    return startNum;
                }

                public void setStartNum(int startNum) {
                    this.startNum = startNum;
                }

                public int getEndNum() {
                    return endNum;
                }

                public void setEndNum(int endNum) {
                    this.endNum = endNum;
                }

                public int getPrintItemId() {
                    return printItemId;
                }

                public void setPrintItemId(int printItemId) {
                    this.printItemId = printItemId;
                }

                public String getContent() {
                    return content;
                }

                public void setContent(String content) {
                    this.content = content;
                }

                public StyleBean getStyle() {
                    return style;
                }

                public void setStyle(StyleBean style) {
                    this.style = style;
                }

                public int getPrintId() {
                    return printId;
                }

                public void setPrintId(int printId) {
                    this.printId = printId;
                }

                public int getOrderNum() {
                    return orderNum;
                }

                public void setOrderNum(int orderNum) {
                    this.orderNum = orderNum;
                }

                public int getState() {
                    return state;
                }

                public void setState(int state) {
                    this.state = state;
                }

                public static class StyleBean {
                    /**
                     * type : jsonb
                     * value : {"size": 3, "type": 1, "align": 2, "width": null, "height": null, "weight": null, "afterParagraph": 5}
                     */

                    private String type;
                    private String value;

                    public String getType() {
                        return type;
                    }

                    public void setType(String type) {
                        this.type = type;
                    }

                    public String getValue() {
                        return value;
                    }

                    public void setValue(String value) {
                        this.value = value;
                    }
                }
            }
        }
    }

    public static class WorkAttendanceBean {

        /**
         * workAttendanceId : 116
         * startTime : 36000000
         * endTime : 55800000
         * faceCount : 3
         * startWarnTime : 1800000
         * inworkContent : #姓氏##性别#上班打卡成功
         * startWarnContent : 还有半小时就要上班了
         * offworkContent : #姓名##性别#下班打卡成功
         * photoGroupId : 128
         * notEmployeeContent : 上班打卡仅本公司内部使用
         * specialContent : #姓氏##姓名#打卡成功
         * bufferTime : 6
         */

        private int workAttendanceId;
        private int startTime;
        private int endTime;
        private int faceCount;
        private int startWarnTime;
        private String inworkContent;
        private String startWarnContent;
        private String offworkContent;
        private int photoGroupId;
        private String notEmployeeContent;
        private String specialContent;
        private int bufferTime;
        private String companyName;
        private int workFaceCount;

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public int getWorkFaceCount() {
            return workFaceCount;
        }

        public void setWorkFaceCount(int workFaceCount) {
            this.workFaceCount = workFaceCount;
        }

        public int getWorkAttendanceId() {
            return workAttendanceId;
        }

        public void setWorkAttendanceId(int workAttendanceId) {
            this.workAttendanceId = workAttendanceId;
        }

        public int getStartTime() {
            return startTime;
        }

        public void setStartTime(int startTime) {
            this.startTime = startTime;
        }

        public int getEndTime() {
            return endTime;
        }

        public void setEndTime(int endTime) {
            this.endTime = endTime;
        }

        public int getFaceCount() {
            return faceCount;
        }

        public void setFaceCount(int faceCount) {
            this.faceCount = faceCount;
        }

        public int getStartWarnTime() {
            return startWarnTime;
        }

        public void setStartWarnTime(int startWarnTime) {
            this.startWarnTime = startWarnTime;
        }

        public String getInworkContent() {
            return inworkContent;
        }

        public void setInworkContent(String inworkContent) {
            this.inworkContent = inworkContent;
        }

        public String getStartWarnContent() {
            return startWarnContent;
        }

        public void setStartWarnContent(String startWarnContent) {
            this.startWarnContent = startWarnContent;
        }

        public String getOffworkContent() {
            return offworkContent;
        }

        public void setOffworkContent(String offworkContent) {
            this.offworkContent = offworkContent;
        }

        public int getPhotoGroupId() {
            return photoGroupId;
        }

        public void setPhotoGroupId(int photoGroupId) {
            this.photoGroupId = photoGroupId;
        }

        public String getNotEmployeeContent() {
            return notEmployeeContent;
        }

        public void setNotEmployeeContent(String notEmployeeContent) {
            this.notEmployeeContent = notEmployeeContent;
        }

        public String getSpecialContent() {
            return specialContent;
        }

        public void setSpecialContent(String specialContent) {
            this.specialContent = specialContent;
        }

        public int getBufferTime() {
            return bufferTime;
        }

        public void setBufferTime(int bufferTime) {
            this.bufferTime = bufferTime;
        }
    }

    public static class MapsBean {
        /**
         * mapId : 148
         * mapName : 小笨总部
         * imgUrl : http://121.41.40.145:7080/img/mapImgFile/2018-01-09_592cbe.jpg
         * file : http://121.41.40.145:7080/img/mapFile/2018-01-09_592cbe
         * positionPoints : [{"name":"\b办公区","location":"00","broadcast":""},{"name":"\b饮水间","location":"01","broadcast":""}]
         */

        private String mapId;
        private String mapName;
        private String imgUrl;
        private String file;
        private List<PositionPointsBeanX> positionPoints;

        public String getMapId() {
            return mapId;
        }

        public void setMapId(String mapId) {
            this.mapId = mapId;
        }

        public String getMapName() {
            return mapName;
        }

        public void setMapName(String mapName) {
            this.mapName = mapName;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public List<PositionPointsBeanX> getPositionPoints() {
            return positionPoints;
        }

        public void setPositionPoints(List<PositionPointsBeanX> positionPoints) {
            this.positionPoints = positionPoints;
        }

        public static class PositionPointsBeanX {
            /**
             * name : 办公区
             * location : 00
             * broadcast :
             */

            private String name;
            private String location;
            private String broadcast;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getLocation() {
                return location;
            }

            public void setLocation(String location) {
                this.location = location;
            }

            public String getBroadcast() {
                return broadcast;
            }

            public void setBroadcast(String broadcast) {
                this.broadcast = broadcast;
            }
        }
    }

    public static class CleverPreachBean {
        /**
         * id : 1
         * robUuid : f72c4e38d0f64a338ea32bbcc0194f99
         * preachEndMsg : 宣讲结束播报
         * stopPreachPwd : 终止宣讲口令
         * halfwayQuizFlag : 2
         * switchMsg : 切换业务咨询播报语
         * advisoryMsg : 需要咨询播报语
         * noAdvisoryMsg : 不需要咨询播报语
         * beforeMsg : 运动前提示播报语
         * noPreachMsg : 不需要宣讲播报语
         * otherMsg : 其它内容回复
         * preachImg : http://43.240.248.100:7080/img/preach/img/202/2019-02-22_2e2a1e.jpg
         * callMsg : 宣讲号召播报
         * callMsgTime : 10
         * isleadVip : 欢迎尊敬的#全名#@你好
         * isleadNotVip : 欢迎尊敬的#性别#@你好
         * noPreachTime : 30
         * repeatPointFlag : 2
         * notPointMsg : 不到点播报
         * halfwayTime : 30
         * halfwayOtherMsg : 中途提问-其他内容回复
         */

        private int id;
        private String robUuid;
        private String preachEndMsg;
        private String stopPreachPwd;
        private int halfwayQuizFlag;
        private String switchMsg;
        private String advisoryMsg;
        private String noAdvisoryMsg;
        private String beforeMsg;
        private String noPreachMsg;
        private String otherMsg;
        @SerializedName("preachImg")
        private String preachImgX;
        private String callMsg;
        private int callMsgTime;
        private String isleadVip;
        private String isleadNotVip;
        private int noPreachTime;
        private int repeatPointFlag;
        private String notPointMsg;
        private int halfwayTime;
        private String halfwayOtherMsg;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRobUuid() {
            return robUuid;
        }

        public void setRobUuid(String robUuid) {
            this.robUuid = robUuid;
        }

        public String getPreachEndMsg() {
            return preachEndMsg;
        }

        public void setPreachEndMsg(String preachEndMsg) {
            this.preachEndMsg = preachEndMsg;
        }

        public String getStopPreachPwd() {
            return stopPreachPwd;
        }

        public void setStopPreachPwd(String stopPreachPwd) {
            this.stopPreachPwd = stopPreachPwd;
        }

        public int getHalfwayQuizFlag() {
            return halfwayQuizFlag;
        }

        public void setHalfwayQuizFlag(int halfwayQuizFlag) {
            this.halfwayQuizFlag = halfwayQuizFlag;
        }

        public String getSwitchMsg() {
            return switchMsg;
        }

        public void setSwitchMsg(String switchMsg) {
            this.switchMsg = switchMsg;
        }

        public String getAdvisoryMsg() {
            return advisoryMsg;
        }

        public void setAdvisoryMsg(String advisoryMsg) {
            this.advisoryMsg = advisoryMsg;
        }

        public String getNoAdvisoryMsg() {
            return noAdvisoryMsg;
        }

        public void setNoAdvisoryMsg(String noAdvisoryMsg) {
            this.noAdvisoryMsg = noAdvisoryMsg;
        }

        public String getBeforeMsg() {
            return beforeMsg;
        }

        public void setBeforeMsg(String beforeMsg) {
            this.beforeMsg = beforeMsg;
        }

        public String getNoPreachMsg() {
            return noPreachMsg;
        }

        public void setNoPreachMsg(String noPreachMsg) {
            this.noPreachMsg = noPreachMsg;
        }

        public String getOtherMsg() {
            return otherMsg;
        }

        public void setOtherMsg(String otherMsg) {
            this.otherMsg = otherMsg;
        }

        public String getPreachImgX() {
            return preachImgX;
        }

        public void setPreachImgX(String preachImgX) {
            this.preachImgX = preachImgX;
        }

        public String getCallMsg() {
            return callMsg;
        }

        public void setCallMsg(String callMsg) {
            this.callMsg = callMsg;
        }

        public int getCallMsgTime() {
            return callMsgTime;
        }

        public void setCallMsgTime(int callMsgTime) {
            this.callMsgTime = callMsgTime;
        }

        public String getIsleadVip() {
            return isleadVip;
        }

        public void setIsleadVip(String isleadVip) {
            this.isleadVip = isleadVip;
        }

        public String getIsleadNotVip() {
            return isleadNotVip;
        }

        public void setIsleadNotVip(String isleadNotVip) {
            this.isleadNotVip = isleadNotVip;
        }

        public int getNoPreachTime() {
            return noPreachTime;
        }

        public void setNoPreachTime(int noPreachTime) {
            this.noPreachTime = noPreachTime;
        }

        public int getRepeatPointFlag() {
            return repeatPointFlag;
        }

        public void setRepeatPointFlag(int repeatPointFlag) {
            this.repeatPointFlag = repeatPointFlag;
        }

        public String getNotPointMsg() {
            return notPointMsg;
        }

        public void setNotPointMsg(String notPointMsg) {
            this.notPointMsg = notPointMsg;
        }

        public int getHalfwayTime() {
            return halfwayTime;
        }

        public void setHalfwayTime(int halfwayTime) {
            this.halfwayTime = halfwayTime;
        }

        public String getHalfwayOtherMsg() {
            return halfwayOtherMsg;
        }

        public void setHalfwayOtherMsg(String halfwayOtherMsg) {
            this.halfwayOtherMsg = halfwayOtherMsg;
        }
    }

    public class DataBean {
        private String errorCode;
        private String errorMsg;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }
    }


    /**
     * ============================以下是v4.0添加================================
     */
    private List<String> sounds;
    private List<CarouselItemsBean> carouselItems;
    private List<String> functionManages;
    private List<String> statusBars;
    private RobotInfoBean robotInfo;
    private int expressionGroupId;
    private List<String> isContent;//肯定库
    private List<String> noContent;//否定库
    private List<String> wholeContent;//一体库

    public List<String> getNoContent() {
        return noContent;
    }

    public void setNoContent(List<String> noContent) {
        this.noContent = noContent;
    }

    public List<String> getIsContent() {
        return isContent;
    }

    public void setIsContent(List<String> isContent) {
        this.isContent = isContent;
    }

    public List<String> getWholeContent() {
        return wholeContent;
    }

    public void setWholeContent(List<String> wholeContent) {
        this.wholeContent = wholeContent;
    }

    public int getExpressionGroupId() {
        return expressionGroupId;
    }

    public void setExpressionGroupId(int expressionGroupId) {
        this.expressionGroupId = expressionGroupId;
    }

    public List<String> getSounds() {
        return sounds;
    }

    public void setSounds(List<String> sounds) {
        this.sounds = sounds;
    }

    public List<CarouselItemsBean> getCarouselItems() {
        return carouselItems;
    }

    public void setCarouselItems(List<CarouselItemsBean> carouselItems) {
        this.carouselItems = carouselItems;
    }

    public List<String> getFunctionManages() {
        return functionManages;
    }

    public void setFunctionManages(List<String> functionManages) {
        this.functionManages = functionManages;
    }

    public static class CarouselItemsBean {
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public List<String> getStatusBars() {
        return statusBars;
    }

    public void setStatusBars(List<String> statusBars) {
        this.statusBars = statusBars;
    }

    public RobotInfoBean getRobotInfo() {
        return robotInfo;
    }

    public void setRobotInfo(RobotInfoBean robotInfo) {
        this.robotInfo = robotInfo;
    }

    public static class RobotInfoBean {
        /**
         * robName : 小笨
         * timbreId : 0
         * wakeFlag : 1
         * moveMsg : 请先关闭急停按钮
         * thirtyDownMsg : 小笨电量不足请及时充电
         * fifteenDownMsg : 小笨要回充电桩充电了
         * spotVip : 1
         * welcomeStatus : 2
         * moreVipWelcomeContent : 欢迎各位嘉宾
         * vipContent : 欢迎尊敬的#姓氏##全名#你好
         * strangerContent : #性别#你好
         * moreVipContent : 尊敬的VIP你们好
         * moreStrangerContent : 先生女士们好
         * lastPower : 15
         * warnPower : 30
         * nowakeContent : 真正的智能机器人#机器人名称#即将为你服务
         * arousalWords : 你好·小笨
         */

        private String robName;
        private int timbreId;
        private int wakeFlag;
        private String moveMsg;
        private String thirtyDownMsg;
        private String fifteenDownMsg;
        private int spotVip;
        private int welcomeStatus;
        private String moreVipWelcomeContent;
        private String vipContent;
        private String strangerContent;
        private String moreVipContent;
        private String moreStrangerContent;
        private int lastPower;
        private int warnPower;
        private String nowakeContent;
        private String arousalWords;

        public String getRobName() {
            return robName;
        }

        public void setRobName(String robName) {
            this.robName = robName;
        }

        public int getTimbreId() {
            return timbreId;
        }

        public void setTimbreId(int timbreId) {
            this.timbreId = timbreId;
        }

        public int getWakeFlag() {
            return wakeFlag;
        }

        public void setWakeFlag(int wakeFlag) {
            this.wakeFlag = wakeFlag;
        }

        public String getMoveMsg() {
            return moveMsg;
        }

        public void setMoveMsg(String moveMsg) {
            this.moveMsg = moveMsg;
        }

        public String getThirtyDownMsg() {
            return thirtyDownMsg;
        }

        public void setThirtyDownMsg(String thirtyDownMsg) {
            this.thirtyDownMsg = thirtyDownMsg;
        }

        public String getFifteenDownMsg() {
            return fifteenDownMsg;
        }

        public void setFifteenDownMsg(String fifteenDownMsg) {
            this.fifteenDownMsg = fifteenDownMsg;
        }

        public int getSpotVip() {
            return spotVip;
        }

        public void setSpotVip(int spotVip) {
            this.spotVip = spotVip;
        }

        public int getWelcomeStatus() {
            return welcomeStatus;
        }

        public void setWelcomeStatus(int welcomeStatus) {
            this.welcomeStatus = welcomeStatus;
        }

        public String getMoreVipWelcomeContent() {
            return moreVipWelcomeContent;
        }

        public void setMoreVipWelcomeContent(String moreVipWelcomeContent) {
            this.moreVipWelcomeContent = moreVipWelcomeContent;
        }

        public String getVipContent() {
            return vipContent;
        }

        public void setVipContent(String vipContent) {
            this.vipContent = vipContent;
        }

        public String getStrangerContent() {
            return strangerContent;
        }

        public void setStrangerContent(String strangerContent) {
            this.strangerContent = strangerContent;
        }

        public String getMoreVipContent() {
            return moreVipContent;
        }

        public void setMoreVipContent(String moreVipContent) {
            this.moreVipContent = moreVipContent;
        }

        public String getMoreStrangerContent() {
            return moreStrangerContent;
        }

        public void setMoreStrangerContent(String moreStrangerContent) {
            this.moreStrangerContent = moreStrangerContent;
        }

        public int getLastPower() {
            return lastPower;
        }

        public void setLastPower(int lastPower) {
            this.lastPower = lastPower;
        }

        public int getWarnPower() {
            return warnPower;
        }

        public void setWarnPower(int warnPower) {
            this.warnPower = warnPower;
        }

        public String getNowakeContent() {
            return nowakeContent;
        }

        public void setNowakeContent(String nowakeContent) {
            this.nowakeContent = nowakeContent;
        }

        public String getArousalWords() {
            return arousalWords;
        }

        public void setArousalWords(String arousalWords) {
            this.arousalWords = arousalWords;
        }
    }

    public boolean isSpeakPower30() {
        return isSpeakPower30;
    }

    public void setSpeakPower30(boolean speakPower30) {
        isSpeakPower30 = speakPower30;
    }

    public boolean isSpeakPower15() {
        return isSpeakPower15;
    }

    public void setSpeakPower15(boolean speakPower15) {
        isSpeakPower15 = speakPower15;
    }
}
