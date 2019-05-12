package cn.kuaikuai.trip.push;

import android.content.Context;
import android.text.TextUtils;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;

import org.json.JSONException;
import org.json.JSONObject;

import cn.kuaikuai.common.LogUtils;
import cn.kuaikuai.trip.utils.SpUtils;

/**
 * 继承 GTIntentService 接收来自个推的消息, 所有消息在线程中回调, 如果注册了该服务, 则务必要在 AndroidManifest中声明, 否则无法接受消息<br>
 * onReceiveMessageData 处理透传消息<br>
 * onReceiveClientId 接收 cid <br>
 * onReceiveOnlineState cid 离线上线通知 <br>
 * onReceiveCommandResult 各种事件处理回执 <br>
 */
public class PushIntentService extends GTIntentService {

    private final String LastGeTuiReportClientID = "LastGeTuiReportClientID";
    private final String LastGeTuiClientIDReportTime = "LastGeTuiClientIDReportTime";

    public PushIntentService() {

    }

    @Override
    public void onReceiveServicePid(Context context, int pid) {
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();

        if (payload != null) {
            String data = new String(payload);
            LogUtils.d("push", "intentservice 推送消息：" + data + ",messageid:" + messageid + ",taskid:" + taskid);
            if (!TextUtils.isEmpty(data)){
                try {
                    JSONObject msg_obj = new JSONObject(data);
                    JSONObject obj_custom_content = msg_obj.optJSONObject("C");
                    if (obj_custom_content != null) {
                        JSONObject args = new JSONObject();
                        args.put("msg_id", obj_custom_content.optString("e", ""));
                        args.put("c_m", obj_custom_content.optString("c_m", ""));
                        AllPushManager.handlerMsgFromServerPush(context, data, messageid, taskid);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        /**获取到个推cid之后即上传，服务器推送都基于此来给设备发送推送消息*/
        if(!TextUtils.isEmpty(clientid)){
            SpUtils spUtils = SpUtils.getInstance(context);
            if (!TextUtils.equals(clientid, (String) spUtils.getSharedPreference(LastGeTuiReportClientID, "")) ||
                    Math.abs(System.currentTimeMillis() - (Long) spUtils.getSharedPreference(LastGeTuiClientIDReportTime, 0l)) >= 172800000) {
                /**如果两次clientId不同，或者距离上传统计时间已经超过了2天*/
                JSONObject args = new JSONObject();
                try {
                    args.put("cid", clientid);
                    args.put("push-sp", "getui");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //统计了将值暂存起来
                spUtils.put(LastGeTuiClientIDReportTime,System.currentTimeMillis());
                spUtils.put(LastGeTuiReportClientID,clientid);
            }
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {

    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }


}
