package cn.kuaikuai.trip.share.shareprocessor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;

import java.io.File;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.share.ShareChannel;
import cn.kuaikuai.trip.share.ShareUtils;
import cn.kuaikuai.trip.utils.CompressPicture;


/**
 * Created by liuyc on 2014/9/19.
 * 分享文字、链接到微信好友、微信朋友圈
 */
public class WeiXingShare extends ShareProcessorTemplate {

    private static final int THUMB_SIZE = 100;
    private final static String WX_MINI_PROGRAM_ID = "gh_48a1ccd56943";//微信小程序的ID

    public static final int WX_SHARE_TYPE_MINI = 2;

    public WeiXingShare(int WXshareType, ShareUtils shareUtils, int shareModel) {
        super(shareUtils, shareModel);
        this.WXshareType = WXshareType;
        needNetImgOrLocalImg = 4;
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
        /**2016-04-05 by LJG,分享微信链接有图片则分享图片链接，无图默认使用icon*/
        if (WXshareType == WX_SHARE_TYPE_MINI) {
            shareWXMiniProgram(shareUtils.getShareLocalImgPath());
        } else {
            shareImageToWeChat4WebView(shareUtils.getShareLocalImgPath(), WXshareType);
        }
    }


    /***
     * Edit by LJG 2016-04-29 需求修改为分享到朋友圈如果描述不为空则使用描述
     */
    public void adjust4pyq(WXMediaMessage msg) {
        if (!TextUtils.isEmpty(oneMsg)) {
            msg.title = oneMsg;
        } else if (!TextUtils.isEmpty(msg.description)) {
            msg.title = msg.description;
        }
    }

    /**
     * 分享链接到微信
     *
     * @param localPath 图片本地地址 toWhere 0:朋友，1：朋友圈
     *                  当图片为空或者图片不存在时则使用万年历的icon作为图标
     */
    public void shareImageToWeChat4WebView(String localPath, int toWhere) {
        Bitmap theImage = null;
        CompressPicture cp = new CompressPicture();
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists()) {
                theImage = cp.extractThumbNail(localPath, THUMB_SIZE, THUMB_SIZE, true);//缩略图要小于32K
            }
        }
        if (theImage == null || theImage.isRecycled()) {
            theImage = BitmapFactory.decodeResource(mActivity.getResources(), R.drawable.share);
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mContentUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = mContentTile;//mContentBody.length() > 15 ? mContentBody.substring(0, 14) + "..." :
        if (!TextUtils.isEmpty(mContentBody)) {
            msg.description = mContentBody.length() > 80 ? mContentBody.substring(0, 79) : mContentBody;
        }
        if (toWhere == 1) {
            adjust4pyq(msg);
        }
        msg.thumbData = cp.bmpToByteArray(theImage, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.scene = toWhere;
        req.message = msg;
        req.transaction = buildTransaction("webpage");
        boolean b = api.sendReq(req);
        if (!b) {
            onFail(100);
        } else {
            onSuccess(ShareChannel.WEIXIN);
        }
    }

    /**
     * 分享中华万年历微信小程序
     * 图片以及标题内容都和之前分享的方式一样
     */
    private void shareWXMiniProgram(String localPath) {
        WXMiniProgramObject miniProgram = new WXMiniProgramObject();
        miniProgram.webpageUrl = mContentUrl;
        //跳转小程序的原始 ID，这个应该是不变，
        miniProgram.userName = WX_MINI_PROGRAM_ID;
        //小程序的path ，这个应该是可以变化的
        if (!TextUtils.isEmpty(WXMiniProgramPath)) {
            miniProgram.path = WXMiniProgramPath;
        } else {
            miniProgram.path = "pages/index/index";//默认的主界面路径，不传应该也是到主页
        }
        WXMediaMessage msg = new WXMediaMessage(miniProgram);
        msg.title = mContentTile;
        if (!TextUtils.isEmpty(mContentBody)) {
            msg.description = mContentBody.length() > 80 ? mContentBody.substring(0, 79) : mContentBody;
        }
//        Bitmap bmp = BitmapFactory.decodeResource(mActivity.getResources(), shareUtils.WXMiniImgId);
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 420, 336, true);
//        bmp.recycle();
        Bitmap theImage = null;
        CompressPicture cp = new CompressPicture();
        if (!TextUtils.isEmpty(localPath)) {
            File file = new File(localPath);
            if (file.exists()) {//这里的宽高传的是图片的尺寸
                theImage = cp.extractThumbNail(localPath, 420, 336, false);//缩略图要小于32K
            }
        }
        if (theImage == null || theImage.isRecycled()) {
            int image_id = shareUtils.WXMiniImgId;
            if (image_id == 0) {
                image_id = R.drawable.icon_share;
            }
            theImage = BitmapFactory.decodeResource(mActivity.getResources(), image_id);
        }
        msg.thumbData = cp.bmpToByteArray(theImage, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneTimeline;
        boolean b = api.sendReq(req);
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
