package cn.kuaikuai.trip.share.shareprocessor;

import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.share.ShareChannel;
import cn.kuaikuai.trip.share.ShareUtils;
import cn.kuaikuai.trip.utils.UtilsManager;

/**
 * Created by liuyc on 2014/9/19.
 * qq好友的分享在手机和电脑上的体验效果很不一样,这与mcontentUrl有关，手Q的分享效果达到预期（包含了图片  title  和描述，点击可以跳转到特定Url），Pc版    分享结果只有一个链接
 */
public class QQPyShare extends ShareProcessorTemplate {

    public QQPyShare(ShareUtils shareUtils, int shareModel) {
        super(shareUtils, shareModel);
        needNetImgOrLocalImg = 1;
    }


    @Override
    public boolean isLegal() {
        //mTencent != null && mTencent.isSupportSSOLogin(mActivity)
        //通过包名判断是否安装qq
        if (UtilsManager.isQQClientAvailable(mActivity)) {
            return true;
        } else {
            onFail(404);
            return false;
        }
    }

    public void shareImage2QQ() {
        if (mActivity == null) {
            return;
        }
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, mContentTile);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, mContentBody);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareUtils.getShareNetImgUrl());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, mContentUrl);
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mActivity.getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mTencent == null) {
                    return;
                }
                mTencent.shareToQQ(mActivity, params, new IUiListener() {

                    @Override
                    public void onCancel() {
                        onFail(0);
                    }

                    @Override
                    public void onError(UiError e) {
                        onFail(2);
                    }

                    @Override
                    public void onComplete(Object response) {
                        onSuccess(ShareChannel.TX_WB);
                    }

                });
            }
        });
    }

    @Override
    public void doShare() {
        if (TextUtils.isEmpty(mContentUrl)) {
            mContentUrl = WlConfig.APK_DOWN_URL;
        }
        shareImage2QQ();
    }


    @Override
    public void onSuccess(String share_channel) {
        super.onSuccess(share_channel);
        cb.onSuccess();
    }

    @Override
    public void onFail(int resultCode) {
        if (resultCode == 404) {
            cb.onToastMessage("系统未安装QQ客户端");
        } else {
            cb.onFail(resultCode, "");
        }
    }
}
