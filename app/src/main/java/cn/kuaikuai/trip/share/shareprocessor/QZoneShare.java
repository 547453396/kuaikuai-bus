package cn.kuaikuai.trip.share.shareprocessor;

import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.share.ShareChannel;
import cn.kuaikuai.trip.share.ShareUtils;
import cn.kuaikuai.trip.utils.UtilsManager;

/**
 * Created by liuyc on 2014/9/19.
 */
public class QZoneShare extends ShareProcessorTemplate {

    public QZoneShare(ShareUtils shareUtils, int shareModel) {
        super(shareUtils, shareModel);
        needNetImgOrLocalImg = 1;

    }

    @Override
    public boolean isLegal() {
        if (UtilsManager.isQQClientAvailable(mActivity)) {
            if (TextUtils.isEmpty(mContentTile) && TextUtils.isEmpty(mContentBody)) {
                onFail(2);
                return false;
            }
            return true;
        } else {
            onFail(404);
            return false;
        }
    }

    @Override
    public void doShare() {

        if (TextUtils.isEmpty(mContentUrl)) {
            mContentUrl = WlConfig.APK_DOWN_URL;
        }
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        if (TextUtils.isEmpty(mContentTile)) {
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mContentBody);//必填
        } else {
            params.putString(QzoneShare.SHARE_TO_QQ_TITLE, mContentTile);//必填
            params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, mContentBody);//选填
        }

        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, mContentUrl);//必填


        String img_Url = shareUtils.getShareNetImgUrl();
        ArrayList<String> imageUrls = new ArrayList<String>();
        if (!TextUtils.isEmpty(img_Url)) {
            imageUrls.add(img_Url);
        }
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imageUrls);

        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTencent.shareToQzone(mActivity, params, new IUiListener() {

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
                        onSuccess(ShareChannel.QZONE);
                    }

                });
            }
        });

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
