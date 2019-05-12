//package cn.kuaikuai.trip.share;
//
//import android.view.View;
//
//import java.util.HashMap;
//
//import cn.kuaikuai.trip.R;
//
//
///**
// * Created by liheng on 17/11/20.
// * 分享框 更多中的一些扩展操作
// */
//public class ShareMoreData {
//    public static final int JUBAO = 1;//举报
//    public static final int DELETE = 2;//删除
//    public static final int SAVE_NOTE = 3;//保存到记事
//    public static final int MOVE_GROUP = 4;//移动分组
//    public static final int SEND_NOTE = 5;//发送到桌面
//    public static final int ZHIDING = 6;//置顶
//    public static final int CANCEL_ZHIDING = 7;//取消置顶
//    public static final int TIME_PICTURE_DOWNLOAD = 8;//时光图册下载
//    public static final int TIME_PICTURE_ACTIVITY = 9;//时光图册界面
//    public static final int MORE = 10;    //更多
//
//    public static HashMap<Integer,Integer> shareMoreRes = new HashMap<>();
//
//    static {
//        shareMoreRes.put(DELETE, R.drawable.icon_share_delete);
//        shareMoreRes.put(SAVE_NOTE, R.drawable.icon_share_save);
//        shareMoreRes.put(MOVE_GROUP, R.drawable.icon_share_packet);
//        shareMoreRes.put(SEND_NOTE, R.drawable.icon_share_add);
//        shareMoreRes.put(ZHIDING, R.drawable.icon_share_top);
//        shareMoreRes.put(CANCEL_ZHIDING, R.drawable.icon_share_cancel_top);
//        shareMoreRes.put(TIME_PICTURE_DOWNLOAD, R.drawable.icon_share_download);
//        shareMoreRes.put(TIME_PICTURE_ACTIVITY, R.drawable.icon_share_shiguangtuce);
//        shareMoreRes.put(MORE, R.drawable.share_icon_other);
//    }
//
//    public static HashMap<Integer,Integer> shareMoreNameRes = new HashMap<>();
//
//    static {
//        shareMoreNameRes.put(JUBAO,R.string.jubao);
//        shareMoreNameRes.put(DELETE,R.string.delete);
//        shareMoreNameRes.put(MORE, R.string.other);
//    }
//
//    public interface OnShareItemClickListener {
//        void onItemClick(View v, int key);
//    }
//}
