package cn.kuaikuai.trip.model.bean.login;

public class LoginBean {
    /**
     * status : 1000
     * desc :
     * data : {"uid":1,"alias":"303ff933a7844f4abc75af45edad237b","acctk":"0.0769454723567875:1.1541555525680","open_id":"6a0d9fb2810e86f0"}
     */

    private int status;
    private String desc;
    private DataBean data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * uid : 1
         * alias : 303ff933a7844f4abc75af45edad237b
         * acctk : 0.0769454723567875:1.1541555525680
         * open_id : 6a0d9fb2810e86f0
         */

        private long uid;
        private String alias;
        private String acctk;
        private String open_id;
        private boolean new_user;

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getAcctk() {
            return acctk;
        }

        public void setAcctk(String acctk) {
            this.acctk = acctk;
        }

        public String getOpen_id() {
            return open_id;
        }

        public void setOpen_id(String open_id) {
            this.open_id = open_id;
        }

        public boolean isNew_user() {
            return new_user;
        }

        public void setNew_user(boolean new_user) {
            this.new_user = new_user;
        }
    }

    public static class IMBean {
        public String accid;
        public String token;
    }
}
