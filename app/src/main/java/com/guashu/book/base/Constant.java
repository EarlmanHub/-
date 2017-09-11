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
package com.guashu.book.base;

import android.graphics.Color;

import com.guashu.book.R;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.FileUtils;

/**
 * @author yuyh.
 * @date 16/8/5.
 */
public class Constant {

    public static final String API_BASE_URL = "http://api.zhuishushenqi.com";

    public static final String APK_BASE_URL = "http://182.150.20.172:20080/apk";

    public static final String APK_URL = APK_BASE_URL + "/app-release.apk";

    public static final String APK_VER_URL = APK_BASE_URL + "/version.json";

    public static final String PATH_DATA = FileUtils.createRootPath(AppUtils.getAppContext()) + "/cache";

    public static final String PATH_COLLECT = FileUtils.createRootPath(AppUtils.getAppContext()) + "/collect";

    public static final String PATH_TXT = PATH_DATA + "/book/";

    public static final String PATH_EPUB = PATH_DATA + "/epub";

    public static final String PATH_CHM = PATH_DATA + "/chm";

    public static final String BASE_PATH = AppUtils.getAppContext().getCacheDir().getPath();

    public static final String ISNIGHT = "isNight";

    public static final String ISBYUPDATESORT = "isByUpdateSort";
    public static final String FLIP_STYLE = "flipStyle";

    public static final String SUFFIX_TXT = ".txt";
    public static final String SUFFIX_PDF = ".pdf";
    public static final String SUFFIX_EPUB = ".epub";
    public static final String SUFFIX_ZIP = ".zip";
    public static final String SUFFIX_CHM = ".chm";

    /**
     * Activity向Fragment传递数据键值
     */
    public static final String KEY_IS_NEVEL = "isNevel";

    /**
     * 是否首次进入app
     */
    public static final String KEY_IS_FIRST = "isFirst";

    /**
     * 时间未知
     */
    public static final String UNKNOW = "unknow";

    public static final int[] ICON_TYPE = new int[]{
            R.mipmap.xiuzhen,
            R.mipmap.xuanhuan,
            R.mipmap.dushi,
            R.mipmap.kehuan,
            R.mipmap.kongbu
    };

    public static final int[] TAG_COLORS = new int[]{
            Color.parseColor("#90C5F0"),
            Color.parseColor("#91CED5"),
            Color.parseColor("#F88F55"),
            Color.parseColor("#C0AFD0"),
            Color.parseColor("#E78F8F"),
            Color.parseColor("#67CCB7"),
            Color.parseColor("#F6BC7E")
    };
}
