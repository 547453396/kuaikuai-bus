package cn.kuaikuai.trip.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;


import cn.kuaikuai.common.LogUtils;

/***
 * 个像相关，现不集成个像，注释掉
 */
public class GInsightEventReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getStringExtra("action");
//        if (!TextUtils.isEmpty(action) && action.equalsIgnoreCase(GInsightManager.ACTION_GIUID_GENERATED)) {
//            String giuid = intent.getStringExtra("giuid");
//            LogUtils.d("push", "push GInsightEventReceiver giuid = " + giuid);
//        }
    }
}
