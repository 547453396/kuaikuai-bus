package cn.kuaikuai.trip.share.shareprocessor;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;

import java.io.File;

import cn.kuaikuai.trip.MainApplication;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.share.ShareChannel;
import cn.kuaikuai.trip.share.ShareUtils;
import cn.kuaikuai.trip.utils.CompressPicture;


/**
 * Created by liuyc on 2014/9/19.
 * 分享图片到微信
 */
public class WeiXingImgShare extends ShareProcessorTemplate {

    private static final int THUMB_SIZE = 100;

    public WeiXingImgShare(int WXshareType, ShareUtils shareUtils, int shareModel) {
        super(shareUtils, shareModel);
        this.WXshareType = WXshareType;
        needNetImgOrLocalImg = 2;
    }


    @Override
    public boolean isLegal() {
        if (api.isWXAppInstalled()) {
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
//        if (WXshareType == 0) {
//            shareToWeChat();
//        } else {
        shareImageToWeChat4WebView(shareUtils.getShareLocalImgPath(), WXshareType);
//        }
    }

    /**
     * 分享到微信朋友
     */
    private void shareToWeChat() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setAction(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareScreenImgUI");
            intent.setComponent(cmp);
            String path = shareUtils.getShareLocalImgPath();
            if (!TextUtils.isEmpty(path) && !path.startsWith("file://")) {
                path = "file://" + path;
            }
            Uri photoOutputUri = FileProvider.getUriForFile(
                    MainApplication.getApplication(),
                    mActivity.getPackageName() + ".fileProvider",
                    new File(path));
            intent.setDataAndType(photoOutputUri, "image/jpeg");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            mActivity.startActivity(intent);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 分享图片到微信
     *
     * @param localPath 图片本地地址 toWhere 0:朋友，1：朋友圈
     * @return
     */
    private void shareImageToWeChat4WebView(String localPath, int toWhere) {
        File file = new File(localPath);
        File parentFile = file.getParentFile();
        if (parentFile == null) {
            return;
        }
        if (!parentFile.exists()) {
            file.mkdirs();
        }
        if (!file.exists()) {
            onFail(500);
            return;
        }
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(localPath);

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;

        CompressPicture cp = new CompressPicture();
        //计算图片的宽高比
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        int m = 1, n = 1;
        try {
            if (options.outHeight > options.outWidth) {
                m = options.outHeight / options.outWidth;
            } else {
                n = options.outWidth / options.outHeight;
            }
        } catch (Exception e) {
            m = 1;
            n = 1;
        }
        Bitmap thumbBmp = cp.extractThumbNail(localPath, THUMB_SIZE * m, THUMB_SIZE * n, true);
        msg.thumbData = cp.bmpToByteArray(thumbBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.message = msg;
        req.scene = toWhere;

        req.transaction = buildTransaction("img");
        Boolean b = api.sendReq(req);
        if (!b) {
            onFail(100);
        } else {
            onSuccess(ShareChannel.WEIXIN);
        }

    }


    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    @Override
    public void onSuccess(String share_channel) {
        super.onSuccess(share_channel);
    }

    @Override
    public void onFail(int resultCode) {
        if (resultCode == 404 && cb != null) {
            cb.onFail(404, mActivity.getString(R.string.WXNotInstalled));
        }
    }

}
