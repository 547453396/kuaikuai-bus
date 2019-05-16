package cn.kuaikuai.trip.main.line;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import cn.kuaikuai.base.activity.BaseActivity;
import cn.kuaikuai.common.image.ETNetImageView;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.customview.ETIconButtonTextView;
import cn.kuaikuai.trip.customview.LoadingView;
import cn.kuaikuai.trip.main.line.presenter.PathPresenter;
import cn.kuaikuai.trip.main.line.view.IPathView;
import cn.kuaikuai.trip.utils.UtilsManager;

public class AddLineActivity extends BaseActivity implements IPathView {
    @BindView(R.id.btn_back)
    ETIconButtonTextView mBtnBack;
    @BindView(R.id.et_icon)
    ETNetImageView mEtIcon;
    @BindView(R.id.btn_ok)
    Button mBtnOk;
    @BindView(R.id.loading)
    LoadingView loading;
    @BindView(R.id.tv_line)
    TextView mTvLine;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_time)
    TextView mTvTime;

    private PathPresenter pathPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_line);
        pathPresenter = new PathPresenter(mActivity, this);
        initView();
    }

    private void initView() {
        mBtnBack.setVisibility(View.VISIBLE);
        mEtIcon.setVisibility(View.GONE);
        mTvTitle.setText(R.string.btn_add_path);
        mTvLine.setText("点击添加线路");
        mTvTime.setText("2019-5-16 09:00:00");
    }

    @Override
    protected void onDestroy() {
        if (loading != null) {
            loading.dismiss();
        }
        super.onDestroy();
    }

    @OnClick(R.id.tv_line)
    protected void clickLinePath() {

    }

    @OnClick(R.id.btn_ok)
    protected void clickOk() {
        loading.showLoading();
        JSONObject body = new JSONObject();
        try {
            body.put("lineId",4);
            body.put("seatNum",4);
            JSONArray seatList = new JSONArray();
            seatList.put("A1");
            seatList.put("B1");
            seatList.put("B2");
            seatList.put("B3");
            body.put("seatList",seatList);
            body.put("startTime","2019-5-16 09:00:00");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        pathPresenter.addLinePath(body.toString());
    }

    @OnClick(R.id.btn_back)
    void close() {
        finish();
    }

    @Override
    public void onAddSucceed() {
        loading.dismiss();
        close();
    }

    @Override
    public void onAddFailed(String msg) {
        loading.dismiss();
        if (!TextUtils.isEmpty(msg)) {
            UtilsManager.Toast(mActivity, msg);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            close();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
