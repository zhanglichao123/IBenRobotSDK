package com.samton.IBenRobotSDK.data;

import java.util.List;

/**
 * <pre>
 *     author : syk
 *     e-mail : shenyukun1024@gmail.com
 *     time   : 2017/05/04
 *     desc   : 小笨回答数据模型
 *     version: 1.0
 * </pre>
 */

public final class MessageBean {
    /**
     * commandFlag : 本轮对话是否结束(1结束,0未结束)
     * data : {"appMessage":[{"answerType":-1,"informationJson":{"current":{"city":"北京","date":"2017-07-06","iname":"空气污染扩散指数","index":"优","temp":"25","temphigh":"25","templow":"23","weather":"大雨","week":"星期四","winddirect":"东风","windpower":"3级"},"daily":[{"date":"2017-07-06","day":{"img":"9","temphigh":"25","weather":"大雨","winddirect":"东风","windpower":"微风"},"night":{"img":"8","templow":"23","weather":"中雨","winddirect":"北风","windpower":"微风"},"sunrise":"04:54","sunset":"19:45","week":"星期四"},{"date":"2017-07-07","day":{"img":"2","temphigh":"31","weather":"阴","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"23","weather":"多云","winddirect":"北风","windpower":"微风"},"sunrise":"04:54","sunset":"19:44","week":"星期五"},{"date":"2017-07-08","day":{"img":"1","temphigh":"34","weather":"多云","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"25","weather":"多云","winddirect":"北风","windpower":"微风"},"sunrise":"04:55","sunset":"19:44","week":"星期六"},{"date":"2017-07-09","day":{"img":"1","temphigh":"34","weather":"多云","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"26","weather":"多云","winddirect":"南风","windpower":"微风"},"sunrise":"04:56","sunset":"19:44","week":"星期日"},{"date":"2017-07-10","day":{"img":"2","temphigh":"33","weather":"阴","winddirect":"南风","windpower":"微风"},"night":{"img":"2","templow":"25","weather":"阴","winddirect":"北风","windpower":"微风"},"sunrise":"04:56","sunset":"19:43","week":"星期一"},{"date":"2017-07-11","day":{"img":"1","temphigh":"36","weather":"多云","winddirect":"东南风","windpower":"微风"},"night":{"img":"0","templow":"24","weather":"晴","winddirect":"东南风","windpower":"微风"},"sunrise":"07:30","sunset":"19:30","week":"星期二"},{"date":"2017-07-12","day":{"img":"1","temphigh":"36","weather":"多云","winddirect":"","windpower":"微风"},"night":{"img":"1","templow":"25","weather":"多云","winddirect":"","windpower":"微风"},"sunrise":"07:30","sunset":"19:30","week":"星期三"}]},"isAnswer":true,"message":"北京今天23℃~25℃ 裤。"}],"code":"10010","info":"消息发送成功","time":"2017-07-06 10:48:48"}
     */

    private int commandFlag;

    private DataBean data;

    public int getCommandFlag() {
        return commandFlag;
    }

    public void setCommandFlag(int commandFlag) {
        this.commandFlag = commandFlag;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * appMessage : [{"answerType":-1,"informationJson":{"current":{"city":"北京","date":"2017-07-06","iname":"空气污染扩散指数","index":"优","temp":"25","temphigh":"25","templow":"23","weather":"大雨","week":"星期四","winddirect":"东风","windpower":"3级"},"daily":[{"date":"2017-07-06","day":{"img":"9","temphigh":"25","weather":"大雨","winddirect":"东风","windpower":"微风"},"night":{"img":"8","templow":"23","weather":"中雨","winddirect":"北风","windpower":"微风"},"sunrise":"04:54","sunset":"19:45","week":"星期四"},{"date":"2017-07-07","day":{"img":"2","temphigh":"31","weather":"阴","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"23","weather":"多云","winddirect":"北风","windpower":"微风"},"sunrise":"04:54","sunset":"19:44","week":"星期五"},{"date":"2017-07-08","day":{"img":"1","temphigh":"34","weather":"多云","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"25","weather":"多云","winddirect":"北风","windpower":"微风"},"sunrise":"04:55","sunset":"19:44","week":"星期六"},{"date":"2017-07-09","day":{"img":"1","temphigh":"34","weather":"多云","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"26","weather":"多云","winddirect":"南风","windpower":"微风"},"sunrise":"04:56","sunset":"19:44","week":"星期日"},{"date":"2017-07-10","day":{"img":"2","temphigh":"33","weather":"阴","winddirect":"南风","windpower":"微风"},"night":{"img":"2","templow":"25","weather":"阴","winddirect":"北风","windpower":"微风"},"sunrise":"04:56","sunset":"19:43","week":"星期一"},{"date":"2017-07-11","day":{"img":"1","temphigh":"36","weather":"多云","winddirect":"东南风","windpower":"微风"},"night":{"img":"0","templow":"24","weather":"晴","winddirect":"东南风","windpower":"微风"},"sunrise":"07:30","sunset":"19:30","week":"星期二"},{"date":"2017-07-12","day":{"img":"1","temphigh":"36","weather":"多云","winddirect":"","windpower":"微风"},"night":{"img":"1","templow":"25","weather":"多云","winddirect":"","windpower":"微风"},"sunrise":"07:30","sunset":"19:30","week":"星期三"}]},"isAnswer":true,"message":"北京今天23℃~25℃ 裤。"}]
         * code : 10010
         * info : 消息发送成功
         * time : 2017-07-06 10:48:48
         */

        private String code;
        private String info;
        private String time;
        private List<AppMessageBean> appMessage;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public List<AppMessageBean> getAppMessage() {
            return appMessage;
        }

        public void setAppMessage(List<AppMessageBean> appMessage) {
            this.appMessage = appMessage;
        }

        public static class AppMessageBean {
            /**
             * answerType : -1
             * informationJson : {"current":{"city":"北京","date":"2017-07-06","iname":"空气污染扩散指数","index":"优","temp":"25","temphigh":"25","templow":"23","weather":"大雨","week":"星期四","winddirect":"东风","windpower":"3级"},"daily":[{"date":"2017-07-06","day":{"img":"9","temphigh":"25","weather":"大雨","winddirect":"东风","windpower":"微风"},"night":{"img":"8","templow":"23","weather":"中雨","winddirect":"北风","windpower":"微风"},"sunrise":"04:54","sunset":"19:45","week":"星期四"},{"date":"2017-07-07","day":{"img":"2","temphigh":"31","weather":"阴","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"23","weather":"多云","winddirect":"北风","windpower":"微风"},"sunrise":"04:54","sunset":"19:44","week":"星期五"},{"date":"2017-07-08","day":{"img":"1","temphigh":"34","weather":"多云","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"25","weather":"多云","winddirect":"北风","windpower":"微风"},"sunrise":"04:55","sunset":"19:44","week":"星期六"},{"date":"2017-07-09","day":{"img":"1","temphigh":"34","weather":"多云","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"26","weather":"多云","winddirect":"南风","windpower":"微风"},"sunrise":"04:56","sunset":"19:44","week":"星期日"},{"date":"2017-07-10","day":{"img":"2","temphigh":"33","weather":"阴","winddirect":"南风","windpower":"微风"},"night":{"img":"2","templow":"25","weather":"阴","winddirect":"北风","windpower":"微风"},"sunrise":"04:56","sunset":"19:43","week":"星期一"},{"date":"2017-07-11","day":{"img":"1","temphigh":"36","weather":"多云","winddirect":"东南风","windpower":"微风"},"night":{"img":"0","templow":"24","weather":"晴","winddirect":"东南风","windpower":"微风"},"sunrise":"07:30","sunset":"19:30","week":"星期二"},{"date":"2017-07-12","day":{"img":"1","temphigh":"36","weather":"多云","winddirect":"","windpower":"微风"},"night":{"img":"1","templow":"25","weather":"多云","winddirect":"","windpower":"微风"},"sunrise":"07:30","sunset":"19:30","week":"星期三"}]}
             * isAnswer : true
             * message : 北京今天23℃~25℃ 裤。
             * TODO 以下新增
             * relationId : 关联问题ID
             * relationName : 关联问题
             * relationWords : 第几轮问答标识
             * relationWords : 关联问题播报语
             * readFaceCommand : 消息关联的表情
             * armSportCommand : 消息关联的手臂动作
             * HeadSportCommandBean : 消息关联的头部动作
             * backSportCommand : 消息关联的身体动作
             */
            private int answerType;
            private InformationJsonBean informationJson;
            private boolean isAnswer;
            private String message;
            private int flowFlag;
            private int categoryId = -1;
            private String imgDesc;//图片的描述
            private Pointlist pointlist;

            private int relationId;
            private String relationName;
            private String relationIndex;
            private String relationWords;
            private int readFaceCommand;
            private ArmSportCommandBean armSportCommand;
            private HeadSportCommandBean headSportCommand;
            private BackSportCommandBean backSportCommand;

            public String getRelationIndex() {
                return relationIndex;
            }

            public void setRelationIndex(String relationIndex) {
                this.relationIndex = relationIndex;
            }

            public String getRelationWords() {
                return relationWords;
            }

            public void setRelationWords(String relationWords) {
                this.relationWords = relationWords;
            }

            public int getRelationId() {
                return relationId;
            }

            public void setRelationId(int relationId) {
                this.relationId = relationId;
            }

            public String getRelationName() {
                return relationName;
            }

            public void setRelationName(String relationName) {
                this.relationName = relationName;
            }

            public int getReadFaceCommand() {
                return readFaceCommand;
            }

            public void setReadFaceCommand(int readFaceCommand) {
                this.readFaceCommand = readFaceCommand;
            }

            public Pointlist getPointlist() {
                return pointlist;
            }

            public void setPointlist(Pointlist pointlist) {
                this.pointlist = pointlist;
            }

            public String getImgDesc() {
                return imgDesc;
            }

            public void setImgDesc(String imgDesc) {
                this.imgDesc = imgDesc;
            }

            public int getCategoryId() {
                return categoryId;
            }

            public void setCategoryId(int categoryId) {
                this.categoryId = categoryId;
            }

            public boolean isAnswer() {
                return isAnswer;
            }

            public void setAnswer(boolean answer) {
                isAnswer = answer;
            }

            public int getFlowFlag() {
                return flowFlag;
            }

            public void setFlowFlag(int flowFlag) {
                this.flowFlag = flowFlag;
            }

            public int getAnswerType() {
                return answerType;
            }

            public void setAnswerType(int answerType) {
                this.answerType = answerType;
            }

            public InformationJsonBean getInformationJson() {
                return informationJson;
            }

            public void setInformationJson(InformationJsonBean informationJson) {
                this.informationJson = informationJson;
            }

            public boolean isIsAnswer() {
                return isAnswer;
            }

            public void setIsAnswer(boolean isAnswer) {
                this.isAnswer = isAnswer;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public ArmSportCommandBean getArmSportCommand() {
                return armSportCommand;
            }

            public void setArmSportCommand(ArmSportCommandBean armSportCommand) {
                this.armSportCommand = armSportCommand;
            }

            public HeadSportCommandBean getHeadSportCommand() {
                return headSportCommand;
            }

            public void setHeadSportCommand(HeadSportCommandBean headSportCommand) {
                this.headSportCommand = headSportCommand;
            }

            public BackSportCommandBean getBackSportCommand() {
                return backSportCommand;
            }

            public void setBackSportCommand(BackSportCommandBean backSportCommand) {
                this.backSportCommand = backSportCommand;
            }

            public static class InformationJsonBean {
                /**
                 * current : {"city":"北京","date":"2017-07-06","iname":"空气污染扩散指数","index":"优","temp":"25","temphigh":"25","templow":"23","weather":"大雨","week":"星期四","winddirect":"东风","windpower":"3级"}
                 * daily : [{"date":"2017-07-06","day":{"img":"9","temphigh":"25","weather":"大雨","winddirect":"东风","windpower":"微风"},"night":{"img":"8","templow":"23","weather":"中雨","winddirect":"北风","windpower":"微风"},"sunrise":"04:54","sunset":"19:45","week":"星期四"},{"date":"2017-07-07","day":{"img":"2","temphigh":"31","weather":"阴","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"23","weather":"多云","winddirect":"北风","windpower":"微风"},"sunrise":"04:54","sunset":"19:44","week":"星期五"},{"date":"2017-07-08","day":{"img":"1","temphigh":"34","weather":"多云","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"25","weather":"多云","winddirect":"北风","windpower":"微风"},"sunrise":"04:55","sunset":"19:44","week":"星期六"},{"date":"2017-07-09","day":{"img":"1","temphigh":"34","weather":"多云","winddirect":"南风","windpower":"微风"},"night":{"img":"1","templow":"26","weather":"多云","winddirect":"南风","windpower":"微风"},"sunrise":"04:56","sunset":"19:44","week":"星期日"},{"date":"2017-07-10","day":{"img":"2","temphigh":"33","weather":"阴","winddirect":"南风","windpower":"微风"},"night":{"img":"2","templow":"25","weather":"阴","winddirect":"北风","windpower":"微风"},"sunrise":"04:56","sunset":"19:43","week":"星期一"},{"date":"2017-07-11","day":{"img":"1","temphigh":"36","weather":"多云","winddirect":"东南风","windpower":"微风"},"night":{"img":"0","templow":"24","weather":"晴","winddirect":"东南风","windpower":"微风"},"sunrise":"07:30","sunset":"19:30","week":"星期二"},{"date":"2017-07-12","day":{"img":"1","temphigh":"36","weather":"多云","winddirect":"","windpower":"微风"},"night":{"img":"1","templow":"25","weather":"多云","winddirect":"","windpower":"微风"},"sunrise":"07:30","sunset":"19:30","week":"星期三"}]
                 */

                private CurrentBean current;
                private List<DailyBean> daily;

                public CurrentBean getCurrent() {
                    return current;
                }

                public void setCurrent(CurrentBean current) {
                    this.current = current;
                }

                public List<DailyBean> getDaily() {
                    return daily;
                }

                public void setDaily(List<DailyBean> daily) {
                    this.daily = daily;
                }

                public static class CurrentBean {
                    /**
                     * city : 北京
                     * date : 2017-07-06
                     * iname : 空气污染扩散指数
                     * index : 优
                     * temp : 25
                     * temphigh : 25
                     * templow : 23
                     * weather : 大雨
                     * week : 星期四
                     * winddirect : 东风
                     * windpower : 3级
                     */

                    private String city;
                    private String date;
                    private String iname;
                    private String index;
                    private String temp;
                    private String temphigh;
                    private String templow;
                    private String weather;
                    private String week;
                    private String winddirect;
                    private String windpower;

                    public String getCity() {
                        return city;
                    }

                    public void setCity(String city) {
                        this.city = city;
                    }

                    public String getDate() {
                        return date;
                    }

                    public void setDate(String date) {
                        this.date = date;
                    }

                    public String getIname() {
                        return iname;
                    }

                    public void setIname(String iname) {
                        this.iname = iname;
                    }

                    public String getIndex() {
                        return index;
                    }

                    public void setIndex(String index) {
                        this.index = index;
                    }

                    public String getTemp() {
                        return temp;
                    }

                    public void setTemp(String temp) {
                        this.temp = temp;
                    }

                    public String getTemphigh() {
                        return temphigh;
                    }

                    public void setTemphigh(String temphigh) {
                        this.temphigh = temphigh;
                    }

                    public String getTemplow() {
                        return templow;
                    }

                    public void setTemplow(String templow) {
                        this.templow = templow;
                    }

                    public String getWeather() {
                        return weather;
                    }

                    public void setWeather(String weather) {
                        this.weather = weather;
                    }

                    public String getWeek() {
                        return week;
                    }

                    public void setWeek(String week) {
                        this.week = week;
                    }

                    public String getWinddirect() {
                        return winddirect;
                    }

                    public void setWinddirect(String winddirect) {
                        this.winddirect = winddirect;
                    }

                    public String getWindpower() {
                        return windpower;
                    }

                    public void setWindpower(String windpower) {
                        this.windpower = windpower;
                    }
                }

                public static class DailyBean {
                    /**
                     * date : 2017-07-06
                     * day : {"img":"9","temphigh":"25","weather":"大雨","winddirect":"东风","windpower":"微风"}
                     * night : {"img":"8","templow":"23","weather":"中雨","winddirect":"北风","windpower":"微风"}
                     * sunrise : 04:54
                     * sunset : 19:45
                     * week : 星期四
                     */

                    private String date;
                    private DayBean day;
                    private NightBean night;
                    private String sunrise;
                    private String sunset;
                    private String week;

                    public String getDate() {
                        return date;
                    }

                    public void setDate(String date) {
                        this.date = date;
                    }

                    public DayBean getDay() {
                        return day;
                    }

                    public void setDay(DayBean day) {
                        this.day = day;
                    }

                    public NightBean getNight() {
                        return night;
                    }

                    public void setNight(NightBean night) {
                        this.night = night;
                    }

                    public String getSunrise() {
                        return sunrise;
                    }

                    public void setSunrise(String sunrise) {
                        this.sunrise = sunrise;
                    }

                    public String getSunset() {
                        return sunset;
                    }

                    public void setSunset(String sunset) {
                        this.sunset = sunset;
                    }

                    public String getWeek() {
                        return week;
                    }

                    public void setWeek(String week) {
                        this.week = week;
                    }

                    public static class DayBean {
                        /**
                         * img : 9
                         * temphigh : 25
                         * weather : 大雨
                         * winddirect : 东风
                         * windpower : 微风
                         */

                        private String img;
                        private String temphigh;
                        private String weather;
                        private String winddirect;
                        private String windpower;

                        public String getImg() {
                            return img;
                        }

                        public void setImg(String img) {
                            this.img = img;
                        }

                        public String getTemphigh() {
                            return temphigh;
                        }

                        public void setTemphigh(String temphigh) {
                            this.temphigh = temphigh;
                        }

                        public String getWeather() {
                            return weather;
                        }

                        public void setWeather(String weather) {
                            this.weather = weather;
                        }

                        public String getWinddirect() {
                            return winddirect;
                        }

                        public void setWinddirect(String winddirect) {
                            this.winddirect = winddirect;
                        }

                        public String getWindpower() {
                            return windpower;
                        }

                        public void setWindpower(String windpower) {
                            this.windpower = windpower;
                        }
                    }

                    public static class NightBean {
                        /**
                         * img : 8
                         * templow : 23
                         * weather : 中雨
                         * winddirect : 北风
                         * windpower : 微风
                         */

                        private String img;
                        private String templow;
                        private String weather;
                        private String winddirect;
                        private String windpower;

                        public String getImg() {
                            return img;
                        }

                        public void setImg(String img) {
                            this.img = img;
                        }

                        public String getTemplow() {
                            return templow;
                        }

                        public void setTemplow(String templow) {
                            this.templow = templow;
                        }

                        public String getWeather() {
                            return weather;
                        }

                        public void setWeather(String weather) {
                            this.weather = weather;
                        }

                        public String getWinddirect() {
                            return winddirect;
                        }

                        public void setWinddirect(String winddirect) {
                            this.winddirect = winddirect;
                        }

                        public String getWindpower() {
                            return windpower;
                        }

                        public void setWindpower(String windpower) {
                            this.windpower = windpower;
                        }
                    }
                }
            }

            public static class Pointlist {
                private String sceneName;
                private String slocLocation;
                private String slocName;
                private String pointContent;
                private int sceneId;
                private String failureContent;
                private int slocId;
                private String robUuid;
                private String wayContent;

                public String getSceneName() {
                    return sceneName;
                }

                public void setSceneName(String sceneName) {
                    this.sceneName = sceneName;
                }

                public String getSlocLocation() {
                    return slocLocation;
                }

                public void setSlocLocation(String slocLocation) {
                    this.slocLocation = slocLocation;
                }

                public String getSlocName() {
                    return slocName;
                }

                public void setSlocName(String slocName) {
                    this.slocName = slocName;
                }

                public String getPointContent() {
                    return pointContent;
                }

                public void setPointContent(String pointContent) {
                    this.pointContent = pointContent;
                }

                public int getSceneId() {
                    return sceneId;
                }

                public void setSceneId(int sceneId) {
                    this.sceneId = sceneId;
                }

                public String getFailureContent() {
                    return failureContent;
                }

                public void setFailureContent(String failureContent) {
                    this.failureContent = failureContent;
                }

                public int getSlocId() {
                    return slocId;
                }

                public void setSlocId(int slocId) {
                    this.slocId = slocId;
                }

                public String getRobUuid() {
                    return robUuid;
                }

                public void setRobUuid(String robUuid) {
                    this.robUuid = robUuid;
                }

                public String getWayContent() {
                    return wayContent;
                }

                public void setWayContent(String wayContent) {
                    this.wayContent = wayContent;
                }
            }

            public static class ArmSportCommandBean {
                /**
                 * armOneCommand : 1代2代
                 * armThreeCommand : 3代
                 */

                private int armOneCommand;
                private int armThreeCommand;

                public int getArmOneCommand() {
                    return armOneCommand;
                }

                public void setArmOneCommand(int armOneCommand) {
                    this.armOneCommand = armOneCommand;
                }

                public int getArmThreeCommand() {
                    return armThreeCommand;
                }

                public void setArmThreeCommand(int armThreeCommand) {
                    this.armThreeCommand = armThreeCommand;
                }
            }

            public static class HeadSportCommandBean {
                /**
                 * headOneCommand : 1代2代
                 * headTwoCommand : 3代
                 */

                private int headOneCommand;
                private int headThreeCommand;

                public int getHeadOneCommand() {
                    return headOneCommand;
                }

                public void setHeadOneCommand(int headOneCommand) {
                    this.headOneCommand = headOneCommand;
                }

                public int getHeadThreeCommand() {
                    return headThreeCommand;
                }

                public void setHeadThreeCommand(int headThreeCommand) {
                    this.headThreeCommand = headThreeCommand;
                }
            }

            public static class BackSportCommandBean {
                /**
                 * backOneCommand : 1代2代
                 * baclTwoCommand : 3代
                 */

                private int backOneCommand;
                private int backThreeCommand;

                public int getBackOneCommand() {
                    return backOneCommand;
                }

                public void setBackOneCommand(int backOneCommand) {
                    this.backOneCommand = backOneCommand;
                }

                public int getBackThreeCommand() {
                    return backThreeCommand;
                }

                public void setBackThreeCommand(int backThreeCommand) {
                    this.backThreeCommand = backThreeCommand;
                }
            }
        }
    }
}
