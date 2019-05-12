package cn.kuaikuai.trip.share.shareprocessor;

import android.content.Intent;
import android.net.Uri;

import cn.kuaikuai.trip.share.ShareChannel;
import cn.kuaikuai.trip.share.ShareModel;
import cn.kuaikuai.trip.share.ShareUtils;


/**
 * Created by lyc on 2015/10/23.
 */
public class SMSShare extends ShareProcessorTemplate {

    public SMSShare(ShareUtils shareUtils) {
        super(shareUtils, ShareModel.TYPE_URL_AND_IMG);
    }

    @Override
    public boolean isLegal() {
        return true;
    }

    @Override
    public void doShare() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                String uriStr = "smsto:";
                Uri uri = Uri.parse(uriStr);
                Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //task 1085 短信分享加上内容
                intent.putExtra("sms_body", mContentBody + "\n" + mContentUrl + "  " + mContentTile);
                mActivity.startActivity(intent);

                onSuccess(ShareChannel.SMS);
            }
        });

    }

    @Override
    public void onFail(int resultCode) {

    }

    @Override
    public void onSuccess(String share_channel) {
        super.onSuccess(share_channel);
    }
}
