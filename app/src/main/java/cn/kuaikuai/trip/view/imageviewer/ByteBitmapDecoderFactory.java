package cn.kuaikuai.trip.view.imageviewer;

import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;

import java.io.IOException;

/**
 * Created by admin on 2017/8/1.
 */
public class ByteBitmapDecoderFactory implements BitmapDecoderFactory {
    private byte[] data;

    public ByteBitmapDecoderFactory(byte[] data) {
        this.data = data;
    }

    @Override
    public BitmapRegionDecoder made() throws IOException {
        return BitmapRegionDecoder.newInstance(data, 0, data.length, false);
    }

    @Override
    public int[] getImageInfo() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);
        return new int[]{options.outWidth, options.outHeight};
    }
}
