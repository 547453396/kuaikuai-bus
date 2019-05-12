package cn.kuaikuai.trip.share.shareprocessor;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.Tencent;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.share.ShareUtils;
import cn.kuaikuai.common.LogUtils;


/**
 * Created by liuyc on 2014/9/19.
 */
public abstract class ShareProcessorTemplate {

    public WorkThread currentThread;
    public Activity mActivity;
    public String mContentId, mContentBody, mContentUrl, mContentTile, oneMsg;
    public String WXMiniProgramPath;
    public ShareCallBack cb;
    public IWXAPI api;
    ShareUtils shareUtils;
    public Handler handler;
    public Tencent mTencent = null;
    //分享逻辑需要的是本地imgpath 还是网络img url 1:netImg 2:localImg  3:both 4：none
    public int needNetImgOrLocalImg = 0;
    //WeiXingShare WeiXingImgShare 共用分享的类型，01是之前的，并且是不能改的，0.微信好有，1朋友圈
    // 因为在设置分享的方式的时候传入到了微信sdk的方法中
    // 2是新增的分享小程序
    public int WXshareType;
    public int shareModel;//标记分享所用的图片,TYPE_URL_AND_IMG:小图标，TYPE_IMG:大图，Type_WX_MINI:微信小程序的图标

    public ShareProcessorTemplate setShareCallback(ShareCallBack cb) {
        this.cb = cb;
        return this;
    }

    protected ShareProcessorTemplate(ShareUtils shareUtils, int shareModel) {
        this.shareUtils = shareUtils;
        this.shareModel = shareModel;
    }


    public abstract boolean isLegal();

    /**
     * share的入口
     */
    public void execute() {
        currentThread = new WorkThread() {
            @Override
            public void run() {
                super.run();
                if (shareUtils == null || shareUtils.mActivity == null) {
                    return;
                }
                if (null != cb) {
                    cb.onStartShare();
                }
                shareUtils.preparaingForShare(ShareProcessorTemplate.this);
                initDate(shareUtils);

                if (currentThread.isShutDown()) {
                    LogUtils.i("force stop share");
                    return;
                }

                if (mActivity == null || mActivity.isFinishing()) {
                    return;
                }
                if (!isLegal()) {
                    return;
                }
                doShare();

            }
        };
        currentThread.start();

    }

    public abstract void doShare();

    /**
     * @param share_channel Sysparams.ShareChannel...
     */
    public void onSuccess(final String share_channel) {
//        new Thread(new Runnable() {//尽量保证每次都正确返回统计数据
//            @Override
//            public void run() {
//                String postId = "";
//                if (mContentUrl == null) {
//                    mContentUrl = "";
//                }
//                int i = mContentUrl.lastIndexOf("/");
//                if (mContentUrl.length() >= i + 1) {
//                    postId = mContentUrl.substring(i + 1);
//                }
//            }
//        }).start();

    }

    public abstract void onFail(int resultCode);

    private ShareProcessorTemplate initDate(ShareUtils orgin) {
        api = orgin.api;
        mActivity = orgin.mActivity;
        mContentBody = orgin.mContentBody;
        mContentId = orgin.mContentId;
        mContentUrl = orgin.mContentUrl;
        WXMiniProgramPath = orgin.WXMiniProgramPath;
        if (TextUtils.isEmpty(orgin.mContentTitle)) {
            mContentTile = mActivity.getResources().getString(R.string.app_name);
        } else {
            mContentTile = orgin.mContentTitle;
        }
        this.mTencent = orgin.mTencent;
        this.handler = orgin.handler;
        this.oneMsg = orgin.oneMsg;
        return this;
    }

    @Override
    public String toString() {
        return "ShareProcessorTemplate{" +
                "mContentId='" + mContentId + '\'' +
                ", mContentBody='" + mContentBody + '\'' +
                ", mContentUrl='" + mContentUrl + '\'' +
                ", mContentTile='" + mContentTile + '\'' +
                ", oneMsg='" + oneMsg + '\'' +
                '}';
    }

    public class WorkThread extends Thread {
        private boolean flag = false;

        public synchronized void shutDownThread() {
            flag = true;
        }

        public synchronized boolean isShutDown() {
            return flag;
        }
    }

    ;
}
