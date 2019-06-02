package cn.kuaikuai.trip.main.line.view;

import cn.kuaikuai.trip.model.bean.path.LineListBean;

public interface IPathView {
    void onAddSucceed();

    void onAddFailed(String msg);

    void onAllPathSucceed(LineListBean.DataEntity data);

    void onAllPathFailed(String msg);
}
