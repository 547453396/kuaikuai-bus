package cn.kuaikuai.trip.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.kuaikuai.trip.constant.WlConfig;

public class CompressPicture {
    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    /**
     * 按照宽度读取一个图片
     */
    public Bitmap compressWithWidth(String picPath, float width) {
        if (picPath == null || picPath.length() == 0) {
            return null;
        }
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // 获取这个图片的宽和高
            BitmapFactory.decodeFile(picPath, options); // 此时返回bm为空
            try {
                options.inJustDecodeBounds = false;
                int be = 1;
                if (width > 0) {
                    be = (int) (options.outWidth / width);
                }
                if (be <= 0)
                    be = 1;
                options.inSampleSize = be;
                return BitmapFactory.decodeFile(picPath, options);
            } catch (OutOfMemoryError e) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将图片压缩到1080*1920左右
     *
     * @param picPath
     * @param orientation
     * @return
     */
    public static String compressImageTo(String picPath, int orientation) {
        if (TextUtils.isEmpty(picPath)) {
            return null;
        }

        File fromFile = new File(picPath);
        if (!fromFile.exists()) {
            return null;
        }

        File dir = new File(WlConfig.imageDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String name = System.currentTimeMillis() + picPath.substring(picPath.lastIndexOf(".") + 1);
        File toFile = new File(WlConfig.imageDir + name);
        if (toFile.exists()) {
            if (toFile.length() > 0) {
                return WlConfig.imageDir + name;
            }
        } else {
            try {
                toFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                if (toFile.exists()) {
                    toFile.delete();
                }
            }
        }

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            // 获取这个图片的宽和高
            Bitmap bitmap; // 此时返回bm为空
            try {
                options.inJustDecodeBounds = false;

                int w = options.outWidth;
                int h = options.outHeight;
                float hh = 1920f;
                float ww = 1080f;

                //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
                int be = 1;//be=1表示不缩放
                if (h >= w) {
                    if (h > 1920 && w > 1080) {
                        hh = 1920;
                        ww = 1080;
                    }
                } else {
                    if (h > 1080 && w > 1920) {
                        hh = 1080;
                        ww = 1920;
                    }
                }

                if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
                    be = (int) (options.outWidth / ww);
                } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
                    be = (int) (options.outHeight / hh);
                }
                if (be <= 0)
                    be = 1;
                options.inSampleSize = be;//设置缩放比例
                bitmap = BitmapFactory.decodeFile(picPath, options);
                if (orientation != 0) {
                    Matrix m = new Matrix();
                    m.setRotate(orientation);
                    Bitmap b2 = Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                    if (bitmap != b2) {
                        bitmap.recycle();  //Android开发网再次提示Bitmap操作完应该显示的释放
                        bitmap = b2;
                    }
                }

                try {
                    FileOutputStream out = new FileOutputStream(toFile);
                    if (picPath.toLowerCase().endsWith(".png")) {
                        if (bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)) {
                            out.flush();
                            out.close();
                        }
                    } else {
                        if (bitmap
                                .compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                            out.flush();
                            out.close();
                        }
                    }
                    bitmap.recycle();
                } catch (FileNotFoundException e) {

                    e.printStackTrace();
                    return null;
                } catch (IOException e) {

                    e.printStackTrace();
                    if (toFile.exists()) {
                        toFile.delete();
                    }
                    return null;
                }
                return WlConfig.imageDir + name;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                if (toFile.exists()) {
                    toFile.delete();
                }
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (toFile.exists()) {
                toFile.delete();
            }
            return null;
        }
    }


    public Bitmap extractThumbNail(final String path, final int height,
                                   final int width, final boolean crop) {
        if (TextUtils.isEmpty(path) || height <= 0 || width <= 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        try {
            options.inJustDecodeBounds = true;
            Bitmap tmp = BitmapFactory.decodeFile(path, options);
            if (tmp == null) {
                return null;
            }
            tmp.recycle();
            final double beY = options.outHeight * 1.0 / height;
            final double beX = options.outWidth * 1.0 / width;
            options.inSampleSize = (int) (crop ? (beY > beX ? beX : beY) : (beY < beX ? beX : beY));
            if (options.inSampleSize <= 1) {
                options.inSampleSize = 1;
            }
            // NOTE: out of memory error
            while (options.outHeight * options.outWidth / options.inSampleSize > MAX_DECODE_PICTURE_SIZE) {
                options.inSampleSize++;
            }
            int newHeight = height;
            int newWidth = width;
            if (crop) {
                if (beY > beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            } else {
                if (beY < beX) {
                    newHeight = (int) (newWidth * 1.0 * options.outHeight / options.outWidth);
                } else {
                    newWidth = (int) (newHeight * 1.0 * options.outWidth / options.outHeight);
                }
            }
            options.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeFile(path, options);
            if (bm == null) {
                return null;
            }
            final Bitmap scale = Bitmap.createScaledBitmap(bm, newWidth, newHeight, true);
            if (scale != null && scale != bm) {
                bm = scale;
            }
            if (crop) {
                final Bitmap cropped = Bitmap.createBitmap(bm, (bm.getWidth() - width) >> 1,
                        (bm.getHeight() - height) >> 1, width, height);
                if (cropped == null) {
                    return bm;
                }
                bm = cropped;
            }
            return bm;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
        if (bmp == null) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }
        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
