package cn.kuaikuai.trip.view.imageviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;

import cn.kuaikuai.base.activity.BaseActivity;
import cn.kuaikuai.trip.R;

/**
 * 1、图片浏览、缩放、拖动、自动居中
 * 传入参数：
 * 1、pic_paths 图片数组
 * 2、position 默认显示哪一个图片位置
 * 3、isAddData是否来自添加页面(来自添加页面则显示删除按钮，否则显示保存到图库按钮)
 */
public class ImageViewer extends BaseActivity {

    private TextView tv_title;
    private int curPosition = 0;
    private ArrayList<Item> imageList = new ArrayList<Item>();
    private HackyViewPager viewPager;
    private ImageAdapter adapter;

    public static void openImageViewerActivity(Activity activity, String[] images, int position) {
        Intent intent = new Intent(activity, ImageViewer.class);
        intent.putExtra("pic_paths", images);
        intent.putExtra("position", position);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer_new);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        String[] picPaths = getIntent().getStringArrayExtra("pic_paths");
        if (picPaths == null || picPaths.length < 1) {
            finish();
            return;
        }
        curPosition = getIntent().getIntExtra("position", 0);
        for (int i = 0; i < picPaths.length; i++) {
            Item item = new Item();
            item.imageUrl = picPaths[i];
            item.position = i;
            imageList.add(item);
        }//end for
        Init();
    }

    private void Init() {
        tv_title = (TextView) findViewById(R.id.textView_count);
        viewPager = (HackyViewPager) findViewById(R.id.hackyViewPager);
        viewPager.setOnPageChangeListener(new PageChangeListener());
        tv_title.setText(curPosition + 1 + "/" + imageList.size());
        handler.sendEmptyMessage(DECODE_DONE);
    }

    class PageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int i, float f, int j) {
        }

        @Override
        public void onPageSelected(final int i) {
            curPosition = i;
            handler.sendEmptyMessage(CHANGE_PIC);
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }
    }

    class ImageAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, final int position) {
            ItemView itemView = new ItemView(container.getContext());
            itemView.setImageUrl(imageList.get(position).imageUrl);
            itemView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageViewer.this.finish();
                }
            });
            itemView.setTag(position);
            container.addView(itemView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    class SortInteger implements Comparator {
        public int compare(Object o1, Object o2) {
            try {
                int t1 = Integer.valueOf(o1.toString());
                int t2 = Integer.valueOf(o2.toString());
                if (t1 > t2) {
                    return -1;
                } else if (t1 < t2) {
                    return 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    private static final int SAVE_FAIL = 1;
    private static final int DECODE_DONE = 2;
    private static final int CHANGE_PIC = 3;
    private static final int REFRESH_ADAPTER = 4;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (ImageViewer.this.isFinishing()) {
                return;
            }
            super.handleMessage(msg);
            switch (msg.what) {
                case DECODE_DONE:// decode完成，初始化imageView，加到scrollLayout中
                    if (adapter == null) {
                        adapter = new ImageAdapter();
                        viewPager.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    if (curPosition > 0) {
                        viewPager.setCurrentItem(curPosition, false);
                    }
                    break;
                case CHANGE_PIC:
                    tv_title.setText(String.valueOf(curPosition + 1) + "/" + imageList.size());
                    break;
                case REFRESH_ADAPTER:
                    if (adapter == null) {
                        adapter = new ImageAdapter();
                        viewPager.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    viewPager.setCurrentItem(curPosition);
                    break;
            }
        }
    };

    class Item {
        /**
         * 图片地址
         */
        public String imageUrl = "";
        /**
         * 传入数组中得位置
         */
        public int position = 0;
    }

    /***/
    class ItemView extends RelativeLayout {
        private Context ctx;
        private PhotoView photoView;
        private ProgressBar loadingBar;
        private OnClickListener onClickListener;
        /**
         * 缩略图
         */
        private Bitmap theSmallBitmap = null;
        /**
         * 图片是否加载完成
         */
        private boolean isLoadImageFinished = false;

        ItemView(Context context) {
            super(context);
            Init(context);
        }

        ItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            Init(context);
        }

        ItemView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            Init(context);
        }

        public boolean isLoadSuccess() {
            return isLoadImageFinished;
        }

        private void Init(Context ctx) {
            this.ctx = ctx;
            loadingBar = new ProgressBar(ctx, null, android.R.attr.progressBarStyleSmall);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
//            this.addView(loadingBar, params);

            params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            photoView = new PhotoView(ctx);
            photoView.setOnPhotoTapListener(onTap);
            photoView.setVisibility(VISIBLE);
            this.addView(photoView, params);
        }

        public void setImageUrl(String imageUrl) {
            photoView.loadUrl(imageUrl, R.drawable.blank);
        }

        @Override
        public void setOnClickListener(OnClickListener l) {
            onClickListener = l;
            photoView.setOnClickListener(onClickListener);
            super.setOnClickListener(l);
        }

        PhotoViewAttacher.OnPhotoTapListener onTap = new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (onClickListener != null) {
                    onClickListener.onClick(ItemView.this);
                }
            }
        };

        public Bitmap getTheViewImageBitmap() {
            return photoView.getImageBitmap();
        }

        public void recycleTheSmallBitmap() {
            if (theSmallBitmap != null && !theSmallBitmap.isRecycled()) {
                theSmallBitmap.recycle();
                theSmallBitmap = null;
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            recycleTheSmallBitmap();
            super.onDetachedFromWindow();
        }
    }//end ItemView

    @Override
    public boolean fitsSystemWindows() {
        return false;
    }

    @Override
    protected boolean enableSwipeBack() {
        return false;
    }
}
