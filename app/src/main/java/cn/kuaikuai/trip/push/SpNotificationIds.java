package cn.kuaikuai.trip.push;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * 保存推送消息id的一些增量
 */
public class SpNotificationIds {
    private Context context;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    private String FILE_NAME = "NotificationIds";

    public static SpNotificationIds getInstance(Context context) {
        return new SpNotificationIds(context.getApplicationContext());
    }

    public SpNotificationIds(Context context) {
        this.context = context;
        settings = context.getSharedPreferences(FILE_NAME, MODE_PRIVATE);
        editor = settings.edit();
    }

    /**
     * 设置当前推送的通知栏id，方便后续进行增量处理
     * 新增 OTHER_TYPE key
     *
     * @param tag
     * @param id
     */
    private void setCurrentNotificationIds(int tag, int id) {
        switch (tag) {
            default:
                editor.putInt("OTHER_TYPE", id);
                break;
        }
        editor.commit();
    }

    /**
     * 获取当前推送消息的通知栏id
     *
     * @param tag
     * @return
     */
    private int getCurrentNotificationIds(int tag) {
        int id;
        switch (tag) {
            default://其他类型的消息
                id = settings.getInt("OTHER_TYPE", 0);
                break;
        }
        return id;
    }

    /**
     * 根据pushMsg的tag去获取新的notification id
     *
     * @param tag PushInfo.h.xxx
     * @return
     */
    public int getNewtNotificationIds(int tag) {
        int id = getCurrentNotificationIds(tag);
        int new_id = (id + 1) % 10;
        setCurrentNotificationIds(tag, new_id);
        return new_id;
    }
}

