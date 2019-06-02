package cn.kuaikuai.trip.main.line.model;

import android.content.Context;

import java.util.Map;

import cn.kuaikuai.base.model.BaseModel;
import cn.kuaikuai.common.net.api.ApiManage;
import cn.kuaikuai.trip.model.bean.path.LineListBean;
import cn.kuaikuai.trip.model.bean.path.PathInfoBean;
import cn.kuaikuai.trip.net.ApiInterface;
import rx.Observer;

public class PathModel extends BaseModel {
    public PathModel(Context context) {
        super(context);
    }

    public void addLinePath(Map<String, Object> parameters,String body, Observer<PathInfoBean> observer) {
        subscribe(ApiManage.getInstance().body(ApiInterface.LinePathApi.ROUTE_ADD, body,parameters, PathInfoBean.class), observer);
    }

    public void getAllLinePath(Map<String, Object> parameters,String body, Observer<LineListBean> observer) {
        subscribe(ApiManage.getInstance().body(ApiInterface.LinePathApi.LINE_ALL, body,parameters, LineListBean.class), observer);
    }
}
