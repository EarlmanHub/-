package com.guashu.book.ui.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.guashu.book.R;
import com.tencent.tmselfupdatesdk.ITMSelfUpdateListener;
import com.tencent.tmselfupdatesdk.TMSelfUpdateManager;
import com.tencent.tmselfupdatesdk.YYBDownloadListener;
import com.tencent.tmselfupdatesdk.model.TMSelfUpdateUpdateInfo;

public class YYBTestActivity extends AppCompatActivity {
    private TMSelfUpdateManager selfUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initTMSelfUpdateManager();
    }

    private void initTMSelfUpdateManager() {
        //自更新 sdk 初始化
        selfUpdateManager = TMSelfUpdateManager.getInstance();
        Context context = getApplicationContext();
        String channelid = "1003079";// 应用宝渠道包的渠道号
        ITMSelfUpdateListener selfupdateListener = new UpdateListener();
        YYBDownloadListener yybDownloadListener = new DownloadListener();
        selfUpdateManager.init(context, channelid, selfupdateListener, yybDownloadListener, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selfUpdateManager.onActivityResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        selfUpdateManager.destroy();
    }

    public void click(View v) {
        switch (v.getId()) {
            case R.id.checkSelfUpdate:
                break;
            case R.id.startSelfUpdate:
                break;
            case R.id.checkYYBInstallState:
                break;
        }
        selfUpdateManager.checkSelfUpdate();
    }

    /**
     * 自更新状态监听器
     */
    class UpdateListener implements ITMSelfUpdateListener {
        /**
         * 更新包下载状态变化的处理逻辑
         *
         * @param i
         * @param i1
         * @param s
         */
        @Override
        public void onDownloadAppStateChanged(int i, int i1, String s) {
        }

        /**
         * 更新包下载进度发生变化的处理逻辑
         *
         * @param l
         * @param l1
         */
        @Override
        public void onDownloadAppProgressChanged(long l, long l1) {

        }

        /**
         * 收到更新信息的处理逻辑
         *
         * @param tmSelfUpdateUpdateInfo
         */
        @Override
        public void onUpdateInfoReceived(TMSelfUpdateUpdateInfo tmSelfUpdateUpdateInfo) {

        }
    }

    /**
     * 应用宝下载状态监听器
     */
    class DownloadListener implements YYBDownloadListener {
        /**
         * 应用宝下载状态变化的处理逻辑
         *
         * @param s
         * @param i
         * @param i1
         * @param s1
         */
        @Override
        public void onDownloadYYBStateChanged(String s, int i, int i1, String s1) {

        }

        /**
         * 应用宝下载进度变化的处理逻辑
         *
         * @param s
         * @param l
         * @param l1
         */
        @Override
        public void onDownloadYYBProgressChanged(String s, long l, long l1) {

        }

        /**
         * @param s
         * @param i
         * @param l
         * @param l1
         */
        @Override
        public void onCheckDownloadYYBState(String s, int i, long l, long l1) {

        }
    }
}
