package cn.kuaikuai.trip.model.bean.login;

public class VerifyCodeBean {
    /**
     * status : 1000
     * desc : OK
     */
    private int status;
    private String desc;

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
}
