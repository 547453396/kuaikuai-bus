package cn.kuaikuai.trip.share.shareprocessor;

import android.text.TextUtils;

import cn.kuaikuai.trip.share.ShareUtils;
import cn.kuaikuai.trip.share.WeiBoShareActivity;


/**
 * Created by liuyc on 2014/9/19.
 * 这个只是针对新浪微博的分享
 */
public class WeiBoShare extends ShareProcessorTemplate {
    private String shareContent = "";

    public WeiBoShare(ShareUtils shareUtils, int shareModel) {
        super(shareUtils, shareModel);
        needNetImgOrLocalImg = 4;
    }

    @Override
    public boolean isLegal() {
        return true;
    }

    @Override
    public void doShare() {
        shareContent = "";
        if (!TextUtils.isEmpty(mContentUrl)) {
            shareContent = get140Content(TextUtils.isEmpty(oneMsg) ? mContentBody : oneMsg, mContentUrl);
        } else {
            shareContent = get140Content(TextUtils.isEmpty(oneMsg) ? mContentBody : oneMsg, "");
        }
//        shareContent += " @中华万年历 中国最好的日历";
        shareUtils.handler.post(new Runnable() {
            @Override
            public void run() {
                String img = shareUtils.netImgUrl;
                if (TextUtils.isEmpty(img)) {
                    img = shareUtils.localImgPath;
                }
                WeiBoShareActivity.shareCallBack = cb;
                WeiBoShareActivity.shareMsg2WeiBo(mActivity, shareContent, img);
            }
        });

    }


    private String get140Content(String content, String url) {
        if (TextUtils.isEmpty(content)) content = "";
        if (calculateLength(content + url) <= 130) {
            return content + "\n" + url;
        }
        if (calculateLength(url) > 130) {
            return content + "\n" + url;
        } else {
            int plusLength = (int) calculateLength("......" + url);
            if (plusLength >= 110) {
                return content + "\n" + url;
            } else {
                return content.substring(0, (int) Math.min(get110WordsIndex(content, plusLength), content.length())) + "......" + url;
            }
        }
    }

    /**
     * @param c
     * @param plusLength
     * @return 微博统计的110字所在的位置
     */

    private long get110WordsIndex(CharSequence c, int plusLength) {
        long result[] = new long[2];
        for (int i = 0; result[0] < 110 - plusLength; i++) {
            int tmp = (int) c.charAt(i);
            result[1] += 1;
            if (tmp > 0 && tmp < 127) {
                result[0] += 0.5;
            } else {
                result[0] += 1;
            }
        }
        return result[1];
    }

    /**
     * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
     *
     * @param c
     * @return
     */

    private long calculateLength(CharSequence c) {
        double len = 0;
        for (int i = 0; i < c.length(); i++) {
            int tmp = (int) c.charAt(i);
            if (tmp > 0 && tmp < 127) {
                len += 0.5;
            } else {
                len++;
            }
        }
        return Math.round(len);
    }


    @Override
    public void onSuccess(String share_channel) {
        super.onSuccess(share_channel);
        if (cb != null) {
            cb.onSuccess();
        }
    }

    @Override
    public void onFail(int resultCode) {//0分享取消 2分享失败
        if (cb != null) {
            cb.onFail(resultCode, "");
        }

    }
}
