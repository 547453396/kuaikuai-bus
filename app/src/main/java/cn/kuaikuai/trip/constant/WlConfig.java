package cn.kuaikuai.trip.constant;

import android.os.Environment;

/**
 * Created by liheng on 18/7/6.
 * 保存一些配置信息
 */

public interface WlConfig {

    String appDir = Environment.getExternalStorageDirectory().getPath() + "/.wlcc/";
    String voiceDir = appDir + "voice/";
    String imageDir = appDir + "image/";
    String nimDir = appDir + "nim/";
    String tempDir = appDir + "temp/";
    String SHARE_PATH = appDir + "wlcc_share_icon.jpg";

    String APK_DOWN_URL = "http://sj.qq.com/myapp/detail.htm?apkName=cn.kuaikuai.trip";
    String SHARE_URL = "http://wlcc-img.weli010.cn/2018/08/13/upload_wo4exbm7pimfpu8t6qe44h9ur297r5ev.png";

    String wx_appId = "wxd0fbdf55b43fdc4d";
    String wx_appSecret = "c06f7ed33953ed31b81347ec9286c9a3";
    //微信获取access_token地址
    String WX_access_token_Url = "https://api.weixin.qq.com/sns/oauth2/access_token?";

    /**
     * 服务器所需appkey
     */
    String appkey = "61452801";
    String appSecret = "7127b568770f49c798ea0b1263ab15fe";
}
