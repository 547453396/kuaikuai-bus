package cn.kuaikuai.trip.main.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.OnClick;
import cn.kuaikuai.base.activity.BaseActivity;
import cn.kuaikuai.common.image.ETImageView;
import cn.kuaikuai.common.image.ETNetImageView;
import cn.kuaikuai.trip.R;
import cn.kuaikuai.trip.customview.ETIconButtonTextView;
import cn.kuaikuai.trip.main.login.LoginPhoneActivity;
import cn.kuaikuai.trip.main.login.LoginUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.btn_back)
    ETIconButtonTextView mBtnBack;
    @BindView(R.id.et_icon)
    ETNetImageView mEtIcon;
    @BindView(R.id.btn_add)
    Button mBtnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        mBtnBack.setVisibility(View.GONE);
        mEtIcon.setVisibility(View.VISIBLE);
        mEtIcon.setDisplayMode(ETImageView.CIRCLE);
    }

    @OnClick(R.id.et_icon)
    protected void clickIcon() {
        if (LoginUtils.isLoginSuccess(getApplicationContext())){
            startActivity(new Intent(this, LoginPhoneActivity.class));
        }else {
            startActivity(new Intent(this, LoginPhoneActivity.class));
        }
    }

    @OnClick(R.id.btn_add)
    protected void clickAdd() {
        startActivity(new Intent(this, LoginPhoneActivity.class));
    }
}
