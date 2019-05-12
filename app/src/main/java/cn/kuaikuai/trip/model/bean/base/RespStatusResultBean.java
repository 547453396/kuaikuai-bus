package cn.kuaikuai.trip.model.bean.base;

import org.json.JSONObject;

public class RespStatusResultBean {
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

    public void json2Bean(JSONObject object) {
        if (object == null) {
            return;
        }
        try {
            status = object.optInt("status");
            desc = object.optString("desc");
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        return "status:" + status + "  desc" + desc;
    }
}
