package cn.kuaikuai.trip.share;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.ShareConstant;
import cn.kuaikuai.trip.utils.UtilsManager;

public class WeiXinShareUtil {
    /**
     * 微信是否安装
     */
    public static boolean isWeixinAvilible(Context context) {
        try {
            //微信授权
            String WECHAT_APP_ID = ShareConstant.WECHAT_APP_ID;
            // 通过WXAPIFactory工厂，获取IWXAPI的实例
            IWXAPI api = WXAPIFactory.createWXAPI(context, WECHAT_APP_ID, true);
            return api.isWXAppInstalled();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 微信分享到好友(单张图片及描述)
     */
    public static void shareToWeiXinFriend(Context context, File file, String description) {
        try {
            if (!isWeixinAvilible(context)) {
                UtilsManager.Toast(context, context.getString(R.string.WXNotInstalled));
                return;
            }
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, description);
            if (file != null) {
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            } else {
                intent.setType("text/*");
            }
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 微信分享到好友(多张图片及描述)
     */
    public static void shareMultiplePictureToFriend(Context context, List<File> files, String description) {
        try {
            if (!isWeixinAvilible(context)) {
                UtilsManager.Toast(context, context.getString(R.string.WXNotInstalled));
                return;
            }
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            if (files != null) {
                intent.setType("image/*");
                ArrayList<Uri> imageUris = new ArrayList<>();
                for (File f : files) {
                    imageUris.add(Uri.fromFile(f));
                }
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            } else {
                intent.setType("text/*");
            }
            intent.putExtra(Intent.EXTRA_TEXT, description);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 微信分享到朋友圈(单张图片及描述)
     */
    public static void shareToWeiXinTimeLine(Context context, File file, String description) {
        try {
            if (!isWeixinAvilible(context)) {
                UtilsManager.Toast(context, context.getString(R.string.WXNotInstalled));
                return;
            }
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND);
            if (file != null && file.exists()) {
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            } else {
                intent.setType("text/*");
            }
            intent.putExtra(Intent.EXTRA_TEXT, description);
            intent.putExtra("Kdescription", description);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 微信分享到朋友圈(多张图片及描述)
     */
    public static void shareMultiplePictureToTimeLine(Context context, List<File> files, String description) {
        try {
            if (!isWeixinAvilible(context)) {
                UtilsManager.Toast(context, context.getString(R.string.WXNotInstalled));
                return;
            }
            Intent intent = new Intent();
            ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
            intent.setComponent(comp);
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            if (files != null) {
                intent.setType("image/*");
                ArrayList<Uri> imageUris = new ArrayList<>();
                for (File f : files) {
                    imageUris.add(Uri.fromFile(f));
                }
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
            } else {
                intent.setType("text/*");
            }
            intent.putExtra(Intent.EXTRA_TEXT, description);
            intent.putExtra("Kdescription", description);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

