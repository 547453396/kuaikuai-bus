package cn.kuaikuai.trip.share;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.Hashtable;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.WlConfig;
import cn.kuaikuai.trip.share.shareprocessor.OtherAppShare;
import cn.kuaikuai.trip.share.shareprocessor.QQImgShare;
import cn.kuaikuai.trip.share.shareprocessor.QQPyShare;
import cn.kuaikuai.trip.share.shareprocessor.QZoneShare;
import cn.kuaikuai.trip.share.shareprocessor.SMSShare;
import cn.kuaikuai.trip.share.shareprocessor.ShareCallBack;
import cn.kuaikuai.trip.share.shareprocessor.ShareProcessorTemplate;
import cn.kuaikuai.trip.share.shareprocessor.WeiBoShare;
import cn.kuaikuai.trip.share.shareprocessor.WeiXingImgShare;
import cn.kuaikuai.trip.share.shareprocessor.WeiXingShare;
import cn.kuaikuai.trip.utils.UtilsManager;

/**
 *
 * */

/**
 * @author lyc
 * @desc 从下面 弹出一个sharePopwondows，（包含 微信朋友分享，微信朋友圈，sina微博，qq空间，qq好友，短信，其他  共5中分享方式）
 * <p>
 * 主要调用的方法
 * 1\
 * must:  setShareContent(String  contentTitle,String contentBody, String contentImg,String  contentUrl)
 * 每次分享必须先调用
 * contentTitle  分享的title
 * contentBody 需要分享的body内容
 * contentImg 分享的图片地址  （本地或者网络地址）
 * contentUrl  分享的网络链接地址
 * <p>
 * 注：它存在一个重载方法  ：   传人的图片是图片的资源Id
 * <p>
 * <p>
 * 2\
 * opt:   turnOnUmengTrackForAd()  用于统计广告页面的分享情况
 * opt:   turnOnUmengTrackForSNS   用于统计社区帖子的分享情况
 * opt:   turnOnUmengTrackForJilu   用于统计个人记录的分享情况
 * opt：  setContentId(String contentId) 设置ugc数据的contentId(当setShareContent时候，没有设置contentUrl的时候，需要这样设置)
 * opt:   setOneMsgShareContent(String oneMsg)  由于微信朋友圈  微博分享  不分tile和body,  不设置就是由分享逻辑自己组装，否则  就使用onemsg
 * <p>
 * Attention
 * 1、禁止在setShareContent中的contentBody的值的时候，手动在后面contentBody后面append  shareUrl的地址，  需要设置分享的url地址的时候请调用  setShareWebUrl(String webUrl)
 * 2、禁止跳过SharePopWindow，直接操作shareutil和ShareProcessorTemplate的实例
 * 3、标记为must的方法必须在标记为opt的方法之前调用，因为must方法会在设置新的内容前清空所有分享内容的设置
 * 4、不要把contentTitle和contentBody传相同的数据 （万一没有contentTitle   就传“中华万年历”）
 * <p>
 * <p>
 * <p>
 * 主要使用的方式：
 * sharePopWindow = new SharePopWindow(ECalendar.this);
 * sharePopWindow.setShareContent("i am  title",
 * “ i am  body”,
 * “ i am  local Imag  location", "i am content url");
 * sharePopWindow.setConentId（”i am ugc contentId“);
 * sharePopWindow.show();
 * handler.postDelayed(new Runnable() {
 * public void run() {
 * ScreenShot.shoot(ECalendar.this);// 产生屏幕截图
 * }
 * }, 100);
 */
public class SharePopWindow extends Dialog implements OnClickListener {

    private Context ctx;
    public LinearLayout ll_copy2clip, ll_WeiXin_py, ll_WeiXin_pyq, ll_Sina, ll_Tencent, ll_QZone, ll_shareOther, ll_sms_life;
    private LinearLayout layout_main, ll_more, ll_more_content;
    private RelativeLayout linearLayout_root, rl_tips;
    private Activity mActivity;
    private Dialog pd = null;
    public static String trans;
    public static ShareUtils shareUtils;
    public TextView tv_fetch_share_share, tv_cancel;
    public Button btn_login;
    private int dataId = -1;
    private boolean bHasRegistered = false;

    /**
     * 表示是否是分享帖子和ugc内容等，用于积分任务
     */
    private boolean isShareLife = false;
    /**
     * 标示是否是网络分享（微信、朋友圈、微博、qq好友和qq空间）
     */
    private boolean isNetShare = false;

//    /**微信分享图文时候是否不要强制分享分享*/
//    private boolean isImgShare =false;

    /**
     * 是否是分享个人中心图片，是则可分享到生活圈
     */
    private boolean isUGCShare = false;
    private TextView tv_sms_life;
    private ImageView iv_sms_life;
    /**
     * 主要用于详情界面分享点击统计
     */
    private String ugc_value = "";
    //分享统计的类型及key值
    private String type, key;
    private ImageView iv_other;
    private TextView tv_other;
    private boolean isNeedShare2FishPool = false;

    /**
     * 标记分享方式的map，用这种方式方便为每个分享渠道定制不同的分享内容
     **/
    private Hashtable<String, Integer> shareMap;//HashMap<SharePresenter.ShareTypeModel,SharePresenter.shareType>

    private int ad_item_id = -1;
    private String userKey = "";
    private int headline_category_id = -1;

    public void setIsUGCShare(boolean isUGCShare) {
        this.isUGCShare = isUGCShare;
    }

    public void setShareTongjiKeyVaule(String type, String key) {
        this.type = type;
        this.key = key;
    }

    public void setAdItemId(int ad_item_id, String userKey, int headline_category_id) {
        this.ad_item_id = ad_item_id;
        this.headline_category_id = headline_category_id;
        this.userKey = userKey;
    }

    public void setUgcValue(String ugc_value) {
        this.ugc_value = ugc_value;
    }

    /**
     * only  用于帖子分享统计
     */
    public void turnOnUmengTrackForSNS() {
        shareUtils.isSNS = true;

    }

    /**
     * only  用于记录分享统计
     */
    public void turnOnUmengTrackForJilu() {
        shareUtils.isJilu = true;
    }

    /**
     * 设置纯图片分享（全渠道）
     */
    public void turnOnImgShareMode() {
        if (shareMap == null) {
            return;
        }
        shareMap.put(ShareModel.WX_SHARE_TYPE, ShareModel.TYPE_IMG);
        shareMap.put(ShareModel.PYQ_SHARE_TYPE, ShareModel.TYPE_IMG);
        shareMap.put(ShareModel.QQ_SHARE_TYPE, ShareModel.TYPE_IMG);
        shareMap.put(ShareModel.ZONE_SHARE_TYPE, ShareModel.TYPE_IMG);
        shareMap.put(ShareModel.WEIBO_SHARE_TYPE, ShareModel.TYPE_IMG);
        shareMap.put(ShareModel.LIFE_CIRCLE_SHARE_TYPE, ShareModel.TYPE_IMG);
        shareMap.put(ShareModel.COTY2Clip_SHARE_TYPE, ShareModel.TYPE_IMG);
    }

    public void setIsShareLife(boolean isShareLife) {
        this.isShareLife = isShareLife;
    }

    /**
     * 外部拦截scheme 进行分享时需要的参数
     */
    private String mch_id = "", appid = "";

    public void setOutShareParas(String mch_id, String appid) {
        this.mch_id = mch_id;
        this.appid = appid;
    }

    private String event_type = "";
    private int cid, md;

    public void setPeacockEventData(String event_type, int cid, int md) {
        this.event_type = event_type;
        this.cid = cid;
        this.md = md;
    }

    public SharePopWindow(Activity activity) {
        super(activity, R.style.Theme_Translucent);
        this.ctx = activity.getApplicationContext();
        this.mActivity = activity;
        this.setContentView(R.layout.share_popwindow);
        String path = WlConfig.tempDir;
        File _file = new File(path);
        if (!_file.exists()) {
            _file.mkdirs();
        }
        shareUtils = ShareUtils.getShareUtils(activity);
        shareUtils.clearShareMethod();
        shareUtils.handler = this.handler;
        dataId = -1;
        initView();
        initShareMap();
    }

    /**
     * 初始化分享map
     */
    private void initShareMap() {
        if (shareMap == null) {
            shareMap = new Hashtable<>();
        }
        shareMap.put(ShareModel.WX_SHARE_TYPE, ShareModel.TYPE_URL_AND_IMG);
        shareMap.put(ShareModel.PYQ_SHARE_TYPE, ShareModel.TYPE_URL_AND_IMG);
        shareMap.put(ShareModel.QQ_SHARE_TYPE, ShareModel.TYPE_URL_AND_IMG);
        shareMap.put(ShareModel.ZONE_SHARE_TYPE, ShareModel.TYPE_URL_AND_IMG);
        shareMap.put(ShareModel.WEIBO_SHARE_TYPE, ShareModel.TYPE_URL_AND_IMG);
        shareMap.put(ShareModel.LIFE_CIRCLE_SHARE_TYPE, ShareModel.TYPE_URL_AND_IMG);
        shareMap.put(ShareModel.SMS_SHARE_TYPE, ShareModel.TYPE_URL_AND_IMG);
        shareMap.put(ShareModel.COTY2Clip_SHARE_TYPE, ShareModel.TYPE_URL_AND_IMG);
    }

    /**
     * 设置分享类型，默认图标加短链，只需要传入需要更改的即可；
     *
     * @param table
     */
    public void setShareType(Hashtable<String, Integer> table) {
        if (table == null || table.size() == 0) {
            return;
        }
        if (table.containsKey(ShareModel.WX_SHARE_TYPE)) {
            shareMap.put(ShareModel.WX_SHARE_TYPE, table.get(ShareModel.WX_SHARE_TYPE));
        }
        if (table.containsKey(ShareModel.PYQ_SHARE_TYPE)) {
            shareMap.put(ShareModel.PYQ_SHARE_TYPE, table.get(ShareModel.PYQ_SHARE_TYPE));
        }
        if (table.containsKey(ShareModel.QQ_SHARE_TYPE)) {
            shareMap.put(ShareModel.QQ_SHARE_TYPE, table.get(ShareModel.QQ_SHARE_TYPE));
        }
        if (table.containsKey(ShareModel.ZONE_SHARE_TYPE)) {
            shareMap.put(ShareModel.ZONE_SHARE_TYPE, table.get(ShareModel.ZONE_SHARE_TYPE));
        }
        if (table.containsKey(ShareModel.WEIBO_SHARE_TYPE)) {
            shareMap.put(ShareModel.WEIBO_SHARE_TYPE, table.get(ShareModel.WEIBO_SHARE_TYPE));
        }
        if (table.containsKey(ShareModel.LIFE_CIRCLE_SHARE_TYPE)) {
            shareMap.put(ShareModel.LIFE_CIRCLE_SHARE_TYPE, table.get(ShareModel.LIFE_CIRCLE_SHARE_TYPE));
        }
        if (table.containsKey(ShareModel.SMS_SHARE_TYPE)) {
            shareMap.put(ShareModel.SMS_SHARE_TYPE, table.get(ShareModel.SMS_SHARE_TYPE));
        }
    }

    public void windowDeploy() {
        Window window = getWindow(); //得到对话框
        if (window != null) {
            window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
            //window.setBackgroundDrawableResource(R.color.vifrification); //设置对话框背景为透明
            WindowManager.LayoutParams wl = window.getAttributes();
            //根据x，y坐标设置窗口需要显示的位置
            wl.y = this.getWindow().getWindowManager().getDefaultDisplay().getHeight(); //y小于0上移，大于0下移
            wl.gravity = Gravity.BOTTOM; //设置重力
            window.setAttributes(wl);
        }
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;

    }
/*

    public void onEvent(DataChangedEvent event) {
        if (event.causeBy == DataChangedEvent.CauseBy.by_SynSuccess) {
            if (TextUtils.isEmpty(shareUtils.mContentId) & TextUtils.isEmpty(shareUtils.mContentUrl)) {
                shareUtils.mContentId = DBManager.open(mActivity).getSidFromEcalendarTableDataByDataId(dataId);
                showTip();
            }
            dataId = -1;
        }

    }
*/

    private void initView() {
        layout_main = (LinearLayout) findViewById(R.id.layout_main);
        ll_copy2clip = (LinearLayout) findViewById(R.id.ll_copy2clip);
        ll_WeiXin_py = (LinearLayout) findViewById(R.id.ll_wxpy);
        ll_WeiXin_pyq = (LinearLayout) findViewById(R.id.ll_wx_pyq);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        ll_Sina = (LinearLayout) findViewById(R.id.ll_sina);
        ll_Tencent = (LinearLayout) findViewById(R.id.ll_qq);
        ll_QZone = (LinearLayout) findViewById(R.id.ll_qzone);
        ll_shareOther = (LinearLayout) findViewById(R.id.ll_other);
        ll_sms_life = (LinearLayout) findViewById(R.id.ll_sms_life);
        ll_more = (LinearLayout) findViewById(R.id.ll_more);
        ll_more_content = (LinearLayout) findViewById(R.id.ll_more_content);
        ll_copy2clip.setOnClickListener(this);
        ll_WeiXin_py.setOnClickListener(this);
        ll_WeiXin_pyq.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        ll_Sina.setOnClickListener(this);
        ll_Tencent.setOnClickListener(this);
        layout_main.setOnClickListener(this);
        ll_QZone.setOnClickListener(this);
        ll_shareOther.setOnClickListener(this);
        ll_sms_life.setOnClickListener(this);
        iv_sms_life = (ImageView) findViewById(R.id.iv_sms_life);
        tv_sms_life = (TextView) findViewById(R.id.tv_sms_life);
        linearLayout_root = (RelativeLayout) findViewById(R.id.linearLayout_root);
        linearLayout_root.setOnClickListener(this);
        rl_tips = findViewById(R.id.rl_tips);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_fetch_share_share = (TextView) findViewById(R.id.tv_fetch_share_share);
        iv_other = findViewById(R.id.iv_other);
        tv_other = findViewById(R.id.tv_other);
    }

   /* private void showTip() {
        if (!TextUtils.isEmpty(shareUtils.mContentId)) {
            tv_fetch_share_share.setText(R.string.fetch_url_success);
            rl_tips.setVisibility(View.VISIBLE);
            btn_login.setVisibility(View.GONE);
        } else if (dataId != -1) {
            rl_tips.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(SuishenAccountPreferences.getInstance(ctx).getUserUid())) {
                shareUtils.mContentId = DBManager.open(mActivity).getSidFromEcalendarTableDataByDataId(dataId);
                if (!TextUtils.isEmpty(shareUtils.mContentId)) {
                    tv_fetch_share_share.setText(R.string.fetch_url_success);
                    btn_login.setVisibility(View.GONE);
                } else {
                    tv_fetch_share_share.setText(R.string.fetch_url_ing);
                    btn_login.setVisibility(View.GONE);
                }
            } else {
                tv_fetch_share_share.setText(R.string.can_not_get_shareurl);
                btn_login.setVisibility(View.VISIBLE);
                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.startActivity(new Intent(mActivity, RegistAndLoginActivity.class));
                        SharePopWindow.this.dismiss();
                    }
                });
            }
        } else {
            //不传递 shareUrl  contentId dataId 的异常情况
            rl_tips.setVisibility(View.GONE);
        }
        if (isUGCShare) {
            iv_sms_life.setImageResource(R.drawable.icon_share_post);
            tv_sms_life.setText(R.string.tool_life);
        } else {
            iv_sms_life.setImageResource(R.drawable.icon_share_message);
            tv_sms_life.setText(R.string.duanxin);
        }
    }*/

    @Override
    public void show() {
        show_init();
        if (!mActivity.isFinishing()) {
            super.show();
            windowDeploy();
            //设置触摸对话框意外的地方取消对话框
            setCanceledOnTouchOutside(true);
        }
    }

    public void show_init() {
        if (!bHasRegistered) {
            bHasRegistered = true;
        }
        if (!mActivity.isFinishing()) {
            shareUtils.mActivity = mActivity;
            initShareMethods();
//            showTip();
        }
    }

    @Override
    public void dismiss() {
        dismiss_init();
        if (isShowing()) {
            super.dismiss();
        }
    }

    public void dismiss_init() {
        if (bHasRegistered) {
            bHasRegistered = false;
        }
    }

    /**
     * 分享内容
     *
     * @param contentTitle 记录的title
     * @param contentBody  记录的内容
     * @param contentImg   图片地址,为网络地址
     * @param contentUrl   分享的webUrl地址  （可选参数，不传默认设置为  http://www.zhwnl.cn ）
     */
    public void setShareContent(String contentTitle, String contentBody, String contentImg, String contentUrl) {
        //清空shareUtil缓存的上次分享内容
        shareUtils.resetValue();
        shareUtils.setShareContent(contentTitle, contentBody, contentImg, contentUrl);
    }

    //重载上面的方法不过传入的图片是图片的资源Id
    public void setShareContent(String contentTitle, String contentBody, int imgID, String contentUrl) {
        //清空shareUtil缓存的上次分享内容
        shareUtils.resetValue();
        shareUtils.setShareContent(contentTitle, contentBody, "", contentUrl);
        shareUtils.imgId = imgID;
    }

    public void setBigShareImg(String localPath) {
        shareUtils.bigImg = localPath;
    }

    /**
     * @param contentId 传入ugc数据的contentId(对于ugc数据的分享，可以不传contentUrl，但是必须传入contentId，分享时候会通过这个contentId取到shareUrl)
     */
    public void setContentId(String contentId) {
        shareUtils.setContentId(contentId);
    }

    @Deprecated
    public void setShareWebUrl(String web) {
        shareUtils.mContentUrl = web;
    }

    /**
     * @param oneMsg 由于微信朋友圈  微博分享  不分tile和body,  oneMsg就是设置这个的内容。
     */

    public void setOneMsgShareContent(String oneMsg) {
        shareUtils.oneMsg = oneMsg;
    }

    @Deprecated
    public void setTitle(CharSequence title) {
        shareUtils.mContentTitle = title + "";
    }


    public void initShareMethods() {
        ShareCallBack _cb = new ShareCallBack() {
            @Override
            public void onStartShare() {
                handler.sendEmptyMessage(3);
            }

            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(4);
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onFail(int resultCode, String msg) {
                handler.sendEmptyMessage(4);
                if (0 == resultCode) {
                    handler.sendEmptyMessage(0);
                } else {
                    handler.sendEmptyMessage(2);
                }
            }

            @Override
            public void onToastMessage(String msg) {
                Message m = handler.obtainMessage(10);
                m.getData().putString("msg", msg);
                handler.sendMessage(m);

            }

        };
        ShareCallBack wxCb = new ShareCallBack() {
            @Override
            public void onStartShare() {
            }

            @Override
            public void onSuccess() {
                handler.sendEmptyMessage(4);
                handler.sendEmptyMessage(1);
            }

            @Override
            public void onFail(int resultCode, String msg) {
                Message failMsg = handler.obtainMessage(10);
                failMsg.getData().putString("msg", msg);
                handler.sendMessage(failMsg);
            }

            @Override
            public void onToastMessage(String msg) {

            }
        };

        //微信分享
        int type = shareMap.get(ShareModel.WX_SHARE_TYPE);
        ShareProcessorTemplate wxShare = null;
        if (type == ShareModel.Type_WX_MINI) {//分享小程序
            wxShare = new WeiXingShare(WeiXingShare.WX_SHARE_TYPE_MINI, shareUtils, type).setShareCallback(wxCb);
//            shareUtils.addShareMethod(1, wxShare);
        } else if (type == ShareModel.TYPE_IMG) {//单个大图
            wxShare = new WeiXingImgShare(0, shareUtils, type).setShareCallback(wxCb);
//            shareUtils.addShareMethod(1, wxShare);
        } else if (type == ShareModel.TYPE_URL_AND_IMG) {//url and image
            wxShare = new WeiXingShare(0, shareUtils, type).setShareCallback(wxCb);
//            shareUtils.addShareMethod(1, wxShare);
        }
        if (wxShare != null) {//微信分享
            shareUtils.addShareMethod(ShareModel.WX_SHARE_TYPE, wxShare);
        }

        //朋友圈分享
        type = shareMap.get(ShareModel.PYQ_SHARE_TYPE);
        ShareProcessorTemplate pyqShare = null;
        if (type == ShareModel.TYPE_IMG) {//单图
            pyqShare = new WeiXingImgShare(1, shareUtils, type).setShareCallback(wxCb);
//            shareUtils.addShareMethod(2, pyqShare);
        } else if (type == ShareModel.TYPE_URL_AND_IMG) {//url and image
            pyqShare = new WeiXingShare(1, shareUtils, type).setShareCallback(wxCb);
//            shareUtils.addShareMethod(2, pyqShare);
        }
        if (pyqShare != null) {//朋友圈分享
            shareUtils.addShareMethod(ShareModel.PYQ_SHARE_TYPE, pyqShare);
        }

        //qq分享
        type = shareMap.get(ShareModel.QQ_SHARE_TYPE);
        ShareProcessorTemplate qqShare = null;
        if (type == ShareModel.TYPE_IMG) {
            qqShare = new QQImgShare(0, shareUtils, type).setShareCallback(_cb);
//            shareUtils.addShareMethod(4, qqShare);
        } else if (type == ShareModel.TYPE_URL_AND_IMG) {
            qqShare = new QQPyShare(shareUtils, type).setShareCallback(_cb);
//            shareUtils.addShareMethod(4, qqShare);
        }
        if (qqShare != null) {//qq分享
            shareUtils.addShareMethod(ShareModel.QQ_SHARE_TYPE, qqShare);
        }

        //qq空间
        type = shareMap.get(ShareModel.ZONE_SHARE_TYPE);
        ShareProcessorTemplate qzoneShare = null;
        if (type == ShareModel.TYPE_IMG) {
            qzoneShare = new QQImgShare(1, shareUtils, type).setShareCallback(_cb);
//            shareUtils.addShareMethod(5,qzoneShare);
        } else if (type == ShareModel.TYPE_URL_AND_IMG) {
            qzoneShare = new QZoneShare(shareUtils, type).setShareCallback(_cb);
//            shareUtils.addShareMethod(5,qzoneShare);
        }
        if (qzoneShare != null) {//qq空间
            shareUtils.addShareMethod(ShareModel.ZONE_SHARE_TYPE, qzoneShare);
        }

        type = shareMap.get(ShareModel.LIFE_CIRCLE_SHARE_TYPE);
        ShareProcessorTemplate smsShare = null;
        if (type == ShareModel.TYPE_IMG && isUGCShare) {
//            shareUtils.addShareMethod(8, smsShare);//生活圈
        } else if (!isUGCShare) {
            smsShare = new SMSShare(shareUtils);
//            shareUtils.addShareMethod(8, smsShare);//短信
        }
        if (qzoneShare != null) {
            shareUtils.addShareMethod(ShareModel.LIFE_CIRCLE_SHARE_TYPE, smsShare);
        }

        //微博
        type = shareMap.get(ShareModel.WEIBO_SHARE_TYPE);
        ShareProcessorTemplate weiBoShare = new WeiBoShare(shareUtils, type).setShareCallback(_cb);
//        shareUtils.addShareMethod(3, weiBoShare);
        shareUtils.addShareMethod(ShareModel.WEIBO_SHARE_TYPE, weiBoShare);

        //其它
        ShareProcessorTemplate otherShare = new OtherAppShare(shareUtils);
//        shareUtils.addShareMethod(7,otherShare);
        shareUtils.addShareMethod(ShareModel.OTHER_SHARE_TYPE, otherShare);

        if (isUGCShare) {//用于屏幕截图短信分享
            ShareProcessorTemplate screenshotShare = new SMSShare(shareUtils);
//            shareUtils.addShareMethod(9, screenshotShare);
            shareUtils.addShareMethod(ShareModel.OTHER_SHARE_TYPE, screenshotShare);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == ll_WeiXin_py) {
            isNetShare = true;
            ShareUtils.setOutShareParas(mch_id, appid);
            shareUtils.doShare(ShareModel.WX_SHARE_TYPE);
        } else if (v == ll_WeiXin_pyq) {
            isNetShare = true;
            ShareUtils.setOutShareParas(mch_id, appid);
            shareUtils.doShare(ShareModel.PYQ_SHARE_TYPE);
        } else if (v == ll_Sina) {
            if (!UtilsManager.isAppInstalled(mActivity, "com.sina.weibo")) {
                UtilsManager.Toast(mActivity, R.string.weibo_not_installed);
                return;
            }
            isNetShare = true;
            ShareUtils.setOutShareParas(mch_id, appid);
            shareUtils.doShare(ShareModel.WEIBO_SHARE_TYPE);
        } else if (v == ll_Tencent) {
            isNetShare = true;
            shareUtils.doShare(ShareModel.QQ_SHARE_TYPE);
        } else if (v == ll_QZone) {
            isNetShare = true;
            shareUtils.doShare(ShareModel.ZONE_SHARE_TYPE);
        } else if (v == ll_copy2clip) {
            isNetShare = false;
            int type = shareMap.get(ShareModel.COTY2Clip_SHARE_TYPE);
            if (type == ShareModel.TYPE_IMG) {
                UtilsManager.Toast(ctx, R.string.share_not_support);
            } else {
                shareUtils.doShare(ShareModel.COTY2Clip_SHARE_TYPE);
            }
        } else if (v == ll_shareOther) {
            if (!isNeedShare2FishPool) {
                isNetShare = false;
                shareUtils.doShare(ShareModel.OTHER_SHARE_TYPE);
            } else {
                UtilsManager.Toast(ctx, R.string.share_not_support);
            }
        } else if (v == ll_sms_life) {//短信或者生活圈
            isNetShare = false;
            if (isUGCShare) {//生活圈
                int type = shareMap.get(ShareModel.LIFE_CIRCLE_SHARE_TYPE);
                if (type == ShareModel.TYPE_IMG) {
                    shareUtils.doShare(ShareModel.LIFE_CIRCLE_SHARE_TYPE);
                } else {
                    UtilsManager.Toast(ctx, R.string.share_not_support);
                }
            } else {
                int type = shareMap.get(ShareModel.SMS_SHARE_TYPE);
                if (type == ShareModel.TYPE_IMG) {
                    UtilsManager.Toast(ctx, R.string.share_not_support);
                } else {
                    shareUtils.doShare(ShareModel.LIFE_CIRCLE_SHARE_TYPE);
                }
            }
        }
        dismiss();
    }


    private ShareListener mShareListener = null;

    public void setShareListener(ShareListener listener) {
        this.mShareListener = listener;
    }

    public interface ShareListener {
        void onShareSuccess();

        void onShareCancel();

        void onShareFailed();
    }


    private final int SHARE_SUCCESS_TOAST = 12;
    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:  //分享取消
//                    UtilsManager.Toast(mActivity, ctx.getResources().getString(R.string.share_cancel));
                    if (mShareListener != null) {
                        mShareListener.onShareCancel();
                    }
                    break;
                case 1: //分享成功
                    if (mShareListener != null) {
                        mShareListener.onShareSuccess();
                    }
                    doShareTask();
                    break;
                case 2:  //分享失败
                    if (mShareListener != null) {
                        mShareListener.onShareFailed();
                    }
                    break;
                case 3:
//                    pd = UtilsManager.createLoadingDialog(mActivity, ctx.getResources().getString(R.string.share_now), true);
//                    pd.setCanceledOnTouchOutside(true);
//
//                    pd.setOnCancelListener(new OnCancelListener() {
//                        @Override
//                        public void onCancel(DialogInterface dialog) {
//                            if (shareUtils != null) {
//                                UtilsManager.printlog4lyc("share progress cancel");
//                                shareUtils.forceStopShare();
//                                shareUtils.mActivity = null;
//                            }
//                        }
//                    });
//                    if (!mActivity.isFinishing() && isShowing()) {
//                        pd.show();
//                    }
                    break;
                case 4:
                    if (pd != null && pd.isShowing() && isShowing()) {
                        pd.dismiss();
                    }
                    break;

                case 6:
//                    Toast.makeText(ctx, ctx.getResources().getString(R.string.more_share_23), Toast.LENGTH_SHORT).show();
                    UtilsManager.Toast(ctx, "找不到APP");
                    break;
                case 8:
                    UtilsManager.Toast(ctx, "找不到APP");
                case 10:
                    String _msg = msg.getData().getString("msg");
                    if (!TextUtils.isEmpty(_msg)) {
                        UtilsManager.Toast(mActivity, _msg);
                    }
                    break;
                case 11:
//                    UtilsManager.Toast(mActivity, (String) msg.obj + mActivity.getString(R.string.sign_task_complete)
//                            + msg.arg1 + mActivity.getString(R.string.sign_coins));
                    break;
                case SHARE_SUCCESS_TOAST:
//                    UtilsManager.Toast(mActivity, ctx.getResources().getString(R.string.share_success));
                    break;
                default:
                    break;
            }
        }

    };

    public void closeProgressBar() {
        handler.sendEmptyMessage(4);
    }

    private void doShareTask() {
        handler.sendEmptyMessage(SHARE_SUCCESS_TOAST);
    }

    /**
     * 设置是否分享到小程序，要放到show之前设置
     */
    public void setIsWXMiniProgram() {
        if (shareMap == null) {
            return;
        }
        shareMap.put(ShareModel.WX_SHARE_TYPE, ShareModel.Type_WX_MINI);
    }

    /**
     * 设置微信分享小程序的path，要setIsWXMiniProgram设置为true才会有效
     *
     * @param path
     */
    public void setWXMiniProgramPath(String path) {
        shareUtils.WXMiniProgramPath = path;
    }

    /**
     * 设置微信小程序单独分享的本地图片要setIsWXMiniProgram设置为true才会有效
     *
     * @param imgID
     */
    public void setWXMiniProgramImgId(int imgID) {
        shareUtils.WXMiniImgId = imgID;
    }
}
