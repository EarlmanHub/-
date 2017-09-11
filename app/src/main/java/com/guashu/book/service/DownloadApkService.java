package com.guashu.book.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.guashu.book.R;
import com.guashu.book.bean.support.AppDownloadTask;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import static com.guashu.book.base.Constant.APK_URL;


public class DownloadApkService extends Service {
    private boolean isRunning;
    private String name = AppUtils.getResource().getString(R.string.app_name) + ".apk";
    private long mTaskId;
    private DownloadManager mDownloadManager;
    private String downloadPath;

    @Override
    public void onCreate() {
        super.onCreate();
        downloadPath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).
                        getAbsolutePath() + File.separator + name;
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppDownloadTask appDownloadTask = new AppDownloadTask();
        appDownloadTask.setAppUrl(APK_URL);
        post(appDownloadTask);
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    public static void post(AppDownloadTask appDownloadTask) {
        EventBus.getDefault().post(appDownloadTask);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public synchronized void onPostAppDownloadTask(AppDownloadTask appDownloadTask) {
        if (appDownloadTask == null || appDownloadTask.getAppUrl() == null || isRunning) {
            return;
        }
        doDownload(appDownloadTask.getAppUrl());
    }

    private void doDownload(final String url) {
        isRunning = true;
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载
        //设置文件类型，可以在下载结束后自动打开该文件
//        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
//        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
//        request.setMimeType(mimeString);
        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
        //sdcard的目录下的download文件夹，必须设置
        File file = new File(downloadPath);
        if (file.exists()) {
            file.delete();
        }
        request.setDestinationInExternalPublicDir("/download/", name);

        mTaskId = mDownloadManager.enqueue(request);
        //注册广播接收者，监听下载状态
        registerReceiver(receiver,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //广播接受者，接收下载状态
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            checkDownloadStatus();//检查下载状态
        }
    };

    //检查下载状态
    private void checkDownloadStatus() {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(mTaskId);//筛选下载任务，传入任务ID，可变参数
        Cursor c = mDownloadManager.query(query);
        if (c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    ToastUtils.showSingleToast("下载暂停");
                case DownloadManager.STATUS_PENDING:
                    ToastUtils.showSingleToast("下载延迟");
                case DownloadManager.STATUS_RUNNING:
                    ToastUtils.showSingleToast("正在下载");
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    ToastUtils.showSingleToast("下载完成"); //下载完成安装APK
                    isRunning = false;
                    LogUtils.e("TEST", downloadPath);
                    AppUtils.install(downloadPath);
                    stopSelf();
                    break;
                case DownloadManager.STATUS_FAILED:
                    ToastUtils.showSingleToast("下载失败");
                    isRunning = false;
                    stopSelf();
                    break;
            }
        }
    }
}
