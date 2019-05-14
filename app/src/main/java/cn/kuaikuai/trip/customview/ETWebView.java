package cn.kuaikuai.trip.customview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import cn.kuaikuai.common.ChannelUtil;
import cn.kuaikuai.common.NetUtils;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.utils.PackageHelper;
import cn.kuaikuai.trip.utils.SpUtils;
import cn.kuaikuai.trip.utils.UserAccountPreferences;
import cn.kuaikuai.trip.utils.UtilsManager;

/**
 * Created by etouch on 14/12/3.
 * 封装了WebView的常用属性，外部直接引用即可。
 * <p>
 * 1、解决了跳转地址回退时循环跳转无法退出的问题(判断
 * 页面停留时间<600ms认为是跳转地址，回退时跳过该地址)
 */
public class ETWebView extends WebView implements View.OnLongClickListener {

    private Context ctx;
    /**
     * Activity主要用于加载本地广告
     */
    private Activity mActivity;
    protected ETWebViewHelper mETWebViewHelper;
    /**
     * 用户设置的WebViewClient
     */
    private WebViewClient theUserWebViewClient = null;
    private OnTouchListener theUserOnTouchListener = null;
    private WebChromeClient webChromeClient = null;
    private final int The_Default_time = 600;
    private LinkedHashMap<String, Long> urlTables = new LinkedHashMap<>();
    private long lastTime = 0;
    private String lastUrl = "";
    /**
     * 进入后是否点击了WebView中的链接(用于判断没点击前不跳转到新页面)
     */
    public boolean hasTouchWebViewLink = false;
    public boolean isTouchDown = false;
    private String additionalJs;

    /***************网页加载完成时加载去广告JS代码块start**********************/
    private String js = "";
    private Hashtable<String, Integer> hasLoadJsUrl = new Hashtable<String, Integer>();
    private boolean isNeedLoadRemoveAdJs = false;
    /***************网页加载完成时加载去广告JS代码块end**********************/
    private ETWebViewListener mETWebViewListener;
    private String sourceUserAgent = "";
    private boolean needVisibilityForWindow = true;
    private boolean mCanLongClick = true;

    public ETWebView(Context context) {
        super(context);
        Init(context);
    }

    public ETWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public ETWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Init(context);
    }

    private void Init(Context ctx) {
        this.ctx = ctx;
        mETWebViewHelper = new ETWebViewHelper(ctx, webViewHelperListener);
        this.getSettings().setJavaScriptEnabled(true);
        this.addJavascriptInterface(mETWebViewHelper, "etouch_client");
        super.setWebViewClient(myWebViewClient);
        WebSettings settings = this.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);//可能的话，使所有列的宽度不超过屏幕宽度
        settings.setSupportMultipleWindows(false);//多窗口
        settings.setDefaultTextEncodingName("UTF-8");//设置编码
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);//渲染高
        int SDK = Build.VERSION.SDK_INT;
        try {
            settings.setAllowFileAccess(true);
            settings.setDatabaseEnabled(true);
            settings.setGeolocationEnabled(true);
            settings.setAppCacheEnabled(true);
            settings.setDomStorageEnabled(true);
            /**处理自适应网页方法1*/
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            settings.setPluginState(WebSettings.PluginState.ON);
            //设置WebView是否通过手势触发播放媒体，默认是true，需要手势触发。
            if (SDK >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                settings.setMediaPlaybackRequiresUserGesture(false);
            }
            /**支持缩放网页*/
            settings.setBuiltInZoomControls(false); //显示放大缩小controler
            settings.setSupportZoom(false);//可以缩放
            settings.setTextZoom(100);
            String appCachePath = ctx.getCacheDir().getAbsolutePath();
            settings.setAppCachePath(appCachePath);
        } catch (Exception e) {
        }
        this.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
        super.setOnTouchListener(onTouchListener);
        sourceUserAgent = settings.getUserAgentString();
        /**添加userAgent*/
        settings.setUserAgentString(sourceUserAgent + equipUserAgent());
        hasTouchWebViewLink = false;
        isTouchDown = false;
        setOnLongClickListener(this);
    }

    private String equipUserAgent() {
        StringBuilder sb = new StringBuilder();
        sb.append(" app/wlcc");
        PackageHelper packageHelper = new PackageHelper(getContext());
        sb.append(" v_name/").append(packageHelper.getLocalVersionName());
        sb.append(" channel/").append(ChannelUtil.getChannel(getContext()));
        if (NetUtils.isWiFi(getContext())) {
            sb.append(" net/wifi");
        }
        UserAccountPreferences userAccountPreferences = UserAccountPreferences.getInstance(getContext());
        if (!TextUtils.isEmpty(userAccountPreferences.getToken())) {
            sb.append(" open_id/").append(userAccountPreferences.getToken());
        }
        sb.append(" device_id/");
        String device_id = SpUtils.getInstance(ctx).getImei() + SpUtils.getInstance(ctx).getMac();
        device_id = UtilsManager.getMD5(device_id.getBytes());
        sb.append(device_id);
        return sb.toString();
    }

    /**
     * webView在刷新或者重新加载数据时将js计数器重置
     */
    public void resetJSCount() {
        hasLoadJsUrl.clear();
    }

    @Override
    public void loadUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        String tempUrl = url.toLowerCase();
        int index = url.indexOf("?");
        if (index != -1) {
            tempUrl = url.substring(0, index);
        }
        if (tempUrl.endsWith(".apk")) {
            String fileName = tempUrl.substring(tempUrl.lastIndexOf("/") + 1);
//            DownloadMarketService.DownloadTheApk(mActivity, 0, "", fileName, url, null);
        } else {
            if (tempUrl.startsWith("http") || tempUrl.startsWith("https") || tempUrl.startsWith("ftp") || tempUrl.startsWith("javascript") || tempUrl.startsWith("file")) {
                try {
                    //微信支付通过Referer检查是否盗链，修复每日运势页面广告不显示微信支付的问题
                    Map<String, String> extraHeaders = new HashMap<String, String>();
                    extraHeaders.put("Referer", getUrl());
                    super.loadUrl(url, extraHeaders);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        super.loadUrl(url);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            } else {
                try {
                    //修复点击京东类广告（app后台已经启动）启动京东app后退出无法回到万年历的问题
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    ctx.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ctx.startActivity(intent);
                    } catch (Exception e1) {
                    }
                }
            }//end else
        }
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.theUserOnTouchListener = onTouchListener;
    }

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int action = motionEvent.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isTouchDown = true;
                    break;
                case MotionEvent.ACTION_UP:

                    break;
            }
            if (theUserOnTouchListener != null) {
                return theUserOnTouchListener.onTouch(view, motionEvent);
            }
            return false;
        }
    };

    private final String ADJsString = "ADJsString", ADJsTime = "ADJsTime";

    /**
     * 用于处理返回时由于重定向而产生的死循环start============
     */
    private WebViewClient myWebViewClient = new WebViewClient() {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!hasTouchWebViewLink && isTouchDown) {
                hasTouchWebViewLink = true;
            }
            isTouchDown = false;
            if (theUserWebViewClient != null) {
                return theUserWebViewClient.shouldOverrideUrlLoading(view, url);
            } else {
                return super.shouldOverrideUrlLoading(view, url);
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (lastTime == 0) {
                lastTime = System.currentTimeMillis();
            }
            if (!TextUtils.isEmpty(lastUrl)) {
                urlTables.put(lastUrl, System.currentTimeMillis() - lastTime);
            }
            lastTime = System.currentTimeMillis();
            lastUrl = url;
            if (theUserWebViewClient != null) {
                theUserWebViewClient.onPageStarted(view, url, favicon);
            } else {
                super.onPageStarted(view, url, favicon);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (isNeedLoadRemoveAdJs) {
                int count = hasLoadJsUrl.containsKey(url) ? hasLoadJsUrl.get(url) : 0;
                if (count < 5) {
                    hasLoadJsUrl.put(url, count + 1);/**加载去广告js*/
                    view.loadUrl(js);
                }
            }
            if (theUserWebViewClient != null) {
                theUserWebViewClient.onPageFinished(view, url);
            } else {
                super.onPageFinished(view, url);
            }

            /***额外 JS 屏蔽方法*/
            try {
                if (!TextUtils.isEmpty(additionalJs)) {
                    view.loadUrl("javascript:" + additionalJs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if (theUserWebViewClient != null) {
                theUserWebViewClient.onReceivedError(view, errorCode, description, failingUrl);
            } else {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
//            super.onReceivedSslError(view, handler, error);
            if (ChannelUtil.getUmengChannel(mActivity).equalsIgnoreCase("googleMarket")) {
                CustomDialog customDialog = new CustomDialog(mActivity);
                customDialog.setMessage("当前网站的证书来自不可信任的授权中心，是否信任并继续访问？");
                customDialog.setPositiveButton("继续访问", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.proceed();
                    }
                });
                customDialog.setNegativeButton("取消", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.cancel();
                    }
                });
                customDialog.show();
            } else {
                handler.proceed();
            }
        }
    };

    @Override
    public void setWebViewClient(WebViewClient client) {
        this.theUserWebViewClient = client;
    }

    //最后backForwardUrl地址
    private String lastBackForward = "";

    @Override
    public boolean canGoBack() {
        boolean result = super.canGoBack();
        if (result) {/**如果可以返回则判断url栈中是否是跳转地址，停留时间小于500认为是跳转地址*/
            WebBackForwardList list = this.copyBackForwardList();
            int nowIndex = list.getCurrentIndex();

            /*String url = list.getItemAtIndex(nowIndex - 1).getUrl();
            if (!urlTables.containsKey(url)) {
                urlTables.put(url,800L);
            }*/

            int position;
            for (position = nowIndex - 1; position >= 0; position--) {
                String url = list.getItemAtIndex(position).getUrl();
                long time = 0;
                if (urlTables.containsKey(url)) {
                    time = urlTables.get(url);
                } else {
                    urlTables.put(url, 800L);
                    time = 800L;
//                    //避免中文书城这种无法退出的情况http://wannianli.ikanshu.cn/wread/wxread.htm
//                    if (lastBackForward.equals(url)) {
//                        time = 0;
//                    } else {
//                        //不在链接记录历史里的也可以正常回退，所以把time改大
//                        time = 800;
//                    }
//                    lastBackForward = url;
                }
                if (time > The_Default_time) {
                    break;
                }
            }
            result = position >= 0;
        }
        return result;
    }

    @Override
    public void goBack() {
        WebBackForwardList list = this.copyBackForwardList();
        int nowIndex = list.getCurrentIndex();
        int position;
        for (position = nowIndex - 1; position >= 0; position--) {
            String url = list.getItemAtIndex(position).getUrl();
            long time = urlTables.containsKey(url) ? urlTables.get(url) : 800;
            if (time > The_Default_time) {
                break;
            }
        }
        if (position < 0) {
            position = 0;
        }
        goBackOrForward(position - nowIndex);
        if (webChromeClient != null) {
            webChromeClient.onReceivedTitle(this, getTheTitleWhenGoBack());
        }
    }
    /**用于处理返回时由于重定向而产生的死循环end============*/
    /**
     * 回退时用户获取标题
     */
    public String getTheTitleWhenGoBack() {
        WebBackForwardList list = this.copyBackForwardList();
        int nowIndex = list.getCurrentIndex();
        if (list.getSize() > nowIndex) {
            return list.getItemAtIndex(nowIndex).getTitle();
        }
        return "";
    }

    @Override
    public void setWebChromeClient(WebChromeClient client) {
        this.webChromeClient = client;
        super.setWebChromeClient(client);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //注册支付广播和onDetachedFromWindow中unregisterReceiver对应
        registerBroadCast();
        if (needVisibilityForWindow) {
            this.setVisibility(VISIBLE);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        /**移除前设置Gone目的是避免5.x手机中设置可缩放后退出时闪退*/
        //应该在内置缩放控件消失以后,再执行mWebView.destroy(),否则报错WindowLeaked
        if (needVisibilityForWindow) {
            this.setVisibility(GONE);
        }
        ctx.unregisterReceiver(broadcastReceiver);
        hasLoadJsUrl.clear();
        if (urlTables != null) {
            urlTables.clear();
        }
        super.onDetachedFromWindow();
    }

    private ETWebViewHelper.ETWebViewHelperListener webViewHelperListener = new ETWebViewHelper.ETWebViewHelperListener() {
        @Override
        public void onGetShareContent(String title, String desc, String img, String url) {
            if (mETWebViewListener != null) {
                mETWebViewListener.onGetShareContent(title, desc, img, url);
            }
        }

        @Override
        public void onScrollListener(int height) {
            if (mETWebViewListener != null) {
                mETWebViewListener.onScrollListener(height);
            }
        }
    };

    public void setETWebViewListener(ETWebViewListener listener) {
        this.mETWebViewListener = listener;
    }

    @Override
    public boolean onLongClick(View v) {
        if (mCanLongClick) {
            HitTestResult result = ((WebView) v).getHitTestResult();
            if (null == result)
                return false;
            int type = result.getType();
            if (type == HitTestResult.UNKNOWN_TYPE)
                return false;
            if (type == HitTestResult.IMAGE_TYPE) {
                String saveImgUrl = result.getExtra();
                if (!TextUtils.isEmpty(saveImgUrl)) {
                    showSaveToPhoto(saveImgUrl);
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public void canLongClick(boolean longClick) {
        mCanLongClick = longClick;
    }

    /**
     * 保存到相册对话框
     *
     * @param saveImgUrl
     */
    private void showSaveToPhoto(final String saveImgUrl) {
        if (mActivity == null) {
            return;
        }
        CustomDialog customDialog = new CustomDialog(mActivity);
        customDialog.setMessage(mActivity.getString(R.string.save_to_photo) + "?");
        customDialog.setPositiveButton(R.string.btn_ok, new OnClickListener() {
            @Override
            public void onClick(View v) {
                save2Photo(saveImgUrl);
            }
        });
        customDialog.setNegativeButton(R.string.btn_cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        customDialog.show();
    }

    /**
     * 保存到相册
     *
     * @param url
     */
    public void save2Photo(final String url) {
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String folder = "";
                    File dcim = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
                    if (!dcim.exists()) {
                        if (dcim.mkdirs()) {
                            folder = dcim.getAbsolutePath();
                        } else {
                            File pictures = new File(Environment.getExternalStorageDirectory() + "/Pictures");
                            if (!pictures.exists()) {
                                pictures.mkdirs();
                            }
                            folder = pictures.getAbsolutePath();
                        }
                    } else {
                        folder = dcim.getAbsolutePath();
                    }
                    Bitmap bm = null;
                    //注意下这儿分成http形式的下载链接和已经下载的base64流
                    if (url.contains("base64")) {
                        byte[] bytes = Base64.decode(url.substring(url.lastIndexOf(",")), Base64.DEFAULT);
                        bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    } else {
                        URL iconUrl = new URL(url);
                        URLConnection conn = iconUrl.openConnection();
                        HttpURLConnection http = (HttpURLConnection) conn;
                        int length = http.getContentLength();
                        conn.connect();
                        // 获得图像的字符流
                        InputStream is = conn.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is, length);
                        bm = BitmapFactory.decodeStream(bis);
                        bis.close();
                        is.close();
                    }

                    String filePath = UtilsManager.saveBitmapToPath(folder, bm);
                    /**刷新相册*/
                    if (!TextUtils.isEmpty(filePath)) {
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = FileProvider.getUriForFile(
                                mActivity,
                                mActivity.getPackageName() + ".fileProvider",
                                new File(filePath));
                        intent.setData(uri);
                        mActivity.sendBroadcast(intent);
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsManager.Toast(mActivity, R.string.save_to_photo_success);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UtilsManager.Toast(mActivity, R.string.save_to_photo_fail);
                        }
                    });
                }
            }
        });
    }

    public interface ETWebViewListener {
        /**
         * 获取到分享信息，title,desc,img
         */
        void onGetShareContent(String title, String desc, String img, String url);

        void onGetShareContent(String content);

        void onScrollListener(int height);
    }

    /**
     * 设置userAgent
     */
    public void setUserAgent() {
        getSettings().setUserAgentString(sourceUserAgent + equipUserAgent());
    }

    private boolean isRegister = false;
    private boolean isNeedReceive = false;
    BroadcastReceiver broadcastReceiver;

    public void setIsNeedReceive(boolean isNeedReceive) {
        this.isNeedReceive = isNeedReceive;
    }

    private void registerBroadCast() {
        if (!isRegister) {
            broadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    if (isNeedReceive) {
                        String action = intent.getAction();
                        if (action.equals("zhwnl_login_succ")) {
                            setUserAgent();
                            //避免出现url为空的情况
                            String url = getUrl();
                            if (!TextUtils.isEmpty(url) && !TextUtils.equals("about:blank", url)) {
                                loadUrl(url);
                            }
                        } else if (action.equals("zhwnl_pay_succ")) {
                            String mac_id = intent.getStringExtra("mch_id");
                            String appid = intent.getStringExtra("appid");
                            StringBuffer sb = new StringBuffer("javascript:zhwnlPayResult('" + mac_id + "','" + appid + "','1')");
                            loadUrl(sb.toString());
                        } else if (action.equals("zhwnl_pay_cancel")) {
                            String mac_id = intent.getStringExtra("mch_id");
                            String appid = intent.getStringExtra("appid");
                            StringBuffer sb = new StringBuffer("javascript:zhwnlPayResult('" + mac_id + "','" + appid + "','0')");
                            loadUrl(sb.toString());
                        }
                    }
                }
            };
            IntentFilter filter = new IntentFilter();
            filter.addAction("zhwnl_login_succ");
            filter.addAction("zhwnl_pay_succ");
            filter.addAction("zhwnl_pay_cancel");
            ctx.registerReceiver(broadcastReceiver, filter);
        }
    }

    /**
     * onAttachedToWindow  onDetachedFromWindow时，是否显示 隐藏View
     * 默认 true
     */
    public void setNeedVisibilityForWindow(boolean needVisibilityForWindow) {
        this.needVisibilityForWindow = needVisibilityForWindow;
    }

    /**
     * WebView 滚动回调
     */
    public interface OnScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    private OnScrollChangedListener onScrollChangedListener;

    public void setWVOnScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener = onScrollChangedListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangedListener != null) {
            onScrollChangedListener.onScrollChanged(l, t, oldl, oldt);
        }
    }

    /**
     * 页面加载完成后, 执行 js
     *
     * @param jsCode
     */
    public void setAdditionalJs(String jsCode) {
        additionalJs = jsCode;
    }

    public Map<String, Long> getHistory() {
        return urlTables;
    }
}
