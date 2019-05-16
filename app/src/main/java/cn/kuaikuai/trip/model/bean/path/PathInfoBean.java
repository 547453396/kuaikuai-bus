package cn.kuaikuai.trip.model.bean.path;

import java.util.List;

public class PathInfoBean {

    /**
     * msg : ok
     * code : 200
     * data : {"seatNum":3,"createTime":null,"lineId":4,"seatList":[{"routeId":100097,"createTime":null,"updateTime":null,"id":100363,"type":"A1","desc":"副驾驶","status":"open"},{"routeId":100097,"createTime":null,"updateTime":null,"id":100364,"type":"B1","desc":"后排1","status":"open"},{"routeId":100097,"createTime":null,"updateTime":null,"id":100365,"type":"B2","desc":"后排2","status":"open"}],"startTime":"2019-05-15 23:00:00","updateTime":null,"id":100097,"vehicleId":100001,"userId":100001,"status":"open"}
     */
    private String msg;
    private String code;
    private DataEntity data;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public String getCode() {
        return code;
    }

    public DataEntity getData() {
        return data;
    }

    public class DataEntity {
        /**
         * seatNum : 3
         * createTime : null
         * lineId : 4
         * seatList : [{"routeId":100097,"createTime":null,"updateTime":null,"id":100363,"type":"A1","desc":"副驾驶","status":"open"},{"routeId":100097,"createTime":null,"updateTime":null,"id":100364,"type":"B1","desc":"后排1","status":"open"},{"routeId":100097,"createTime":null,"updateTime":null,"id":100365,"type":"B2","desc":"后排2","status":"open"}]
         * startTime : 2019-05-15 23:00:00
         * updateTime : null
         * id : 100097
         * vehicleId : 100001
         * userId : 100001
         * status : open
         */
        private int seatNum;
        private String createTime;
        private int lineId;
        private List<SeatListEntity> seatList;
        private String startTime;
        private String updateTime;
        private int id;
        private int vehicleId;
        private int userId;
        private String status;

        public void setSeatNum(int seatNum) {
            this.seatNum = seatNum;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setLineId(int lineId) {
            this.lineId = lineId;
        }

        public void setSeatList(List<SeatListEntity> seatList) {
            this.seatList = seatList;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setVehicleId(int vehicleId) {
            this.vehicleId = vehicleId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getSeatNum() {
            return seatNum;
        }

        public String getCreateTime() {
            return createTime;
        }

        public int getLineId() {
            return lineId;
        }

        public List<SeatListEntity> getSeatList() {
            return seatList;
        }

        public String getStartTime() {
            return startTime;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public int getId() {
            return id;
        }

        public int getVehicleId() {
            return vehicleId;
        }

        public int getUserId() {
            return userId;
        }

        public String getStatus() {
            return status;
        }

        public class SeatListEntity {
            /**
             * routeId : 100097
             * createTime : null
             * updateTime : null
             * id : 100363
             * type : A1
             * desc : 副驾驶
             * status : open
             */
            private int routeId;
            private String createTime;
            private String updateTime;
            private int id;
            private String type;
            private String desc;
            private String status;

            public void setRouteId(int routeId) {
                this.routeId = routeId;
            }

            public void setCreateTime(String createTime) {
                this.createTime = createTime;
            }

            public void setUpdateTime(String updateTime) {
                this.updateTime = updateTime;
            }

            public void setId(int id) {
                this.id = id;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setDesc(String desc) {
                this.desc = desc;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public int getRouteId() {
                return routeId;
            }

            public String getCreateTime() {
                return createTime;
            }

            public String getUpdateTime() {
                return updateTime;
            }

            public int getId() {
                return id;
            }

            public String getType() {
                return type;
            }

            public String getDesc() {
                return desc;
            }

            public String getStatus() {
                return status;
            }
        }
    }
}
