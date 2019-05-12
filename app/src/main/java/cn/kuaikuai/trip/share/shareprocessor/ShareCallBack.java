package cn.kuaikuai.trip.share.shareprocessor;

/**
 * Created by admin on 2014/9/19.
 */
public abstract class ShareCallBack {

    public abstract void onStartShare();

    public abstract void onSuccess();

    public abstract void onFail(int resultCode, String msg);

    public abstract void onToastMessage(String msg);

}
