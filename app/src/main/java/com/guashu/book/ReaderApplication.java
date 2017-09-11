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
package com.guashu.book;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.guashu.book.base.Constant;
import com.guashu.book.base.CrashHandler;
import com.guashu.book.component.AppComponent;
import com.guashu.book.component.DaggerAppComponent;
import com.guashu.book.module.AppModule;
import com.guashu.book.module.BookApiModule;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.SharedPreferencesUtil;

/**
 * @author yuyh.
 * @date 2016/8/3.
 */
public class ReaderApplication extends Application {

    private static ReaderApplication sInstance;
    private AppComponent appComponent;
    private  boolean isNeedSplash=true;

    public void setNeedSplash(boolean needSplash) {
        isNeedSplash = needSplash;
    }

    public boolean isNeedSplash() {
        return isNeedSplash;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        initCompoent();
        AppUtils.init(this);
        CrashHandler.getInstance().init(this);
        initPrefs();
        initNightMode();
    }

    public static ReaderApplication getsInstance() {
        return sInstance;
    }

    private void initCompoent() {
        appComponent = DaggerAppComponent.builder()
                .bookApiModule(new BookApiModule())
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    /**
     * 初始化SharedPreference
     */
    protected void initPrefs() {
        SharedPreferencesUtil.init(getApplicationContext(), getPackageName() + "_preference", Context.MODE_MULTI_PROCESS);
    }

    protected void initNightMode() {
        boolean isNight = SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false);
        LogUtils.d("isNight=" + isNight);
        if (isNight) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
