package cn.kuaikuai.base;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;

import cn.kuaikuai.common.ChannelUtil;

public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        MobclickAgent.startWithConfigure(new MobclickAgent.UMAnalyticsConfig(this, "5b3d7670a40fa30eb60001c4", ChannelUtil.getUmengChannel(this)));
        MobclickAgent.openActivityDurationTrack(false);// 友盟禁止默认的页面统计方式，这样将不会再自动统计Activity，在activity中手动统计。

        //崩溃处理
//        CrashHandlerUtil crashHandlerUtil = CrashHandlerUtil.getInstance();
//        crashHandlerUtil.init(this);
//        crashHandlerUtil.setCrashTip("很抱歉，程序出现异常，即将退出！");
    }


}
