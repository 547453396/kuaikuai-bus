package cn.kuaikuai.base.activity;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import cn.kuaikuai.base.view.IBaseView;
import cn.kuaikuai.common.ActivityManagerUtil;
import cn.kuaikuai.common.LogUtils;
import cn.kuaikuai.common.bar.ImmersionBar;
import cn.kuaikuai.common.swipback.SwipeBackHelper;
import cn.kuaikuai.common.swipback.SwipeListener;

public class BaseActivity extends RxAppCompatActivity implements IBaseView, SwipeListener {
    public final String TAG = this.getClass().getSimpleName();
    public ActivityManagerUtil activityManagerUtil;
    public Activity mActivity;
    public ImmersionBar mImmersionBar;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        activityManagerUtil = ActivityManagerUtil.getInstance();
        activityManagerUtil.pushOneActivity(this);
        SwipeBackHelper.onCreate(this);
        SwipeBackHelper.getCurrentPage(this)
                .setSwipeEdgePercent(0.2f)
                .setDisallowInterceptTouchEvent(false)
                .setSwipeBackEnable(enableSwipeBack())
                .setSwipeRelateEnable(true)
                .addListener(this);
        mImmersionBar = ImmersionBar.with(this);
        if (isUseImmersionBar()) {
            if (fitsSystemWindows()) {
                mImmersionBar
                        .fitsSystemWindows(true)//使用该属性,必须指定状态栏颜色
                        .statusBarColor(android.R.color.transparent)
                        .init();
            }
            mImmersionBar.keyboardEnable(true).statusBarDarkFont(true, 0.2f).init();
            //这儿将不支持状态栏字体变色时状态栏颜色改成主题色
//            if (!ImmersionBar.isSupportStatusBarDarkFont()){
//                mImmersionBar.statusBarColor(R.color.module_common_theme).init();
//            }
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        SwipeBackHelper.onPostCreate(this);
    }

    protected boolean enableSwipeBack() {
        return true;
    }

    private void initAfterSetContentView() {
        ButterKnife.bind(this);
    }


    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initAfterSetContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initAfterSetContentView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initAfterSetContentView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            LogUtils.e("   现在是横屏");
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LogUtils.e("   现在是竖屏");
        }
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        //用来统计页面跳转
        if (openActivityDurationTrack()) {
            MobclickAgent.onPageStart(getComponentName().getClassName());
        }
        //友盟统计时长
        MobclickAgent.onResume(this);
    }

    @Override
    @CallSuper
    protected void onPause() {
        super.onPause();
        //用来统计页面跳转
        if (openActivityDurationTrack()) {
            MobclickAgent.onPageEnd(getComponentName().getClassName()); //"SplashScreen"为页面名称，可自定义
        }
        //友盟统计时长
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SwipeBackHelper.onDestroy(this);
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
        //结束Activity&从栈中移除该Activity
        activityManagerUtil.popOneActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showException(Throwable pe) {

    }

    /**
     * 页面统计，默认开启。
     * 当有fragment需要统计时重写该方法返回false，在fragment中手动添加。
     *
     * @return
     */
    public boolean openActivityDurationTrack() {
        return true;
    }

    @Override
    public void onScroll(float percent, int px) {

    }

    @Override
    public void onEdgeTouch() {

    }

    @Override
    public void onScrollToClose() {

    }

    /**
     * 状态栏与布局顶部重叠解决方案,默认true
     *
     * @return
     */
    public boolean fitsSystemWindows() {
        return true;
    }

    public boolean isUseImmersionBar() {
        return true;
    }
}
