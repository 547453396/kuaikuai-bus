package cn.kuaikuai.trip.main.activity;


import android.app.Activity;
import android.text.TextUtils;

import cn.kuaikuai.trip.share.ShareModel;
import cn.kuaikuai.trip.share.SharePopWindow;

/**
 * JXH
 * 2018/8/7
 */
public class SharePresenter {
    private SharePopWindow mSharePopWindow;

    public SharePresenter(Activity activity) {
        mSharePopWindow = new SharePopWindow(activity);
        mSharePopWindow.setShareContent("", "", "", "");
        mSharePopWindow.setIsUGCShare(false);
        mSharePopWindow.show_init();
        mSharePopWindow.dismiss();
    }

    public void setShareListener(SharePopWindow.ShareListener listener) {
        if (mSharePopWindow != null) {
            mSharePopWindow.setShareListener(listener);
        }
    }

    public void share(String shareChannel, String title, String desc, String img, String url) {
        mSharePopWindow.setShareContent(title, desc, img, url);
        String shareType = null;
        if (!TextUtils.isEmpty(shareChannel)) {
            switch (shareChannel) {
                case "wx":
                    shareType = ShareModel.WX_SHARE_TYPE;
                    break;
                case "pyq":
                    shareType = ShareModel.PYQ_SHARE_TYPE;
                    break;
                case "qq":
                    shareType = ShareModel.QQ_SHARE_TYPE;
                    break;
                case "qzone":
                    shareType = ShareModel.ZONE_SHARE_TYPE;
                    break;
                case "weibo":
                    shareType = ShareModel.WEIBO_SHARE_TYPE;
                    break;
                default:
                    break;
            }
        }
        if (TextUtils.isEmpty(shareType)) {
            if (mSharePopWindow != null) {
                mSharePopWindow.show();
            }
        } else {
            SharePopWindow.shareUtils.doShare(shareChannel);
        }
    }
}