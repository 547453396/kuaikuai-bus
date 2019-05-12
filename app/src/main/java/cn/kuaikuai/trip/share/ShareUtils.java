package cn.kuaikuai.trip.share;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;

import cn.kuaikuai.trip.constant.ShareConstant;
import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.share.shareprocessor.ShareProcessorTemplate;
import cn.kuaikuai.trip.utils.SdcardManager;
import cn.kuaikuai.trip.utils.UtilsManager;
import cn.kuaikuai.common.LogUtils;

/**
 * Created by liuyc on 2014/9/12.
 * 分享的公共操作方法
 */
public class ShareUtils {

    static ShareUtils INSTANCE;

    private Map<String, ShareProcessorTemplate> map = new Hashtable<>();
    public Tencent mTencent;
    public Handler handler;
    /**
     * 正常渠道微信id
     */
    public int market_channel = 0;
    public String WECHAT_APP_ID, desc = "", trans = "", content;
    public Context ctx;
    public Activity mActivity;
    public IWXAPI api;
    boolean isPreparing;
    /**
     * google market渠道微信id
     */
    public String mContentId, mContentTitle, mContentBody, mContentImg, mContentUrl, oneMsg;
    public int imgId;
    public String WXMiniProgramPath;

    public String bigImg;
    public String localImgPath, netImgUrl;
    /**
     * 用于微信分享的回调
     */
    public static boolean isWeixinShareSuccess = false;
    public static boolean isWeixinShareCancel = false;
    public static boolean isWeixinShareFailed = false;

    /**
     * 外部调用分享时传入的参数，用于传递给微信分享回调(逻辑在DealIntentActivity中)
     */
    public static String mch_id = "", appid = "";

    public int WXMiniImgId;//微信小程序分享的本地图片id

    public static void setOutShareParas(String mch_id, String appid) {
        ShareUtils.mch_id = mch_id;
        ShareUtils.appid = appid;
    }

    /**
     * 外部scheme拦截url 分享时发送广播
     *
     * @param type 0：成功 1：取消 2：失败
     */
    public static void sendBroadCast4OutShare(Context context, int type) {
        if (!TextUtils.isEmpty(mch_id) && !TextUtils.isEmpty(appid)) {
            Intent intent = new Intent();
            if (type == 0) {
                LogUtils.i("share--->_zhwnl_share_succ");
                intent.setAction(mch_id + "_" + appid + "_zhwnl_share_succ");
            } else if (type == 1) {
                LogUtils.i("share--->_zhwnl_share_cancel");
                intent.setAction(mch_id + "_" + appid + "_zhwnl_share_cancel");
            } else {
                LogUtils.i("share--->_zhwnl_share_fail");
                intent.setAction(mch_id + "_" + appid + "_zhwnl_share_fail");
            }
            context.sendBroadcast(intent);
            mch_id = "";
            appid = "";
        }
    }

    public boolean isAd, isSNS, isJilu;

    public void resetValue() {
        isPreparing = false;
        mContentId = "";
        mContentTitle = "";
        mContentBody = "";
        mContentImg = "";
        mContentUrl = "";
        localImgPath = "";
        netImgUrl = "";
        localImgPath = "";
        netImgUrl = "";
        oneMsg = "";
        imgId = 0;
        isAd = false;
        isSNS = false;
        isJilu = false;
        WXMiniImgId = 0;
    }

    public static ShareUtils getShareUtils(Activity act) {
        if (null == INSTANCE) {
            INSTANCE = new ShareUtils();
            INSTANCE.ctx = act.getApplicationContext();
            INSTANCE.WECHAT_APP_ID = ShareConstant.WECHAT_APP_ID;
            INSTANCE.api = WXAPIFactory.createWXAPI(INSTANCE.ctx, INSTANCE.WECHAT_APP_ID, true);
            // 将该app注册到微信
            INSTANCE.api.registerApp(INSTANCE.WECHAT_APP_ID);
            INSTANCE.mTencent = Tencent.createInstance(ShareConstant.QQ_APP_ID, INSTANCE.ctx);
        }
        INSTANCE.mActivity = act;
        return INSTANCE;
    }


    public static void clearInstance() {
        if (INSTANCE != null) {
            INSTANCE.mActivity = null;
        }
        INSTANCE = null;
    }


    /**
     * 准备去分享的内容，比如图片要上传成网络的地址
     *
     * @param shareProcessor
     */
    public void preparaingForShare(ShareProcessorTemplate shareProcessor) {
        try {
            SdcardManager.copyIconToTheDir(INSTANCE.ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (shareProcessor.shareModel == ShareModel.Type_WX_MINI) {//小程序的分享
            int img_id = WXMiniImgId;
            if (img_id == 0) {//没有设置小程序分享的id就用其他分享的图
                img_id = imgId;
            }
            if (img_id != 0) {//有设置的本地图片id就以本地的为准了
                copyImgResToTheDir(img_id);
                mContentImg = WlConfig.tempDir + "share.jpg";
            }
        } else if (shareProcessor.shareModel == ShareModel.TYPE_IMG) {//其他分享的类型不变
            if (!TextUtils.isEmpty(bigImg)) {
                mContentImg = bigImg;
            } else {
                if (imgId != 0) {//有设置的本地图片id就以本地的为准了
                    copyImgResToTheDir(imgId);
                    mContentImg = WlConfig.tempDir + "share.jpg";
                }
            }
        } else {
            if (imgId != 0) {//有设置的本地图片id就以本地的为准了
                copyImgResToTheDir(imgId);
                mContentImg = WlConfig.tempDir + "share.jpg";
            }
        }
        if (TextUtils.isEmpty(mContentUrl) && !TextUtils.isEmpty(mContentId)) {
            isPreparing = true;
//            getShareUrl(ctx, mContentId);
        }
        if (shareProcessor.needNetImgOrLocalImg == 1) {
            netImgUrl = getShareNetImgUrl();
        } else if (shareProcessor.needNetImgOrLocalImg == 2) {
            localImgPath = getShareLocalImgPath();
        } else if (shareProcessor.needNetImgOrLocalImg == 3) {
            netImgUrl = getShareNetImgUrl();
            localImgPath = getShareLocalImgPath();
        } else {
            //不含图片的分享 就把现有的img传进去 不做处理
            if (mContentImg.startsWith("http://") || mContentImg.startsWith("https://")) {
                netImgUrl = mContentImg;
            } else {
                localImgPath = mContentImg;
            }
        }
    }

    /**
     * 将分享的资源图拷贝到临时文件
     *
     * @param imgId
     */
    private void copyImgResToTheDir(int imgId) {
        File file = new File(WlConfig.tempDir + "share.jpg");
        if (!SdcardManager.isSDCardAvailable()) {
            return;
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            is = ctx.getResources().openRawResource(imgId);

            fos = new FileOutputStream(file);

            byte[] buffer = new byte[8192];
            int count = 0;

            while ((count = is.read(buffer)) > 0) {
                fos.write(buffer, 0, count);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        localImgPath = WlConfig.tempDir + "share.jpg";
    }

    public void setShareNetImg(String netImgUrl) {
        this.netImgUrl = netImgUrl;
    }

    /**
     * 初始化分享内容
     *
     * @param contentTitle 分享的title
     * @param contentBody  记录的内容
     * @param contentImg   图片地址,
     * @param contentUrl   分享的url地址
     */
    public void setShareContent(String contentTitle, String contentBody, String contentImg, String contentUrl) {
        isPreparing = true;

        if (TextUtils.isEmpty(contentBody)) {
            this.mContentBody = "";
        } else {
            this.mContentBody = contentBody;
        }
        if (TextUtils.isEmpty(contentTitle)) {
            this.mContentTitle = "";
        } else {
            this.mContentTitle = contentTitle;
        }
        this.mContentImg = contentImg;
        this.mContentUrl = contentUrl;
        isPreparing = false;
    }

    /**
     * @param contentId 数据的contentId
     */
    public void setContentId(String contentId) {
        this.mContentId = contentId;
    }


    public String getShareLocalImgPath() {
        if (!TextUtils.isEmpty(localImgPath)) {
            return localImgPath;
        } else {
            if (TextUtils.isEmpty(mContentImg)) {
                mContentImg = WlConfig.SHARE_URL;
            }
            if (mContentImg.startsWith("http://")) {
                localImgPath = getLocalImgPath();
            } else {
                localImgPath = mContentImg;
            }
            return localImgPath;
        }
    }

    /**
     * 获取图片网络地址：
     * 如果地址是网络地址则直接返回，如果是本地地址则上传后返回网址
     */
    public String getShareNetImgUrl() {
        if (!TextUtils.isEmpty(netImgUrl)) {
            return netImgUrl;
        } else {
            if (mContentImg.startsWith("http://")) {
                netImgUrl = mContentImg;
            } else {
                netImgUrl = getNetImgUrl();
            }
            return netImgUrl;

        }
    }

    /**
     * 上传一张图片并返回上传后的网址
     */
    private String getNetImgUrl() {
        String output;
        if (!TextUtils.isEmpty(mContentImg)) {
            output = mContentImg;
        } else {
            output = WlConfig.SHARE_URL;
        }
        return output;
    }

    private String getLocalImgPath() {
        String path = WlConfig.tempDir;
        File _file = new File(path);
        if (!_file.exists()) {
            _file.mkdirs();
        }
        File tmp = new File(WlConfig.tempDir, "share.jpg");
        if (tmp.exists()) {
            tmp.delete();
        }
        try {
            tmp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fos = null;
        try {
            // 打开一个已存在文件的输出流
            fos = new FileOutputStream(tmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream is = null;
        try {
            is = new URL(mContentImg).openStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 将输入流is写入文件输出流fos中
        int ch = 0;
        try {
            if (is != null && fos != null) {
                while ((ch = is.read()) != -1) {
                    fos.write(ch);
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return WlConfig.tempDir + "share.jpg";
    }

    private String getLastContent(String preStr, String url) {
        return preStr + "\n" + url;
    }


    public void addShareMethod(String type, ShareProcessorTemplate processorTemplate) {
        map.put(type, processorTemplate);

    }

    /**
     * 替换指定平台的分享方法。若没有指定平台的分享方法，则添加。
     *
     * @param platform 平台名称。例：SharePresenter.ZONE_SHARE_TYPE
     * @param method   需要的分享方法。
     */
    public void putShareMethod(String platform, ShareProcessorTemplate method) {
        map.put(platform, method);
    }

    public ShareProcessorTemplate getShareMethod(String platform) {
        return map.get(platform);
    }

    public void doShare(String platform) {
        ShareProcessorTemplate processor = map.get(platform);
        if (processor != null) {
            processor.execute();
        }

    }

    public void forceStopShare() {
        for (ShareProcessorTemplate template : map.values()) {
            if (template != null && template.currentThread != null && template.currentThread.isAlive()) {
                template.currentThread.shutDownThread();
            }
        }

    }

    public void clearShareMethod() {
        map.clear();
    }

    /**
     * 发送Text 到 QQ
     */
    public static void sysShareText2QQ(Activity activity, String desc) {
        try {
            String packageName = "com.tencent.mobileqq";
            if (UtilsManager.isAppInstalled(activity.getApplicationContext(), packageName)) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, "com.tencent.mobileqq.activity.JumpActivity"));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, desc);
                activity.startActivity(intent);
            } else {
                UtilsManager.Toast(activity.getApplicationContext(), "QQ未安装");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用系统分享发送文字。
     *
     * @param activity
     * @param desc
     */
    public static void systemShareText(Activity activity, String desc) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, desc);
        intent.setType("text/plain");
        ComponentName componentName = intent.resolveActivity(activity.getPackageManager());
        if (componentName != null) {
            activity.startActivity(intent);
        } else {
            UtilsManager.Toast(activity.getApplicationContext(), "没有可分享的应用");
        }
    }

}
