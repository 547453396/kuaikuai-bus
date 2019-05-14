package cn.kuaikuai.trip.model.bean.login;

public class LoginBean {

    /**
     * msg : ok
     * code : 200
     * data : {"career":null,"signature":null,"nickName":null,"sex":null,"headUrl":null,"updateTime":null,"type":null,"token":"74587f0e252c4656849b9f6da7b3896d","mobilePhone":"15996367215","createTime":null,"id":100003,"age":null,"status":"open"}
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
         * career : null
         * signature : null
         * nickName : null
         * sex : null
         * headUrl : null
         * updateTime : null
         * type : null
         * token : 74587f0e252c4656849b9f6da7b3896d
         * mobilePhone : 15996367215
         * createTime : null
         * id : 100003
         * age : null
         * status : open
         */
        private String career;
        private String signature;
        private String nickName;
        private String sex;
        private String headUrl;
        private String updateTime;
        private String type;
        private String token;
        private String mobilePhone;
        private String createTime;
        private int id;
        private String age;
        private String status;

        public void setCareer(String career) {
            this.career = career;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public void setHeadUrl(String headUrl) {
            this.headUrl = headUrl;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public void setMobilePhone(String mobilePhone) {
            this.mobilePhone = mobilePhone;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public void setId(int id) {
            this.id = id;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCareer() {
            return career;
        }

        public String getSignature() {
            return signature;
        }

        public String getNickName() {
            return nickName;
        }

        public String getSex() {
            return sex;
        }

        public String getHeadUrl() {
            return headUrl;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public String getType() {
            return type;
        }

        public String getToken() {
            return token;
        }

        public String getMobilePhone() {
            return mobilePhone;
        }

        public String getCreateTime() {
            return createTime;
        }

        public int getId() {
            return id;
        }

        public String getAge() {
            return age;
        }

        public String getStatus() {
            return status;
        }
    }
}
