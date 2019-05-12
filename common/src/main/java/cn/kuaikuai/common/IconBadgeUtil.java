package cn.kuaikuai.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

/**
 * Created by LJG on 2018/8/10.
 * 桌面Icon角标管理类，用于设置桌面icon的角标。
 * 适配：华为/荣耀、小米(有通知时默认会有角标无需处理)
 * 注意事项：
 * //华为手机权限
 * <uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE"/>
 */
public class IconBadgeUtil {

    private static IconBadgeUtil iconBadgeUtil=null;

    public static IconBadgeUtil getInstance(Context ctx){
        if(iconBadgeUtil==null){
            iconBadgeUtil=new IconBadgeUtil(ctx);
        }
        return iconBadgeUtil;
    }

    private Context mContext=null;
    private IconBadgeUtil(Context ctx){
        this.mContext=ctx;
    }

    /**
     * 设置桌面图标角标
     * @param num 角标数字，0标识去除角标
     * @param classPath 桌面图片对应的入口Activity的完整路径,不能为空
     */
    public void setBadgeNum(int num,String classPath){
        if (Build.MANUFACTURER.toLowerCase().contains("huawei")){
            setHuaWeiBadgeNum(num,classPath);
        }
    }
    /**
     * 设置华为/荣耀桌面图标角标
     * @param classPath 点击角标时的启动类，建议填写软件启动页(不能为空)
     * */
    private void setHuaWeiBadgeNum(int num,String classPath){
        try {
            Bundle bunlde = new Bundle();
            bunlde.putString("package", mContext.getPackageName());
            if(!TextUtils.isEmpty(classPath)){
                bunlde.putString("class", classPath);
            }
            bunlde.putInt("badgenumber", num);
            mContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bunlde);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 设置OPPO桌面图标角标
//     * */
//    public void setOppoBadgeNum(int num){
//        try {
//            System.out.println("ljg="+Build.MANUFACTURER);
//            if (Build.MANUFACTURER.toLowerCase().contains("oppo")) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
//                    Bundle extras = new Bundle();
//                    extras.putInt("app_badge_count", num);
//                    mContext.getContentResolver().call(Uri.parse("content://com.android.badge/badge"),
//                            "setAppBadgeCount", String.valueOf(num), extras);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    /**
//     * 设置VIVO桌面图标角标
//     * @param classPath 点击角标时的启动类，建议填写软件启动页(不能为空)
//     * */
//    public void setVivoBadgeNum(int num,String classPath){
//        try {
//            System.out.println("ljg="+Build.MANUFACTURER);
//            Intent intent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
//            intent.putExtra("packageName", mContext.getPackageName());
//            intent.putExtra("className", classPath);
//            intent.putExtra("notificationNum", num);
//            mContext.sendBroadcast(intent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


}
