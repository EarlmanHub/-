package com.guashu.book.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.ListPopupWindow;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.guashu.book.R;
import com.guashu.book.base.BaseActivity;
import com.guashu.book.base.Constant;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.ChapterRead;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.support.BookMark;
import com.guashu.book.bean.support.DownloadMessage;
import com.guashu.book.bean.support.DownloadProgress;
import com.guashu.book.bean.support.DownloadQueue;
import com.guashu.book.bean.support.ReadTheme;
import com.guashu.book.component.AppComponent;
import com.guashu.book.component.DaggerBookComponent;
import com.guashu.book.manager.CacheManager;
import com.guashu.book.manager.CollectionsManager;
import com.guashu.book.manager.EventManager;
import com.guashu.book.manager.SettingManager;
import com.guashu.book.manager.ThemeManager;
import com.guashu.book.manager.UpdataManager;
import com.guashu.book.service.DownloadBookService;
import com.guashu.book.ui.adapter.BookMarkAdapter;
import com.guashu.book.ui.adapter.TocListAdapter;
import com.guashu.book.ui.contract.BookReadContract;
import com.guashu.book.ui.easyadapter.ReadThemeAdapter;
import com.guashu.book.ui.presenter.BookReadPresenter;
import com.guashu.book.utils.FileUtils;
import com.guashu.book.utils.FormatUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.NetworkUtils;
import com.guashu.book.utils.ScreenUtils;
import com.guashu.book.utils.SharedPreferencesUtil;
import com.guashu.book.utils.ToastUtils;
import com.guashu.book.view.readview.BaseReadView;
import com.guashu.book.view.readview.OnReadStateChangeListener;
import com.guashu.book.view.readview.OverlappedWidget;
import com.guashu.book.view.readview.PageWidget;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by lfh on 2016/9/18.
 */
public class ReadActivity extends BaseActivity implements BookReadContract.View {

    @Bind(R.id.ivBack)
    ImageView mIvBack;
    @Bind(R.id.tvBookReadIntroduce)
    TextView mTvBookReadChangeSource;

    @Bind(R.id.flReadWidget)
    FrameLayout flReadWidget;

    @Bind(R.id.llBookReadTop)
    LinearLayout mLlBookReadTop;
    @Bind(R.id.tvBookReadTocTitle)
    TextView mTvBookReadTocTitle;
    @Bind(R.id.tvBookReadMode)
    TextView mTvBookReadMode;
    @Bind(R.id.tvSortToc)
    TextView mTvSortToc;
    @Bind(R.id.tvBookReadSettings)
    TextView mTvBookReadSettings;
    @Bind(R.id.tvBookReadDownload)
    TextView mTvBookReadDownload;
    @Bind(R.id.tvBookReadToc)
    TextView mTvBookReadToc;
    @Bind(R.id.llBookReadBottom)
    LinearLayout mLlBookReadBottom;
    @Bind(R.id.rlBookReadRoot)
    RelativeLayout mRlBookReadRoot;
    @Bind(R.id.tvDownloadProgress)
    TextView mTvDownloadProgress;

    @Bind(R.id.rlReadAaSet)
    LinearLayout rlReadAaSet;
    @Bind(R.id.ivBrightnessMinus)
    ImageView ivBrightnessMinus;
    @Bind(R.id.seekbarLightness)
    SeekBar seekbarLightness;
    @Bind(R.id.ivBrightnessPlus)
    ImageView ivBrightnessPlus;
    @Bind(R.id.tvFontsizeMinus)
    TextView tvFontsizeMinus;
    @Bind(R.id.seekbarFontSize)
    SeekBar seekbarFontSize;
    @Bind(R.id.tvFontsizePlus)
    TextView tvFontsizePlus;

    @Bind(R.id.rlReadMark)
    LinearLayout rlReadMark;
    @Bind(R.id.tvAddMark)
    TextView tvAddMark;
    @Bind(R.id.lvMark)
    ListView lvMark;

    @Bind(R.id.cbVolume)
    CheckBox cbVolume;
    @Bind(R.id.cbAutoBrightness)
    CheckBox cbAutoBrightness;
    @Bind(R.id.gvTheme)
    GridView gvTheme;

    private View decodeView;
    @Inject
    BookReadPresenter mPresenter;
    List<BookMixAToc.mixToc.Chapters> mChapterListBackup = new ArrayList<>();
    private List<BookMixAToc.mixToc.Chapters> mChapterList = new ArrayList<>();
    private ListPopupWindow mTocListPopupWindow;//章节展示控件
    private TocListAdapter mTocListAdapter;

    private List<BookMark> mMarkList;
    private BookMarkAdapter mMarkAdapter;

    private int currentChapter = 1;
    private int offsetPosition = -1;
    private int currPosition = 0;
    /**
     * 是否开始阅读章节
     **/
    private boolean startRead = false;


    private BaseReadView mPageWidget;
    private int curTheme = -1;
    private List<ReadTheme> themes;
    private ReadThemeAdapter gvAdapter;
    private Receiver receiver = new Receiver();
    private IntentFilter intentFilter = new IntentFilter();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    public static final String INTENT_BEAN = "recommendBooksBean";
    public static final String INTENT_SD = "isFromSD";

    private Recommend.RecommendBook recommendBook;
    private String bookId;

    private boolean isAutoLightness = false; // 记录其他页面是否自动调整亮度
    private boolean isFromSD = false;

    private String source;

    public static boolean isReverse = false;

    //添加收藏需要，所以跳转的时候传递整个实体类
    public static void startActivity(Context context, Recommend.RecommendBook recommendBook) {
        startActivity(context, recommendBook, false);
    }

    public static void startActivity(Context context, Recommend.RecommendBook recommendBook, boolean isFromSD) {
        context.startActivity(new Intent(context, ReadActivity.class)
                .putExtra(INTENT_BEAN, recommendBook)
                .putExtra(INTENT_SD, isFromSD));
    }

    @Override
    public int getLayoutId() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        statusBarColor = ContextCompat.getColor(this, R.color.reader_menu_bg_color);
        return R.layout.activity_read;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
    }

    @Override
    public void initDatas() {
        recommendBook = (Recommend.RecommendBook) getIntent().getSerializableExtra(INTENT_BEAN);
        bookId = recommendBook._id;
        isFromSD = getIntent().getBooleanExtra(INTENT_SD, false);
        if (!isFromSD) {
            source = recommendBook.bookSource.getName();
        }

        if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            String filePath = Uri.decode(getIntent().getDataString().replace("file://", ""));
            String fileName;
            if (filePath.lastIndexOf(".") > filePath.lastIndexOf("/")) {//hello/test.txt
                fileName = filePath.substring(filePath.lastIndexOf("/") + 1, filePath.lastIndexOf("."));
            } else {//hello.txt/test
                fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            }

            CollectionsManager.getInstance().remove(fileName);
            // 转存
            File desc = FileUtils.createWifiTranfesFile(fileName);
            FileUtils.fileChannelCopy(new File(filePath), desc);
            // 建立
            recommendBook = new Recommend.RecommendBook();
            recommendBook.isFromSD = true;
            recommendBook._id = fileName;
            recommendBook.title = fileName;

            isFromSD = true;
        }
        EventBus.getDefault().register(this);
        showDialog();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);

        CollectionsManager.getInstance().setRecentReadingTime(bookId);
        Observable.timer(1000, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        //延迟1秒刷新书架
                        EventManager.refreshCollectionList();
                    }
                });
    }

    @Override
    public void configViews() {
        mPresenter.attachView(this);
        hideStatusBar();
        decodeView = getWindow().getDecorView();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLlBookReadTop.getLayoutParams();
        params.topMargin = ScreenUtils.getStatusBarHeight(this) - 2;
        mLlBookReadTop.setLayoutParams(params);

        //目录列表
        initTocList();
        //初始化界面
        initAASet();


        initPagerWidget();

        // 本地收藏  直接打开
        if (isFromSD) {
            BookMixAToc.mixToc.Chapters chapters = new BookMixAToc.mixToc.Chapters();
            chapters.title = recommendBook.title;
            mChapterList.add(chapters);
            mChapterListBackup.add(chapters);
            showChapterRead(null, currentChapter);
            //本地书籍隐藏、简介、缓存按钮
            gone(mTvBookReadChangeSource, mTvBookReadDownload);
            return;
        }
        mPresenter.getBookMixAToc(recommendBook.bookSource, recommendBook.url, recommendBook._id);

    }


    private void initTocList() {
        //        LogUtils.e("TAG", "initTocList().currentChapter = " + currentChapter);
        mTocListAdapter = new TocListAdapter(this, mChapterListBackup, bookId, null);
        mTocListPopupWindow = new ListPopupWindow(this);
        mTocListPopupWindow.setAdapter(mTocListAdapter);

        mTocListPopupWindow.setForceIgnoreOutsideTouch(true);
        mTocListPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mTocListPopupWindow.setHeight(ScreenUtils.getScreenHeight()
                - ScreenUtils.getActionBarSize(getApplicationContext())
                - ScreenUtils.getStatusBarHeight(getApplicationContext()));
        mTocListPopupWindow.setAnchorView(mLlBookReadTop);

        mTocListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (isReverse == true) {
                    offsetPosition = mChapterList.size() - position;
                } else {
                    offsetPosition = position + 1;
                }
                currPosition = position;
                startRead = false;
                showDialog();

                readCurrentChapter();
            }
        });
        mTocListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                gone(mTvBookReadTocTitle, mTvSortToc);
                visible(mTvBookReadChangeSource);
            }
        });
    }

    private void initAASet() {
        curTheme = SettingManager.getInstance().getReadTheme();
        ThemeManager.setReaderTheme(curTheme, mRlBookReadRoot);

        seekbarFontSize.setMax(10);
        //int fontSizePx = SettingManager.getInstance().getReadFontSize(bookId);
        int fontSizePx = SettingManager.getInstance().getReadFontSize();
        int progress = (int) ((ScreenUtils.pxToDpInt(fontSizePx) - 12) / 1.7f);
        seekbarFontSize.setProgress(progress);
        seekbarFontSize.setOnSeekBarChangeListener(new SeekBarChangeListener());

        seekbarLightness.setMax(100);
        seekbarLightness.setOnSeekBarChangeListener(new SeekBarChangeListener());
        seekbarLightness.setProgress(SettingManager.getInstance().getReadBrightness());
        isAutoLightness = ScreenUtils.isAutoBrightness(this);
        if (SettingManager.getInstance().isAutoBrightness()) {
            startAutoLightness();
        } else {
            stopAutoLightness();
        }

        cbVolume.setChecked(SettingManager.getInstance().isVolumeFlipEnable());
        cbVolume.setOnCheckedChangeListener(new ChechBoxChangeListener());

        cbAutoBrightness.setChecked(SettingManager.getInstance().isAutoBrightness());
        cbAutoBrightness.setOnCheckedChangeListener(new ChechBoxChangeListener());

        gvAdapter = new ReadThemeAdapter(this, (themes = ThemeManager.getReaderThemeData(curTheme)), curTheme);
        gvTheme.setAdapter(gvAdapter);
        gvTheme.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < themes.size() - 1) {
                    changedMode(false, position);
                } else {
                    changedMode(true, position);
                }
            }
        });
    }

    private void initPagerWidget() {
        if (SharedPreferencesUtil.getInstance().getInt(Constant.FLIP_STYLE, 0) == 0) {//仿真翻页效果
            mPageWidget = new PageWidget(this, isFromSD, bookId, mChapterList, new ReadListener());
        } else {//滑动覆盖翻页效果
            mPageWidget = new OverlappedWidget(this, isFromSD, bookId, mChapterList, new ReadListener());
        }
        registerReceiver(receiver, intentFilter);
        if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {//是否设置为夜间模式
            mPageWidget.setTextColor(ContextCompat.getColor(this, R.color.chapter_content_night),
                    ContextCompat.getColor(this, R.color.chapter_title_night));
        }
        flReadWidget.removeAllViews();
        flReadWidget.addView(mPageWidget);
    }

    /**
     * 加载章节列表
     *
     * @param list
     */
    @Override
    public void showBookToc(List<BookMixAToc.mixToc.Chapters> list) {
        mChapterList.clear();
        mChapterListBackup.clear();
        mChapterList.addAll(list);
        mChapterListBackup.addAll(list);
        mTvBookReadTocTitle.setText(String.format(getString(R.string.book_name), recommendBook.title, list.size()));
        readCurrentChapter();
    }

    /**
     * 获取当前章节。章节文件存在则直接阅读，不存在则请求
     */
    public void readCurrentChapter() {
        if (offsetPosition < 0) {
            offsetPosition = currentChapter;
        }
        File chapterFile = CacheManager.getInstance().getChapterFile(bookId, offsetPosition);
        if (chapterFile != null) {
            showChapterRead(null, offsetPosition);
        } else {
            if (!NetworkUtils.isAvailable(getApplicationContext())) {
                hideDialog();
                ToastUtils.showSingleToast(getString(R.string.network_error_tips));
                return;
            }
            LogUtils.e(" mPresenter.getChapterRead  3");
            mPresenter.getChapterRead(recommendBook.bookSource, mChapterList.get(offsetPosition - 1).link, offsetPosition);
        }


    }

    @Override
    public synchronized void showChapterRead(ChapterRead.Chapter data, int chapter) { // 加载章节内容
        if (data != null) {
            CacheManager.getInstance().saveChapterFile(bookId, chapter, data);
        }

        if (!startRead) {
            startRead = true;
            currentChapter = chapter;
            if (!mPageWidget.isPrepared) {
                mPageWidget.init(curTheme);
            } else {
                mPageWidget.jumpToChapter(offsetPosition);
                mTocListPopupWindow.dismiss();
                mTocListAdapter.setCurrentChapter(mChapterListBackup.get(currPosition));
                currentChapter = offsetPosition;
                hideReadBar();
            }
            hideDialog();
        }
    }

    @Override
    public void netError(int chapter) {
        hideDialog();//防止因为网络问题而出现dialog不消失
        if (Math.abs(chapter - currentChapter) <= 1) {
            ToastUtils.showToast(R.string.net_error);
        }
        finish();
    }

    @Override
    public void showError() {
        hideDialog();
    }

    @Override
    public void complete() {
        hideDialog();
    }

    private synchronized void hideReadBar() {
        gone(mTvDownloadProgress, mLlBookReadBottom, mLlBookReadTop, rlReadAaSet, rlReadMark);
        hideStatusBar();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

    private synchronized void showReadBar() { // 显示工具栏
        visible(mLlBookReadBottom, mLlBookReadTop);
        showStatusBar();
        decodeView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private synchronized void toggleReadBar() { // 切换工具栏 隐藏/显示 状态
        if (isVisible(mLlBookReadTop)) {
            hideReadBar();
        } else {
            showReadBar();
        }
    }

    /***************Title Bar*****************/

    @OnClick(R.id.ivBack)
    public void onClickBack() {
        if (mTocListPopupWindow.isShowing()) {
            mTocListPopupWindow.dismiss();
            return;
        } else {
            finish();
        }
    }

    @OnClick(R.id.tvBookReadIntroduce)
    public void onClickIntroduce() {
        gone(rlReadAaSet, rlReadMark);
        BookSourceActivity.start(this, recommendBook._id, recommendBook.title, recommendBook.bookSource);
    }

    /***************Bottom Bar*****************/

    @OnClick(R.id.tvBookReadMode)
    public void onClickChangeMode() { // 日/夜间模式切换
        gone(rlReadAaSet, rlReadMark);

        boolean isNight = !SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false);
        changedMode(isNight, -1);
    }

    private void changedMode(boolean isNight, int position) {
        SharedPreferencesUtil.getInstance().putBoolean(Constant.ISNIGHT, isNight);
        AppCompatDelegate.setDefaultNightMode(isNight ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);

        if (position >= 0) {
            curTheme = position;
        } else {
            curTheme = SettingManager.getInstance().getReadTheme();
        }
        gvAdapter.select(curTheme);

        mPageWidget.setTheme(isNight ? ThemeManager.NIGHT : curTheme);
        mPageWidget.setTextColor(ContextCompat.getColor(mContext, isNight ? R.color.chapter_content_night : R.color.chapter_content_day),
                ContextCompat.getColor(mContext, isNight ? R.color.chapter_title_night : R.color.chapter_title_day));

        mTvBookReadMode.setText(getString(isNight ? R.string.book_read_mode_day_manual_setting
                : R.string.book_read_mode_night_manual_setting));
        Drawable drawable = ContextCompat.getDrawable(this, isNight ? R.drawable.ic_menu_mode_day_manual
                : R.drawable.ic_menu_mode_night_manual);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        mTvBookReadMode.setCompoundDrawables(null, drawable, null, null);

        ThemeManager.setReaderTheme(curTheme, mRlBookReadRoot);
    }

    @OnClick(R.id.tvBookReadSettings)
    public void setting() {
        if (isVisible(mLlBookReadBottom)) {
            if (isVisible(rlReadAaSet)) {
                gone(rlReadAaSet);
            } else {
                visible(rlReadAaSet);
                gone(rlReadMark);
            }
        }
    }

    @OnClick(R.id.tvBookReadDownload)
    public void downloadBook() {
        gone(rlReadAaSet);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("缓存多少章？")
                .setItems(new String[]{"后面十章", "后面五十章", "后面全部", "全部"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                DownloadBookService.post(new DownloadQueue(recommendBook.bookSource, bookId, mChapterList, currentChapter + 1, currentChapter + 10));
                                break;
                            case 1:
                                DownloadBookService.post(new DownloadQueue(recommendBook.bookSource, bookId, mChapterList, currentChapter + 1, currentChapter + 50));
                                break;
                            case 2:
                                DownloadBookService.post(new DownloadQueue(recommendBook.bookSource, bookId, mChapterList, currentChapter + 1, mChapterList.size()));
                                break;
                            case 3:
                                DownloadBookService.post(new DownloadQueue(recommendBook.bookSource, bookId, mChapterList, 1, mChapterList.size()));
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }

    @OnClick(R.id.tvBookMark)
    public void onClickMark() {
        if (isVisible(mLlBookReadBottom)) {
            if (isVisible(rlReadMark)) {
                gone(rlReadMark);
            } else {
                gone(rlReadAaSet);

                updateMark();

                visible(rlReadMark);
            }
        }
    }

    @OnClick(R.id.tvBookReadToc)
    public void onClickToc() {
        LogUtils.e("bookSource = " + source);
        gone(rlReadAaSet, rlReadMark);
        if (!mTocListPopupWindow.isShowing()) {
            visible(mTvBookReadTocTitle, mTvSortToc);
            //            visible(mTvBookReadTocTitle);
            gone(mTvBookReadChangeSource);
            mTocListPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            mTocListPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            mTocListPopupWindow.show();
//            mTocListPopupWindow.setSelection(currentChapter - 1);
            mTocListPopupWindow.setSelection(mTocListAdapter.getCurrentPosition());
            mTocListPopupWindow.getListView().setFastScrollEnabled(true);
        }
    }

    @OnClick(R.id.tvSortToc)
    public void onCheckedChanged() {
        CharSequence text = mTvSortToc.getText();
        String reverse = getString(R.string.book_read_reverse);//逆序
        String positiveSequence = getString(R.string.book_read_positive_sequence);//正序
        if (text.equals(reverse)) {//如果文字为倒序　则切换为逆序，并逆序列表
            isReverse = true;
            mTocListAdapter.reverseSort(isReverse);
            mTvSortToc.setText(positiveSequence);
        } else {
            isReverse = false;
            mTocListAdapter.reverseSort(isReverse);
            mTvSortToc.setText(reverse);
        }
    }


    /***************Setting Menu*****************/

    @OnClick(R.id.ivBrightnessMinus)
    public void brightnessMinus() {
        int curBrightness = SettingManager.getInstance().getReadBrightness();
        if (curBrightness > 2 && !SettingManager.getInstance().isAutoBrightness()) {
            seekbarLightness.setProgress((curBrightness = curBrightness - 2));
            ScreenUtils.setScreenBrightness(curBrightness, ReadActivity.this);
            SettingManager.getInstance().saveReadBrightness(curBrightness);
        }
    }

    @OnClick(R.id.ivBrightnessPlus)
    public void brightnessPlus() {
        int curBrightness = SettingManager.getInstance().getReadBrightness();
        if (curBrightness < 99 && !SettingManager.getInstance().isAutoBrightness()) {
            seekbarLightness.setProgress((curBrightness = curBrightness + 2));
            ScreenUtils.setScreenBrightness(curBrightness, ReadActivity.this);
            SettingManager.getInstance().saveReadBrightness(curBrightness);
        }
    }

    @OnClick(R.id.tvFontsizeMinus)
    public void fontsizeMinus() {
        calcFontSize(seekbarFontSize.getProgress() - 1);
    }

    @OnClick(R.id.tvFontsizePlus)
    public void fontsizePlus() {
        calcFontSize(seekbarFontSize.getProgress() + 1);
    }

    @OnClick(R.id.tvClear)
    public void clearBookMark() {
        SettingManager.getInstance().clearBookMarks(bookId);
        updateMark();
    }

    /***************Book Mark*****************/

    @OnClick(R.id.tvAddMark)
    public void addBookMark() {
        int[] readPos = mPageWidget.getReadPos();
        BookMark mark = new BookMark();
        mark.chapter = readPos[0];
        mark.startPos = readPos[1];
        mark.endPos = readPos[2];
        if (mark.chapter >= 1 && mark.chapter <= mChapterList.size()) {
            mark.title = mChapterList.get(mark.chapter - 1).title;
        }
        mark.desc = mPageWidget.getHeadLine();
        if (SettingManager.getInstance().addBookMark(bookId, mark)) {
            ToastUtils.showSingleToast("添加书签成功");
            updateMark();
        } else {
            ToastUtils.showSingleToast("书签已存在");
        }
    }

    private void updateMark() {
        if (mMarkAdapter == null) {
            mMarkAdapter = new BookMarkAdapter(this, new ArrayList<BookMark>());
            lvMark.setAdapter(mMarkAdapter);
            lvMark.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    BookMark mark = mMarkAdapter.getData(position);
                    if (mark != null) {
                        mPageWidget.setPosition(new int[]{mark.chapter, mark.startPos, mark.endPos});
                        hideReadBar();
                    } else {
                        ToastUtils.showSingleToast("书签无效");
                    }
                }
            });
            lvMark.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    new android.support.v7.app.AlertDialog.Builder(mContext)
                            .setCancelable(true)
                            .setMessage("是否删除此书签？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//同意
                                    SettingManager.getInstance().removeBookMarks(bookId, mMarkAdapter.getData(position));
                                    hideReadBar();
                                }
                            })
                            .setNegativeButton("否", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {//取消
                                    dialog.dismiss();
                                }
                            })
                            .create()
                            .show();
                    return true;
                }
            });

        }
        mMarkAdapter.clear();

        mMarkList = SettingManager.getInstance().getBookMarks(bookId);
        if (mMarkList != null && mMarkList.size() > 0) {
            Collections.reverse(mMarkList);
            mMarkAdapter.addAll(mMarkList);
        }
    }

    /***************Event*****************/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDownProgress(DownloadProgress progress) {
        if (bookId.equals(progress.bookId)) {
            if (isVisible(mLlBookReadBottom)) { // 如果工具栏显示，则进度条也显示
                visible(mTvDownloadProgress);
                // 如果之前缓存过，就给提示
                mTvDownloadProgress.setText(progress.message);
            } else {
                gone(mTvDownloadProgress);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadMessage(final DownloadMessage msg) {
        if (isVisible(mLlBookReadBottom)) { // 如果工具栏显示，则进度条也显示
            if (bookId.equals(msg.bookId)) {
                visible(mTvDownloadProgress);
                mTvDownloadProgress.setText(msg.message);
                if (msg.isComplete) {
                    mTvDownloadProgress.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gone(mTvDownloadProgress);
                        }
                    }, 2500);
                }
            }
        }
    }

    /**
     * 显示加入书架对话框
     *
     * @param bean
     */
    private void showJoinBookShelfDialog(final Recommend.RecommendBook bean) {
        new AlertDialog.Builder(mContext)
                .setTitle(getString(R.string.book_read_add_book))
                .setMessage(getString(R.string.book_read_would_you_like_to_add_this_to_the_book_shelf))
                .setPositiveButton(getString(R.string.book_read_join_the_book_shelf), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        bean.recentReadingTime = FormatUtils.getCurrentTimeString(FormatUtils.FORMAT_DATE_TIME);
                        CollectionsManager.getInstance().add(bean);
                        finish();
                    }
                })
                .setNegativeButton(getString(R.string.book_read_not), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .create()
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (mTocListPopupWindow != null && mTocListPopupWindow.isShowing()) {
                    mTocListPopupWindow.dismiss();
                    gone(mTvBookReadTocTitle);
                    visible(mTvBookReadChangeSource);
                    return true;
                } else if (isVisible(rlReadAaSet)) {
                    gone(rlReadAaSet);
                    return true;
                } else if (isVisible(mLlBookReadBottom)) {
                    hideReadBar();
                    return true;
                } else if (!CollectionsManager.getInstance().isCollected(bookId)) {
                    showJoinBookShelfDialog(recommendBook);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_MENU:
                toggleReadBar();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (SettingManager.getInstance().isVolumeFlipEnable()) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (SettingManager.getInstance().isVolumeFlipEnable()) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (SettingManager.getInstance().isVolumeFlipEnable()) {
                mPageWidget.nextPage();
                return true;// 防止翻页有声音
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (SettingManager.getInstance().isVolumeFlipEnable()) {
                mPageWidget.prePage();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            LogUtils.e("Receiver not registered");
        }

        if (isAutoLightness) {
            ScreenUtils.startAutoBrightness(ReadActivity.this);
        } else {
            ScreenUtils.stopAutoBrightness(ReadActivity.this);
        }

        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    private class ReadListener implements OnReadStateChangeListener {
        @Override
        public void onChapterChanged(int chapter) {
            currentChapter = chapter;
            mTocListAdapter.setCurrentChapter(mChapterListBackup.get(currentChapter - 1));
            // 加载前一节 与 后三节
            for (int i = chapter - 1; i <= chapter + 3 && i <= mChapterList.size(); i++) {
                if (i > 0 && i != chapter
                        && CacheManager.getInstance().getChapterFile(bookId, i) == null) {
                    mPresenter.getChapterRead(recommendBook.bookSource, mChapterList.get(i - 1).link, i);
                }
            }
        }

        @Override
        public void onLoadChapterFailure(int chapter) {
            startRead = false;
            if (CacheManager.getInstance().getChapterFile(bookId, chapter) == null) {
                ToastUtils.showSingleToast("章节内容加载失败，请重试...");
                finish();
            }
        }

        @Override
        public void onPageChanged(int chapter, int page) {

        }

        @Override
        public void onCenterClick() {
            toggleReadBar();
        }

        @Override
        public void onFlip() {
            hideReadBar();
        }
    }

    private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (seekBar.getId() == seekbarLightness.getId() && fromUser
                    && !SettingManager.getInstance().isAutoBrightness()) { // 非自动调节模式下 才可调整屏幕亮度
                ScreenUtils.setScreenBrightness(progress, ReadActivity.this);
                SettingManager.getInstance().saveReadBrightness(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (seekBar.getId() == seekbarFontSize.getId()) {
                calcFontSize(seekBar.getProgress());
            }
        }
    }

    private class ChechBoxChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (buttonView.getId() == cbVolume.getId()) {
                SettingManager.getInstance().saveVolumeFlipEnable(isChecked);
            } else if (buttonView.getId() == cbAutoBrightness.getId()) {
                if (isChecked) {
                    startAutoLightness();
                } else {
                    stopAutoLightness();
                }
            }
        }
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mPageWidget != null) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                    int level = intent.getIntExtra("level", 0);
                    mPageWidget.setBattery(100 - level);
                } else if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                    mPageWidget.setTime(sdf.format(new Date()));
                }
            }
        }
    }

    private void startAutoLightness() {
        SettingManager.getInstance().saveAutoBrightness(true);
        ScreenUtils.startAutoBrightness(ReadActivity.this);
        seekbarLightness.setEnabled(false);
    }

    private void stopAutoLightness() {
        SettingManager.getInstance().saveAutoBrightness(false);
        ScreenUtils.stopAutoBrightness(ReadActivity.this);
        int value = SettingManager.getInstance().getReadBrightness();
        seekbarLightness.setProgress(value);
        ScreenUtils.setScreenBrightness(value, ReadActivity.this);
        seekbarLightness.setEnabled(true);
    }

    private void calcFontSize(int progress) {
        // progress range 1 - 10
        if (progress >= 0 && progress <= 10) {
            seekbarFontSize.setProgress(progress);
            mPageWidget.setFontSize(ScreenUtils.dpToPxInt(12 + 1.7f * progress));
        }
    }
}
