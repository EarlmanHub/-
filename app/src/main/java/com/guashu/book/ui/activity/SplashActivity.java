package com.guashu.book.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.guashu.book.R;
import com.guashu.book.ReaderApplication;
import com.guashu.book.utils.AppUtils;

import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private boolean flag = false;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReaderApplication application= (ReaderApplication)getApplication();
        if(application.isNeedSplash()) {
            application.setNeedSplash(false);
            setContentView(R.layout.activity_splash);
            ButterKnife.bind(this);
            runnable = new Runnable() {
                @Override
                public void run() {
                    goHome();
                }
            };
            AppUtils.runOnUIDelayed(runnable, 2000);
        }else{

            goHome();

        }
    }

    private synchronized void goHome() {
        if (!flag) {
            flag = true;
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
        ButterKnife.unbind(this);
    }
}
