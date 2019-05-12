package cn.kuaikuai.trip.share;

import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yangshuai on 2017/8/16.
 */

public interface ShareModel {
    /**
     * 设置分享类型的枚举类
     */
    String WX_SHARE_TYPE = "wx";
    String PYQ_SHARE_TYPE = "pyq";
    String QQ_SHARE_TYPE = "qq";
    String ZONE_SHARE_TYPE = "qq_zone";
    String WEIBO_SHARE_TYPE = "weibo";
    String LIFE_CIRCLE_SHARE_TYPE = "life_circle";
    String SMS_SHARE_TYPE = "sms";
    String COTY2Clip_SHARE_TYPE = "cpty_2_clip";
    String OTHER_SHARE_TYPE = "other_share_type";
    String SCREENSHOT_SHARE_TYPE = "screenshot_share_type";

    @StringDef({WX_SHARE_TYPE, PYQ_SHARE_TYPE,
            QQ_SHARE_TYPE, ZONE_SHARE_TYPE,
            WEIBO_SHARE_TYPE, LIFE_CIRCLE_SHARE_TYPE,
            SMS_SHARE_TYPE, COTY2Clip_SHARE_TYPE,
            OTHER_SHARE_TYPE, SCREENSHOT_SHARE_TYPE})
    @Retention(RetentionPolicy.SOURCE)
    @interface ShareTypeModel {
    }

    int TYPE_URL_AND_IMG = 0;
    int TYPE_IMG = 1;
    int Type_WX_MINI = 2;

    @IntDef({TYPE_URL_AND_IMG, TYPE_IMG})
    @Retention(RetentionPolicy.SOURCE)
    @interface shareType {
    }

}
