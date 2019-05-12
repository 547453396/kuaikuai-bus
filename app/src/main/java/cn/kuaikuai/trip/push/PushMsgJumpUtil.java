package cn.kuaikuai.trip.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.igexin.sdk.PushManager;

import org.json.JSONException;
import org.json.JSONObject;

import cn.kuaikuai.common.LogUtils;
import cn.kuaikuai.trip.main.activity.MainActivity;
import cn.kuaikuai.trip.utils.IntentScheme;

/**
 * 推送消息跳转处理类
 */
public class PushMsgJumpUtil {
    public static final String str_messageid = "messageid";
    public static final String str_taskid = "taskid";
    public static final String str_firstJumpType = "firstJumpType";
    public static final String str_backJumpType = "backJumpType";
    public static final String str_jump_data = "jump_data";
    public static final String str_isIntentFromPush = "isIntentFromPush";
    public static final String str_msg_id = "msg_id";//随身云messageid
    public static final String str_c_m = "c_m";//统计信息
    public static final String str_tag = "tag"; //消息tag
    public static final String str_from = "from_";

    public static final String FROM_PUSH = AllPushManager.class.getSimpleName();

    /**
     * 点击通知栏后跳转到Loading界面，对参数的处理
     *
     * @param
     **/
    public static void dealWithPushMsg(Context ctx, Intent intent) {

        Bundle bundle = intent.getExtras();

        //推送消息点击统计
        String push_msg_id = getString(bundle, str_messageid, "");
        String push_task_id = getString(bundle, str_taskid, "");

        LogUtils.d("push消息点击 msg_id :" + push_msg_id + "---" + "task_id :" + push_task_id);
        //点击action
        PushManager.getInstance().sendFeedbackMessage(ctx, push_task_id, push_msg_id, PushActionId.click);
        JSONObject args = new JSONObject();
        try {
            String msg_id = getString(bundle, str_msg_id, "");
            String c_m = getString(bundle, str_c_m, "");
            args.put("msg_id", msg_id);
            args.put("c_m", c_m);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //开始跳转
//        int firstJumpType = bundle.getInt(str_firstJumpType);
        int backJumpType = bundle.getInt(str_backJumpType, PushInfo.g.NONE);
        String jump_data = getString(bundle, str_jump_data, "");

        LogUtils.d("push jump_data : " + jump_data);
        jump2Activity(ctx, backJumpType, jump_data, push_msg_id);
    }


    /**
     * 推送消息的跳转处理 (该方法主要在LoadingAcivity中调用)
     *
     * @param ctx
     * @param backJumpType 进入页面后点击返回按钮的跳转
     */
    private static void jump2Activity(Context ctx, int backJumpType, String jump_data, String msg_id) {

        if (TextUtils.isEmpty(jump_data)) {
            startToMain(ctx);
        }
        try {
            Intent intent = new Intent();
            intent.putExtra(str_isIntentFromPush, true);
            intent.putExtra(str_backJumpType, backJumpType);
            if (!IntentScheme.checkIfSpecialUrl(ctx, jump_data, intent)) {
            }
        } catch (Exception e) {//异常了直接跳转到主页
            startToMain(ctx);
            e.printStackTrace();
        }
    }

    /*** 跳转到主页 **/
    public static void startToMain(Context ctx) {
        Intent intent = new Intent(ctx, MainActivity.class);
        ctx.startActivity(intent);
    }

    /**
     * 获取bundle中string类型的值
     *
     * @param bundle
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getString(Bundle bundle, String key, String defaultValue) {
        final String s = bundle.getString(key);
        return (s == null) ? defaultValue : s;
    }
}
