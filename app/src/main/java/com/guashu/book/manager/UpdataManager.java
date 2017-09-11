package com.guashu.book.manager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.guashu.book.base.Constant;
import com.guashu.book.common.OnCheckUpdataListener;
import com.guashu.book.service.DownloadApkService;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by user on 17-8-8.
 */

public class UpdataManager {
    private boolean isRunning;

    private static UpdataManager manager;

    public static UpdataManager getInstance() {
        return manager == null ? (manager = new UpdataManager()) : manager;
    }

    /**
     * 检测服务器apk版本
     *
     * @return
     */
    public void checkUpdata(final OnCheckUpdataListener listener) {
        if (listener == null || isRunning) {
            return;
        }
        new AsyncTask<Void, Void, Boolean>() {
            Boolean isUpdata;

            @Override
            protected void onPreExecute() {
                isRunning = true;
                listener.onStart();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                LogUtils.e("TEST", " doInBackground ");
                URL url;

                try {
                    url = new URL(Constant.APK_VER_URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
                HttpURLConnection conn = null;
                try {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(1000 * 5);
                    conn.setReadTimeout(5000);
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        return null;
                    }
                    InputStream is = conn.getInputStream();
                    byte[] bytes = new byte[200];
                    int read;
                    if ((read = is.read(bytes)) > 0) {
                        String s = new String(bytes, 0, read);
                        int versionCode;
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            versionCode = jsonObject.getInt("version");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            versionCode = -1;
                        }
                        LogUtils.e("versionCode = " +
                                versionCode);
                        if (versionCode > AppUtils.getAppVersionCode()) {
                            isUpdata = true;
                        } else {
                            isUpdata = false;
                        }
                        return isUpdata;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                } finally {
                    if (conn != null) {
                        //关闭网络连接
                        conn.disconnect();
                    }
                }
                return isUpdata;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                isRunning = false;
                if (aBoolean != null) {
                    listener.onSucced(aBoolean);
                } else {
                    listener.onFailed();
                }
                listener.onFinish();
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void downloadApk(AppCompatActivity appCompatActivity) {
        if (!NetworkUtils.isWifiConnected(AppUtils.getAppContext())) {
            new AlertDialog.Builder(appCompatActivity)
                    .setTitle("温馨提示")
                    .setMessage("当前网络环境不是无线网络，请问是否继续下载？本次下载会消耗少量流量。")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            AppUtils.getAppContext().startService(new Intent(AppUtils.getAppContext(), DownloadApkService.class));
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create().show();
        } else {
            AppUtils.getAppContext().startService(new Intent(AppUtils.getAppContext(), DownloadApkService.class));
        }
    }
}