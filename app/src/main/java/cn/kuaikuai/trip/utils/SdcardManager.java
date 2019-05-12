package cn.kuaikuai.trip.utils;


import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.constant.WlConfig;

public class SdcardManager {

    public static boolean isSDCardAvailable() {
        // String shared = "shared", // 磁盘不可�?
        // removed = "removed", // 移除
        // checking = "checking", // 正在�?��
        String mounted = "mounted";// 正常
        String temp = android.os.Environment.getExternalStorageState();
        // UtilsManager.println("temp=="+temp);
        return temp.equals(mounted);
    }

    /**
     * 将分享图标复制到目标文件夹下
     *
     * @throws IOException
     */
    public static void copyIconToTheDir(Context ctx) throws IOException {
        File file = new File(WlConfig.SHARE_PATH);
        if (!isSDCardAvailable()) {
            return;
        }
        if (file.exists()) {
            return;
        }
        InputStream is = ctx.getResources().openRawResource(R.raw.share);
        FileOutputStream fos = new FileOutputStream(file);
        byte[] buffer = new byte[8192];
        int count = 0;
        while ((count = is.read(buffer)) > 0) {
            fos.write(buffer, 0, count);
        }
        fos.close();
        is.close();
    }
}
