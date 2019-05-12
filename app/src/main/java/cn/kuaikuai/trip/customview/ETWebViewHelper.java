package cn.kuaikuai.trip.customview;

import android.content.Context;
import android.webkit.JavascriptInterface;

/**
 * Created by etouch on 14/12/3.
 * ETWebView辅助类，用于在webView中支持js调用本地方法
 */
public class ETWebViewHelper {

    private Context ctx;
    private ETWebViewHelperListener mETWebViewHelperListener = null;

    public ETWebViewHelper(Context context, ETWebViewHelperListener listener) {
        this.ctx = context;
        this.mETWebViewHelperListener = listener;
    }

    /**
     * 从网页中提取分享字符串，title,desc,img
     * 优先级一
     * <div id="etouchShareText">
     * <div id="etouchShareTextTitle">美荔</div>
     * <div id="etouchShareTextDesc">找闺蜜，上美荔</div>
     * <div id="etouchShareTextImg">http://s.static.etouch.cn/imgs/LJG/IOS/Meili/meili.png</div>
     * <div id="etouchShareLink">http://www.baidu.com</div>
     * </div>
     * 优先级二
     * <meta name="share-title" content="邀请你使用中华万年历,新用户惊喜礼包等着你哦"/>
     * <meta name="description" content="邀请你使用中华万年历,新用户惊喜礼包等着你哦"/>
     */
    @JavascriptInterface
    public void doGetShareContent(String title, String desc, String img, String url) {
        if (mETWebViewHelperListener != null) {
            mETWebViewHelperListener.onGetShareContent(title, desc, img, url);
        }
    }

    /**
     * web调用本地的方法,用来统计
     *
     * @param id
     * @param event
     */
    @JavascriptInterface
    public void bridgeAdStatistics(String id, String event, int md, String pos, String args) {
        try {
            Integer c_id = Integer.parseInt(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 滚动高度的js回调
     *
     * @param height
     */
    @JavascriptInterface
    public void scrollListener(int height) {
        if (mETWebViewHelperListener != null) {
            mETWebViewHelperListener.onScrollListener(height);
        }
    }

    public interface ETWebViewHelperListener {
        /**
         * 获取到分享内容
         */
        void onGetShareContent(String title, String desc, String img, String url);

        void onScrollListener(int height);
    }
}
