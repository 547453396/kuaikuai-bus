package cn.kuaikuai.trip.share.shareprocessor;

import android.content.Intent;
import android.text.TextUtils;

import java.nio.charset.Charset;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.share.ShareModel;
import cn.kuaikuai.trip.share.ShareUtils;


/**
 * Created by liuyc on 2014/9/19.
 */
public class OtherAppShare extends ShareProcessorTemplate {


    public OtherAppShare(ShareUtils shareUtils) {
        super(shareUtils, ShareModel.TYPE_URL_AND_IMG);
        needNetImgOrLocalImg = 4;
    }

    @Override
    public boolean isLegal() {
        return true;
    }

    @Override
    public void doShare() {
        Intent it = new Intent(Intent.ACTION_SEND);
        it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String content = "";
        if (!TextUtils.isEmpty(mContentUrl)) {
            content = new String((mContentBody + " 链接：").getBytes(), Charset.forName("UTF-8")) + mContentUrl;
        } else {
            if (!TextUtils.isEmpty(mContentBody)) {
                content = new String(mContentBody.getBytes(), Charset.forName("UTF-8"));
            }
        }
        it.putExtra(Intent.EXTRA_TEXT, content);
        it.putExtra(Intent.EXTRA_SUBJECT, mActivity.getString(R.string.app_name));
        it.setType("text/plain");
        mActivity.startActivity(it);
    }

    @Override
    public void onSuccess(String share_channel) {
        super.onSuccess(share_channel);
    }

    @Override
    public void onFail(int resultCode) {

    }
}
