package cn.kuaikuai.trip.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.kuaikuai.common.LogUtils;
import cn.kuaikuai.common.image.DownImage;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.KeyConstant;
import cn.kuaikuai.trip.main.activity.LoadingActivity;
import cn.kuaikuai.trip.model.bean.PushModel;
import cn.kuaikuai.trip.model.entity.DBHelper;
import cn.kuaikuai.trip.utils.GsonUtil;
import cn.kuaikuai.trip.utils.PackageHelper;
import cn.kuaikuai.trip.utils.SpUtils;
import cn.kuaikuai.trip.utils.UserAccountPreferences;
import cn.kuaikuai.trip.utils.UtilsManager;

/**
 * 推送管理类
 * Created by ace on 15/1/17.
 */
public class AllPushManager {
    private static AllPushManager allPushManager = null;
    private Context ctx;

    public static final String IsPushRegisterWithApp = "IsPushRegisterWithApp";
    /***上一条通知的时间 */
    private static long sLastNotification;
    private static final String LAST_SET_TAG_TIME_IN_MILLS = "last_set_tag_in_mills";
    private static final String LastPushTagString = "LastPushTagString";
    private static final String LAST_ALIAS_String = "last_alias";
    /*** 时间间隔：限制24，1天只能设置一次tag */
    private static final long TIME_INTERVAL = 24 * 3600 * 1000;
    private long lastRegisterPushTime = 0;

    private AllPushManager(Context ctx) {
        this.ctx = ctx;
    }

    public static synchronized AllPushManager getInstance(Context context) {
        if (allPushManager == null) {
            allPushManager = new AllPushManager(context.getApplicationContext());
        }
        return allPushManager;
    }

    public void initGetuiPush() {
        //个推初始化
        PushManager.getInstance().initialize(ctx, GetuiPushService.class);
        PushManager.getInstance().registerPushIntentService(ctx, PushIntentService.class);
    }

    public synchronized void registerPush() {
        SpUtils spUtils = SpUtils.getInstance(ctx);

        // 小于5s连续请求时 限制;
        if (System.currentTimeMillis() - lastRegisterPushTime < 5 * 1000) {
            return;
        }
        lastRegisterPushTime = System.currentTimeMillis();

        /*** 设置别名 */
        String lastAlias = (String) spUtils.getSharedPreference(LAST_ALIAS_String, "");
        String alias = getAliasFromDb();
        // spUtils存储的别名和数据库的别名有改变时; 重新设置别名alias
        if (!TextUtils.isEmpty(alias) && !TextUtils.equals(lastAlias, alias)) {
            setPushAlias(alias);
        }

        /*** 因设置tag时，个推服务端有限制1天只能成功设置一次，所以限制24小时 */
        long lastSetTagTime = (long) spUtils.getSharedPreference(LAST_SET_TAG_TIME_IN_MILLS, 0l);
        if (System.currentTimeMillis() - lastSetTagTime <= TIME_INTERVAL) {
            return;
        }
        /*** 设置tag */
        List<String> tagList = getTags();
        String tags = TextUtils.join(",", tagList);
        String lastTags = (String) spUtils.getSharedPreference(LastPushTagString, "");
        if (!TextUtils.isEmpty(tags) && !TextUtils.equals(tags, lastTags)) {
            setTag(tagList);
            spUtils.put(LAST_SET_TAG_TIME_IN_MILLS, System.currentTimeMillis());
        }
    }

    private String getAliasFromDb() {
        return UserAccountPreferences.getInstance(ctx).getAlias();
    }

    /**
     * 设置个推的 alias(别名);
     *
     * @param alias 别名
     * @return 是否设置成功
     */
    public boolean setPushAlias(String alias) {

        boolean isBindSuccess = PushManager.getInstance().bindAlias(ctx, alias);

        if (isBindSuccess) {
            SpUtils.getInstance(ctx).put(LAST_ALIAS_String, alias);
        } else {
            SpUtils.getInstance(ctx).put(LAST_ALIAS_String, "");
        }

        LogUtils.i("push bind ALias result = " + isBindSuccess + " , alias = " + alias);
        return isBindSuccess;
    }

    /**
     * 设置个推的tag(标签)
     *
     * @param tagList
     * @return 是否设置成功
     */
    public boolean setTag(List<String> tagList) {
        if (tagList == null || tagList.size() == 0) {
            return false;
        }

        Tag[] tags = new Tag[tagList.size()];

        for (int i = 0; i < tagList.size(); i++) {
            Tag tag = new Tag();
            tag.setName(tagList.get(0));
            tags[i] = tag;
        }

        int resultCode = PushManager.getInstance().setTag(ctx, tags, "");
        boolean isSuccess = resultCode == 0;
        String tagString = TextUtils.join(",", tagList);
        if (isSuccess) {
            SpUtils.getInstance(ctx).put(LastPushTagString, tagString);
        } else {
            SpUtils.getInstance(ctx).put(LastPushTagString, "");
        }

        LogUtils.i("push bind TAG result = " + isSuccess + ", tag = " + tagString);

        return isSuccess;
    }


    private List<String> getTags() {
        List<String> tagList = new ArrayList<>();
        // 本地版本号;
        String versionName = new PackageHelper(ctx).getLocalVersionName();
        if (!TextUtils.isEmpty(versionName)) {
            tagList.add(versionName);
        }
        // citykey1
        String cityKey1 = getCityKey1FromDb();

        if (!TextUtils.isEmpty(cityKey1)) {
            tagList.add(cityKey1);
        }
        return tagList;
    }

    private String getCityKey1FromDb() {
        String location = DBHelper.getCacheDataByKey(KeyConstant.LOCATION);
        if (TextUtils.isEmpty(location)) {
            return "";
        }
        String cityKey1 = "";

        try {
            JSONObject jsonObject = new JSONObject(location);
            cityKey1 = jsonObject.optString("cityKey1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cityKey1;
    }

    /**
     * 处理server推送过来的消息
     * 处理逻辑看这里 http://pm.etouch.cn/doc-view-1865.html
     */
    public static void handlerMsgFromServerPush(final Context context, String pushData, final String messageid, final String taskid) {
        final PushModel pushModel = GsonUtil.JsonToObject(pushData, PushModel.class);
        if (pushModel == null) {
            return;
        }
        PushModel.C pushContent = pushModel.C;
        //没有具体的内容;
        if (pushContent == null) {
            return;
        }
        String imgUrl = pushContent.img;
        if (TextUtils.isEmpty(imgUrl) || Build.BRAND.equalsIgnoreCase("xiaomi")) {
            showNotifycation(context, messageid, taskid, pushModel, null);
        } else {
            // TODO: 18/8/14
            Bitmap bitmap = DownImage.getBitmap(context.getApplicationContext(), imgUrl, 120, 120);

            showNotifycation(context, messageid, taskid, pushModel, bitmap);

        }
    }


    /**
     * 获取notificationbar的id
     * h=其他值或无，则为普通消息有多少条则显示多少条即可 有多少条显示多少条，暂时定义跟消息一样处理逻辑，最大显示10条
     *
     * @param tag PushInfo.h.xxx
     * @return
     */
    private static int getPushMsgNotificationId(Context ctx, int tag) {
        int base_id = ENotificationIds.base_notification_bar;
        //id增量
        int id_incremental = SpNotificationIds.getInstance(ctx).getNewtNotificationIds(tag);
        return base_id - id_incremental;
    }


    private static void showNotifycation(Context context, String messageid, String taskid, PushModel pushModel, Bitmap bmpIcon) {

        if (pushModel == null) return;
        String title = pushModel.A;
        String content = pushModel.B;
        String msg_id = pushModel.C == null ? "" : pushModel.C.e;
        String c_m = "";
        if (pushModel.C != null && pushModel.C.c_m != null) {
            c_m = pushModel.C.c_m.toString();
        }
        int notification_id = getPushMsgNotificationId(context, PushInfo.h.TAG_DEFAULT);

        if (TextUtils.isEmpty(title)) {
            title = context.getString(R.string.app_name);
        }

        long currentTimeMillis = System.currentTimeMillis();

        PendingIntent pendingIntent = buildPendingIntent(context, messageid, taskid, pushModel.C, currentTimeMillis);
        Notification notification = buildNotifycation(context, title, content, pendingIntent, bmpIcon, currentTimeMillis);

        sLastNotification = currentTimeMillis;

        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(notification_id, notification);
        //统计推送消息显示
        PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, PushActionId.show);
        statisticsEvent(context, msg_id, c_m);
    }

    /**
     * 构建notification
     *
     * @param context
     * @param title
     * @param content
     * @param pendingIntent
     * @param bmpIcon
     * @param currentTimeMillis
     * @return
     */
    private static Notification buildNotifycation(Context context, String title, String content, PendingIntent pendingIntent, Bitmap bmpIcon, long currentTimeMillis) {
        NotificationCompat.Builder notificationBuilder = getDefaultNotificationBuilder(context);
        notificationBuilder
                .setContentTitle(title)  /** 设置通知标题*/
                .setContentText(content) /** 设置通知文本*/
                .setSmallIcon(UtilsManager.getNotificationIconResId()) /**设置通知小图标*/
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setLargeIcon(bmpIcon); /** 设置通知自动清除*/

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 7 && hour < 21) {/**早上7-晚上9点，正常提醒*/
            notificationBuilder.setVisibility(Notification.VISIBILITY_PUBLIC);
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);/**设置优先级最高则会弹出横幅显示*/
            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        } else {/**晚上9点后-早上7前，提醒不震动不响铃*/
            notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        }
        if (currentTimeMillis - sLastNotification <= 5 * 1000) {
            notificationBuilder.setDefaults(Notification.DEFAULT_LIGHTS);
        }
        return notificationBuilder.build();
    }

    /**
     * 构建pendingIntent
     *
     * @param context
     * @param messageid
     * @param taskId
     * @param pushContent
     * @param currentTimeMillis
     * @return
     */
    private static PendingIntent buildPendingIntent(Context context, String messageid, String taskId, PushModel.C pushContent, long currentTimeMillis) {

        PendingIntent pendingIntent = null;
        String c_m = "";
        if (pushContent != null && pushContent.c_m != null) {
            c_m = pushContent.c_m.toString();
        }
        Intent intent = new Intent(context, LoadingActivity.class);
        intent.putExtra(PushMsgJumpUtil.str_messageid, messageid);
        intent.putExtra(PushMsgJumpUtil.str_msg_id, taskId);
        intent.putExtra(PushMsgJumpUtil.str_jump_data, pushContent == null ? "" : pushContent.b);
        intent.putExtra(PushMsgJumpUtil.str_c_m, c_m);
        intent.putExtra(PushMsgJumpUtil.str_msg_id, pushContent == null ? "" : pushContent.e);
        intent.putExtra(PushMsgJumpUtil.str_from, PushMsgJumpUtil.FROM_PUSH);
        intent.setAction("push_" + currentTimeMillis);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;
    }

    /**
     * 获取兼容8.0及以上的NotificationCompat；
     *
     * @param context
     * @return
     */
    public static NotificationCompat.Builder getDefaultNotificationBuilder(Context context) {
        String channelId = "default";
        String channelName = "微鲤";

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder;
        //判断是否是8.0 api26+ 以上，8.0google开启了通知渠道，可以自定义通知渠道，不设置在8.0会没法提示;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // IMPORTANCE_DEFAULT 表示此渠道的重要级别: 默认default;
            NotificationChannel chan1 = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            mBuilder = new NotificationCompat.Builder(context, channelId);
            mNotificationManager.createNotificationChannel(chan1);
        } else {
            mBuilder = new NotificationCompat.Builder(context);
        }
        return mBuilder;
    }

    /**
     * 统计
     *
     * @param context
     * @param msg_id
     * @param c_m
     */
    private static void statisticsEvent(Context context, String msg_id, String c_m) {
        //判断有没有通知栏显示的权限，没有权限其实是显示不了通知栏的，不用统计
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            JSONObject args = new JSONObject();
            try {
                args.put("msg_id", msg_id);
                args.put("c_m", c_m);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消Notification
     */
    private static void cancelNotification(Context context, int id) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);
    }

    public void unBindPush() {

        String alias = (String) SpUtils.getInstance(ctx).getSharedPreference(LAST_ALIAS_String, "");
        // boolean true；表示是否只解绑当前clientid，与别名的关系，
        boolean isSuccess = PushManager.getInstance().unBindAlias(ctx, alias, true);
        if (isSuccess) {
            SpUtils.getInstance(ctx).put(LAST_ALIAS_String, "");
        }

        if (isSuccess) {
            LogUtils.i("解绑push成功，alias  = " + alias);
        } else {
            LogUtils.i("解绑push失败 ，alias = " + alias);
        }
    }
}
