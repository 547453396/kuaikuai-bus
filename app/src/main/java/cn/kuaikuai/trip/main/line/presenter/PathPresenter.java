package cn.kuaikuai.trip.main.line.presenter;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.main.line.model.PathModel;
import cn.kuaikuai.trip.main.line.view.IPathView;
import cn.kuaikuai.trip.model.bean.path.LineListBean;
import cn.kuaikuai.trip.model.bean.path.PathInfoBean;
import rx.Observer;

public class PathPresenter {
    private Context ctx;
    private IPathView pathView;
    private PathModel pathModel;

    public PathPresenter(Context context, IPathView pathView) {
        ctx = context;
        this.pathView = pathView;
        pathModel = new PathModel(context);
    }

    public void addLinePath(String body) {
        HashMap<String, Object> table = new HashMap<>();
        pathModel.addLinePath(table, body, new Observer<PathInfoBean>() {
            PathInfoBean bean;

            @Override
            public void onCompleted() {
                if (pathView != null) {
                    if (bean != null){
                        if (bean.getCode().equals("200")){
                            pathView.onAddSucceed();
                        }else {
                            if (TextUtils.isEmpty(bean.getMsg())){
                                pathView.onAddFailed(ctx.getString(R.string.server_error));
                            }else {
                                pathView.onAddFailed(bean.getMsg());
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (pathView != null) {
                    pathView.onAddFailed(ctx.getString(R.string.server_error));
                }
            }

            @Override
            public void onNext(PathInfoBean bean) {
                this.bean = bean;
            }
        });
    }

    public void getAllLinePath(int page){
        JSONObject body = new JSONObject();
        try {
            body.put("pageNum",page);
            body.put("pageSize",10);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HashMap<String, Object> table = new HashMap<>();
        pathModel.getAllLinePath(table, body.toString(), new Observer<LineListBean>() {
            LineListBean lineListBean;
            @Override
            public void onCompleted() {
                if (pathView != null) {
                    if (lineListBean != null){
                        if (lineListBean.getCode().equals("200")){
                            if (lineListBean.getData() != null){
                                pathView.onAllPathSucceed(lineListBean.getData());
                            }else {
                                pathView.onAllPathFailed(ctx.getString(R.string.get_all_path_empty));
                            }
                        }else {
                            if (TextUtils.isEmpty(lineListBean.getMsg())){
                                pathView.onAllPathFailed(ctx.getString(R.string.server_error));
                            }else {
                                pathView.onAllPathFailed(lineListBean.getMsg());
                            }
                        }
                    }
                }
            }

            @Override
            public void onError(Throwable e) {
                if (pathView != null) {
                    pathView.onAllPathFailed(ctx.getString(R.string.server_error));
                }
            }

            @Override
            public void onNext(LineListBean lineListBean) {
                this.lineListBean = lineListBean;
            }
        });
    }
}
