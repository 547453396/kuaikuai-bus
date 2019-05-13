package cn.kuaikuai.trip;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import android.util.Log;

import com.getui.gs.ias.core.GsConfig;
import com.getui.gs.sdk.GsManager;

import java.util.HashMap;
import java.util.Map;

import cn.kuaikuai.base.BaseApplication;
import cn.kuaikuai.common.ChannelUtil;
import cn.kuaikuai.common.LogUtils;
import cn.kuaikuai.common.image.MemoryControl;
import cn.kuaikuai.common.net.api.ApiManage;
import cn.kuaikuai.trip.dao.DaoMaster;
import cn.kuaikuai.trip.dao.DaoSession;
import cn.kuaikuai.trip.event.WXLoginEvent;
import cn.kuaikuai.trip.model.entity.DBHelper;
import cn.kuaikuai.trip.net.ApiInterface;
import cn.kuaikuai.trip.utils.UtilsManager;

/**
 * Created by liheng on 18/7/4.
 */

public class MainApplication extends BaseApplication {
    private static final String TAG = MainApplication.class.getSimpleName();
    //微信登录类型，由于微信授权的返回方式，没有办法监测到从哪里进行的授权回调，
    // 只能搞个全局变量进行表示，不然发event的时候不知道怎么去标示处理数据
    public static int wx_login_type = WXLoginEvent.type_login_register;
    private DaoSession mDaoSession;
    private static MainApplication application;
    public static int screenWidth;
    public static int screenHeight;
    public static int screenMin;// 宽高中，小的一边
    public static int screenMax;// 宽高中，较大的值
    /** 首页是否在运行 */
    private boolean isWLCCRun = false;

    @Override
    public void onCreate() {
        super.onCreate();
        long time = System.currentTimeMillis();
        application = this;

        initDao();
        if (checkMainProgress(getApplicationContext())) {
            Map<String, String> headers = new HashMap<>();
            headers.put("key","bus-token");
            headers.put("type","text");
            headers.put("value","bus-token");
            ApiManage.initApiManage(getApplicationContext(), ApiInterface.DEFAULT_BASE_URL, headers);
            DisplayMetrics dm = getApplicationContext().getResources().getDisplayMetrics();
            screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            screenMin = (screenWidth > screenHeight) ? screenHeight : screenWidth;
            screenMax = (screenWidth < screenHeight) ? screenHeight : screenWidth;
            // 个数初始化，配置GsConfig需放在初始化GsManager之前;
            GsConfig.setInstallChannel(ChannelUtil.getUmengChannel(this));
            GsManager.getInstance().init(this);
        }
        LogUtils.d(TAG + " onCreate cast time:" + (System.currentTimeMillis() - time));
    }

    @Override
    protected void attachBaseContext(Context base) {
        long time = System.currentTimeMillis();
        super.attachBaseContext(base);
        MultiDex.install(base);
        LogUtils.d(TAG + " attachBaseContext cast time:" + (System.currentTimeMillis() - time));
    }

    public static MainApplication getApplication() {
        return application;
    }

    /**
     * 初始化数据库操作
     */
    private void initDao() {
        DBHelper devOpenHelper = new DBHelper(this);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        mDaoSession = daoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    /**
     * 检查是否是主进程
     *
     * @param ctx
     * @return
     */
    private boolean checkMainProgress(Context ctx) {
        if (ctx == null) {
            return false;
        }
        String currentProgressName = UtilsManager.getCurProcessName(ctx);
        if (currentProgressName != null && currentProgressName.equals(ctx.getPackageName())) {
            return true;
        }
        return false;
    }

    /** 设置软件首页是否启动了 */
    public void setWLCCIsRun(boolean isRun) {
        isWLCCRun = isRun;
    }

    /** 获取软件首页是否启动了 */
    public boolean getWLCCIsRun() {
        return isWLCCRun;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.w(TAG, "onLowMemory");
        MemoryControl.onLowMemory(this);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.w(TAG, "onTrimMemory__level:" + level);
        MemoryControl.onTrimMemory(this, level);
    }
}
