/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guashu.book.ui.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.guashu.book.R;
import com.guashu.book.base.BaseActivity;
import com.guashu.book.base.Constant;
import com.guashu.book.bean.support.AgreeDisclaimerEvent;
import com.guashu.book.common.OnCheckUpdataListener;
import com.guashu.book.component.AppComponent;
import com.guashu.book.component.DaggerMainComponent;
import com.guashu.book.manager.CacheManager;
import com.guashu.book.manager.EventManager;
import com.guashu.book.manager.SettingManager;
import com.guashu.book.manager.UpdataManager;
import com.guashu.book.service.DownloadApkService;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.NetworkUtils;
import com.guashu.book.utils.SharedPreferencesUtil;
import com.guashu.book.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by xiaoshu on 2016/10/8.
 */
public class SettingActivity extends BaseActivity {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    @Bind(R.id.mTvSort)
    TextView mTvSort;
    @Bind(R.id.tvFlipStyle)
    TextView mTvFlipStyle;
    @Bind(R.id.tvCacheSize)
    TextView mTvCacheSize;
    @Bind(R.id.noneCoverCompat)
    SwitchCompat noneCoverCompat;


    @Override
    public int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("设置");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final String cachesize = CacheManager.getInstance().getCacheSize();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvCacheSize.setText(cachesize);
                    }
                });

            }
        }).start();
        mTvSort.setText(getResources().getStringArray(R.array.setting_dialog_sort_choice)[
                SharedPreferencesUtil.getInstance().getBoolean(Constant.ISBYUPDATESORT, true) ? 0 : 1]);
        mTvFlipStyle.setText(getResources().getStringArray(R.array.setting_dialog_style_choice)[
                SharedPreferencesUtil.getInstance().getInt(Constant.FLIP_STYLE, 0)]);
    }


    @Override
    public void configViews() {
        noneCoverCompat.setChecked(SettingManager.getInstance().isNoneCover());
        noneCoverCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingManager.getInstance().saveNoneCover(isChecked);
            }
        });
    }

    @OnClick(R.id.bookshelfSort)
    public void onClickBookShelfSort() {
        new AlertDialog.Builder(mContext)
                .setTitle("书架排序方式")
                .setSingleChoiceItems(getResources().getStringArray(R.array.setting_dialog_sort_choice),
                        SharedPreferencesUtil.getInstance().getBoolean(Constant.ISBYUPDATESORT, true) ? 0 : 1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTvSort.setText(getResources().getStringArray(R.array.setting_dialog_sort_choice)[which]);
                                SharedPreferencesUtil.getInstance().putBoolean(Constant.ISBYUPDATESORT, which == 0);
                                EventManager.refreshCollectionList();
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    @OnClick(R.id.rlFlipStyle)
    public void onClickFlipStyle() {
        new AlertDialog.Builder(mContext)
                .setTitle("阅读页翻页效果")
                .setSingleChoiceItems(getResources().getStringArray(R.array.setting_dialog_style_choice),
                        SharedPreferencesUtil.getInstance().getInt(Constant.FLIP_STYLE, 0),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTvFlipStyle.setText(getResources().getStringArray(R.array.setting_dialog_style_choice)[which]);
                                SharedPreferencesUtil.getInstance().putInt(Constant.FLIP_STYLE, which);
                                dialog.dismiss();
                            }
                        })
                .create().show();
    }

    @OnClick(R.id.cleanCache)
    public void onClickCleanCache() {
        //默认不勾选清空书架列表，防手抖！！
        final boolean selected[] = {false, false};
        new AlertDialog.Builder(mContext)
                .setTitle("清除缓存")
                .setCancelable(true)
                .setMultiChoiceItems(new String[]{"清空书架列表", "清空其他缓存"}, selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        selected[which] = isChecked;
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!selected[0] && !selected[1]) {
                            return;
                        }

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CacheManager.getInstance().clearCache(selected[0], selected[1]);
                                final String cacheSize = CacheManager.getInstance().getCacheSize();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTvCacheSize.setText(cacheSize);
                                    }
                                });
                            }
                        }).start();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();
    }

    @OnClick(R.id.aboutUs)
    public void onClickAboutUs() {
        AboutUsActivity.startActivity(mContext);
    }

    @OnClick(R.id.checkUpdata)
    public void onCheckUpdata() {
        if (!NetworkUtils.isAvailable(this)) {
            ToastUtils.showSingleToast("当前网络不可用，请重试！");
            return;
        }
        UpdataManager.getInstance().checkUpdata(new OnCheckUpdataListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSucced(boolean isUpdata) {
                if (isUpdata) {
                    showUpdataDialog();
                } else {
                    ToastUtils.showSingleToast("当前为最新版本！");
                }
            }

            @Override
            public void onFailed() {
            }

            @Override
            public void onFinish() {

            }
        });
    }

    private void showUpdataDialog() {
        new android.support.v7.app.AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle("检测到有新版本")
                .setMessage("请问是否下载新版本？")
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//同意
                        dialog.dismiss();
                        UpdataManager.getInstance().downloadApk(SettingActivity.this);
                    }
                })
                .setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//取消
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    @OnClick(R.id.disclaimer)
    public void onDisclaimer() {
        DisclaimerActivity.startActivity(mContext);
    }
}
