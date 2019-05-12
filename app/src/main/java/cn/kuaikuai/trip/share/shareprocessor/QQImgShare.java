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
 * qq好友和qq空间分享图片
 */
public class QQImgShare extends ShareProcessorTemplate {

    public int sharetype = 0; //0:qq朋友分享  1：qq空间分享

    public QQImgShare(int sharetype, ShareUtils shareUtils, int shareModel) {
        super(shareUtils, shareModel);
        this.sharetype = sharetype;
        needNetImgOrLocalImg = 2;
    }


    @Override
    public boolean isLegal() {
        if (UtilsManager.isQQClientAvailable(mActivity)) {
            return true;
        } else {
            onFail(404);
            return false;
        }
    }

    public void shareImage2QQ() {
        final Bundle params = new Bundle();
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, shareUtils.getShareLocalImgPath());
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, mActivity.getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_IMAGE);
        if (sharetype == 0) {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
        } else {
            params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onFail(0);
            }
        }, 1000);
        handler.post(new Runnable() {
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
                        if (sharetype == 0) {
                            onSuccess(ShareChannel.TX_WB);
                        } else {
                            onSuccess(ShareChannel.QZONE);
                        }
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
