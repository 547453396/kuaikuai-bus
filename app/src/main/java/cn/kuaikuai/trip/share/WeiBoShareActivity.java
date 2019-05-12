/*
 * Copyright (C) 2010-2013 The SINA WEIBO Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.kuaikuai.trip.share;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

import cn.kuaikuai.base.activity.BaseActivity;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.ShareConstant;
import cn.kuaikuai.trip.customview.LoadingView;
import cn.kuaikuai.trip.share.shareprocessor.ShareCallBack;
import cn.kuaikuai.trip.utils.CompressPicture;
import cn.kuaikuai.common.image.DownImage;
import cn.kuaikuai.trip.utils.UtilsManager;
import cn.kuaikuai.common.LogUtils;


/**
 * 该类演示了第三方应用如何通过微博客户端
 * 执行流程： 从本应用->微博->本应用
 *
 * @author SINA
 * @since 2013-10-22
 */
public class WeiBoShareActivity extends BaseActivity implements OnClickListener, IWeiboHandler.Response {

    public LoadingView root;

    public static final String KEY_SHARE_TYPE = "key_share_type";
    public static final int SHARE_CLIENT = 1;

    /**
     * 微博微博分享接口实例
     */
    private IWeiboShareAPI mWeiboShareAPI = null;

    private int mShareType = SHARE_CLIENT;

    private String localImg, netImg, text;

    public static ShareCallBack shareCallBack;

    public static void shareMsg2WeiBo(Activity act, String text, String img) {
        Intent it = new Intent(act, WeiBoShareActivity.class);
        it.putExtra("text", text);
        it.putExtra("img", img);
        act.startActivity(it);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = new LoadingView(this, null);
        setContentView(root);
        root.setOnClickListener(this);
        mShareType = getIntent().getIntExtra(KEY_SHARE_TYPE, SHARE_CLIENT);


        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, ShareConstant.SINA_APP_KEY);
        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();

        // 当 Activity 被重新初始化时（该 Activity 处于后台时，可能会由于内存不足被杀掉了），
        // 需要调用 {@link IWeiboShareAPI#handleWeiboResponse} 来接收微博客户端返回的数据。
        // 执行成功，返回 true，并调用 {@link IWeiboHandler.Response#onResponse}；
        // 失败返回 false，不调用上述回调
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        passIntent();
    }

    /**
     * 处理数据并且判断分类型
     */
    private void passIntent() {
        String img = getIntent().getStringExtra("img");
        if (!TextUtils.isEmpty(img)
                && (img.startsWith("http://") || img.startsWith("https://"))) {//网络图片
            netImg = img;
        } else {
            localImg = img;
        }
        text = getIntent().getStringExtra("text");
        if (!TextUtils.isEmpty(localImg)) {
            sendMultiMessage(null);
            root.dismiss();
        } else if (!TextUtils.isEmpty(netImg)) {
            DownImage.imageBitmap(this, netImg, new DownImage.BitmapCallback() {
                @Override
                public void onSuccessBitmap(Bitmap bitmap) {
                    root.dismiss();
                    if (bitmap != null) {
                        sendMultiMessage(bitmap);
                    }
                }

                @Override
                public void onFail(Throwable throwable) {
                    finish();
                }
            });
        } else {
            sendMultiMessage(null);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        boolean result = mWeiboShareAPI.handleWeiboResponse(intent, this);
        finish();
    }

    boolean isFirst = true;

    @Override
    protected void onResume() {
        super.onResume();
        if (isFirst) {
            isFirst = false;
        } else {
            finish();
        }
        LogUtils.i("WeiBoShareActivity onResume");
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        LogUtils.i("WeiBoShareActivity onResponse");
        if (baseResp != null) {
            switch (baseResp.errCode) {
                case WBConstants.ErrorCode.ERR_OK:
                    UtilsManager.Toast(this, R.string.share_success);
                    if (shareCallBack != null) {
                        shareCallBack.onSuccess();
                        shareCallBack = null;
                    }
                    break;
                case WBConstants.ErrorCode.ERR_CANCEL:
                    UtilsManager.Toast(this, R.string.share_cancel);
                    if (shareCallBack != null) {
                        shareCallBack.onFail(-1, "");
                        shareCallBack = null;
                    }
                    break;
                case WBConstants.ErrorCode.ERR_FAIL:
                    UtilsManager.Toast(this, getString(R.string.share_fail) + "Error Message: " + baseResp.errMsg);
                    if (shareCallBack != null) {
                        shareCallBack.onFail(-1, "");
                        shareCallBack = null;
                    }
                    break;
                default: {
                    shareCallBack = null;
                    break;
                }
            }
        }
        this.finish();
    }

    @Override
    public void onClick(View v) {
        this.finish();
    }


    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     */
    private void sendMultiMessage(Bitmap passInbitmap) {
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (!TextUtils.isEmpty(text)) {
            weiboMessage.textObject = new TextObject();
            weiboMessage.textObject.text = text;
        }
        CompressPicture compressPicture = new CompressPicture();
        if (!TextUtils.isEmpty(localImg)) {
            weiboMessage.imageObject = new ImageObject();
            //设置缩略图。 可以直接传原图进去 让微博帮忙压缩。
            Bitmap bitmap = BitmapFactory.decodeFile(localImg);
            //compressPicture.getBitmap4Share(bitmap);
            weiboMessage.imageObject.setImageObject(bitmap);
        } else {
            weiboMessage.imageObject = new ImageObject();
            //设置缩略图。 让微博帮忙压缩。
//            weiboMessage.imageObject.setImageObject(compressPicture.getBitmap4Share(passInbitmap));
            weiboMessage.imageObject.setImageObject(passInbitmap);
        }
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        // 3. 发送请求消息到微博，唤起微博分享界面
        if (mShareType == SHARE_CLIENT) {
            try {
                mWeiboShareAPI.sendRequest(WeiBoShareActivity.this, request);
            } catch (Exception e) {
                finish();
                UtilsManager.Toast(this, R.string.share_fail);
            }
        }
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }
}
