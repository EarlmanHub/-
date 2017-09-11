package com.guashu.book.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.guashu.book.R;
import com.guashu.book.base.BaseActivity;
import com.guashu.book.component.AppComponent;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.ToastUtils;

import butterknife.Bind;
import butterknife.OnLongClick;

public class AboutUsActivity extends BaseActivity {
    @Bind(R.id.tv_version_code)
    TextView mTvVersionCode;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, AboutUsActivity.class));
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about_us;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("关于我们");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {

    }

    @Override
    public void configViews() {
        String appVersionName = AppUtils.getAppVersionName();
        mTvVersionCode.setText(appVersionName);
    }

    @OnLongClick(R.id.tv_version_code)
    public boolean click() {
        ToastUtils.showSingleToast(AppUtils.getAppVersionCode() + "");
        return true;
    }


}
