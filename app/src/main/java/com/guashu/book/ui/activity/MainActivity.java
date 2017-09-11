/**
 * Copyright (c) 2016, smuyyh@gmail.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.guashu.book.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.menu.MenuBuilder;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.guashu.book.R;
import com.guashu.book.base.BaseActivity;
import com.guashu.book.base.Constant;
import com.guashu.book.bean.support.AgreeDisclaimerEvent;
import com.guashu.book.common.OnCheckUpdataListener;
import com.guashu.book.component.AppComponent;
import com.guashu.book.component.DaggerMainComponent;
import com.guashu.book.manager.SettingManager;
import com.guashu.book.manager.UpdataManager;
import com.guashu.book.service.DownloadApkService;
import com.guashu.book.service.DownloadBookService;
import com.guashu.book.ui.fragment.CommunityFragment;
import com.guashu.book.ui.fragment.RecommendFragment;
import com.guashu.book.utils.FileUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.NetworkUtils;
import com.guashu.book.utils.SharedPreferencesUtil;
import com.guashu.book.utils.ToastUtils;
import com.guashu.book.view.RVPIndicator;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;

import static com.guashu.book.base.Constant.KEY_IS_NEVEL;

public class MainActivity extends BaseActivity {
    private static final String CUR_POS = "currentPos";
    @Bind(R.id.indicator)
    RVPIndicator mIndicator;
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    private RecommendFragment recommendFragment;
    private List<Fragment> mTabContents;
    private FragmentPagerAdapter mAdapter;
    private List<String> mDatas;

    // 退出时间
    private long currentBackPressedTime = 0;
    // 退出间隔
    private static final int BACK_PRESSED_INTERVAL = 2000;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
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
        setTitle(getString(R.string.app_name));
    }

    @Override
    public void initDatas() {
        startService(new Intent(this, DownloadBookService.class));
        mDatas = Arrays.asList(getResources().getStringArray(R.array.home_tabs));
        mTabContents = new ArrayList<>();
        recommendFragment = new RecommendFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_IS_NEVEL, true);
        recommendFragment.setArguments(bundle);
        mTabContents.add(recommendFragment);
        mTabContents.add(new CommunityFragment());
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };
    }

    /**
     * 显示免责声明对话框
     */
    private void showDisclaimerDialog() {
        String s = FileUtils.readDisclaimer();
        new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setTitle(getString(R.string.disclaimer))
                .setMessage(s)
                .setPositiveButton(getString(R.string.agree), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//同意
                        dialog.dismiss();
                        EventBus.getDefault().post(new AgreeDisclaimerEvent());
                        checkUpdata();
                    }
                })
                .setNegativeButton(getString(R.string.disagree), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//取消
                        finish();
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void checkUpdata() {
        if (!NetworkUtils.isAvailable(this)) {
            return;
        }
        UpdataManager.getInstance().checkUpdata(new OnCheckUpdataListener() {
            @Override
            public void onStart() {
                LogUtils.e("checkUpdata onStart");
            }

            @Override
            public void onSucced(boolean isUpdata) {
                LogUtils.e("checkUpdata onSucced = " + isUpdata);
                if (isUpdata) {
                    showUpdataDialog();
                }
            }

            @Override
            public void onFailed() {
                LogUtils.e("checkUpdata onFailed");
            }

            @Override
            public void onFinish() {
                LogUtils.e("checkUpdata onFinish");
            }
        });
    }

    private void showUpdataDialog() {
        new android.support.v7.app.AlertDialog.Builder(mContext)
                .setCancelable(true)
                .setTitle("检测到有新版本")
                .setMessage("请问是否下载新版本？")
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//同意
                        dialog.dismiss();
                        UpdataManager.getInstance().downloadApk(MainActivity.this);
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

    @Override
    public void configViews() {

    }

    @Override
    public void configViews(Bundle savedInstanceState) {
        mIndicator.setTabItemTitles(mDatas);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mDatas.size());
        int currentPos = 0;
        if (savedInstanceState != null) {
            currentPos = savedInstanceState.getInt(CUR_POS);
        }
        mIndicator.setViewPager(mViewPager, currentPos);

        if (!SettingManager.getInstance().isUserAgreeDis()) {
            showDisclaimerDialog();
        } else if (savedInstanceState == null) {
            checkUpdata();
        }
    }

    public void setCurrentItem(int position) {
        mViewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CUR_POS, mViewPager.getCurrentItem());// 把当前positon保存
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                startActivity(new Intent(MainActivity.this, SearchActivity.class));
                break;
            case R.id.action_scan_local_book:
                ScanLocalBookActivity.startActivity(this);
                break;
            case R.id.action_night_mode:
                if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                recreate();
                break;
            case R.id.action_wifi_book:
                WifiBookActivity.startActivity(this);
                break;
            case R.id.action_settings:
                SettingActivity.startActivity(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (recommendFragment.onBackPressed()) {
            return true;
        }
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
                currentBackPressedTime = System.currentTimeMillis();
                ToastUtils.showToast(getString(R.string.exit_tips));
                return true;
            } else {
                finish(); // 退出
            }
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * 显示item中的图片；
     *
     * @param view
     * @param menu
     * @return
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DownloadBookService.cancel();
        stopService(new Intent(this, DownloadBookService.class));
    }
}