package com.guashu.book.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.guashu.book.R;
import com.guashu.book.base.BaseActivity;
import com.guashu.book.component.AppComponent;
import com.guashu.book.utils.FileUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.ScreenUtils;

import butterknife.Bind;
import butterknife.OnClick;

public class DisclaimerActivity extends BaseActivity {
    @Bind(R.id.tv_disclaimer)
    TextView mTvDisclaimer;
    private String mDisclaimer;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, DisclaimerActivity.class));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_disclaimer;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("免责声明");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        mDisclaimer = FileUtils.readDisclaimer();
    }

    @Override
    public void configViews() {
        if (mDisclaimer != null) {
            mTvDisclaimer.setText(mDisclaimer);
        }
    }

    @OnClick(R.id.tv_disclaimer)
    public void vlick() {
        LogUtils.e("ScreenUtils.getScreenHeight() = " + ScreenUtils.getScreenHeight());
        LogUtils.e("ScreenUtils.getScreenHeight1() = " + ScreenUtils.getScreenHeight1());
        LogUtils.e("ScreenUtils.getBottomStatusHeight() = " + ScreenUtils.getBottomStatusHeight(this));
    }
}
