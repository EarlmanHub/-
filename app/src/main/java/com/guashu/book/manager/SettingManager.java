package com.guashu.book.manager;

import com.guashu.book.base.Constant;
import com.guashu.book.bean.support.BookMark;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.ScreenUtils;
import com.guashu.book.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

public class SettingManager {

    private volatile static SettingManager manager;

    public static SettingManager getInstance() {
        return manager != null ? manager : (manager = new SettingManager());
    }

    /**
     * 是否第一次进入app
     *
     * @return
     */
    public boolean isFirst() {
        return SharedPreferencesUtil.getInstance().getBoolean(Constant.KEY_IS_FIRST, true);
    }

    /**
     * 保存书籍阅读字体大小
     *
     * @param bookId     需根据bookId对应，避免由于字体大小引起的分页不准确
     * @param fontSizePx
     * @return
     */
    public void saveFontSize(String bookId, int fontSizePx) {
        // 书籍对应
        SharedPreferencesUtil.getInstance().putInt(getFontSizeKey(bookId), fontSizePx);
    }

    /**
     * 保存全局生效的阅读字体大小
     *
     * @param fontSizePx
     */
    public void saveFontSize(int fontSizePx) {
        saveFontSize("", fontSizePx);
    }

    public int getReadFontSize(String bookId) {
        return SharedPreferencesUtil.getInstance().getInt(getFontSizeKey(bookId), ScreenUtils.dpToPxInt(20));
    }

    public int getReadFontSize() {
        return getReadFontSize("");
    }

    private String getFontSizeKey(String bookId) {
        return bookId + "-readFontSize";
    }

    public int getReadBrightness() {
        return SharedPreferencesUtil.getInstance().getInt(getLightnessKey(),
                (int) ScreenUtils.getScreenBrightness(AppUtils.getAppContext()));
    }

    /**
     * 保存阅读界面屏幕亮度
     *
     * @param percent 亮度比例 0~100
     */
    public void saveReadBrightness(int percent) {
        SharedPreferencesUtil.getInstance().putInt(getLightnessKey(), percent);
    }

    private String getLightnessKey() {
        return "readLightness";
    }

    public synchronized void saveReadProgress(String bookId, int[] pro) {
        saveReadProgress(bookId, pro[0], pro[1], pro[2], pro[3]);
    }

    public synchronized void saveReadProgress(String bookId, int currentChapter, int m_mbBufBeginPos, int m_mbBufEndPos, int m_mbBufPage) {
        SharedPreferencesUtil.getInstance()
                .putInt(getChapterKey(bookId), currentChapter)
                .putInt(getStartPosKey(bookId), m_mbBufBeginPos)
                .putInt(getEndPosKey(bookId), m_mbBufEndPos)
                .putInt(getPagePosKey(bookId), m_mbBufPage);
    }

    /**
     * 获取上次阅读章节及位置
     *
     * @param bookId
     * @return
     */
    public int[] getReadProgress(String bookId) {
        int lastChapter = SharedPreferencesUtil.getInstance().getInt(getChapterKey(bookId), 1);
        int startPos = SharedPreferencesUtil.getInstance().getInt(getStartPosKey(bookId), 0);
        int endPos = SharedPreferencesUtil.getInstance().getInt(getEndPosKey(bookId), 0);
        int page = SharedPreferencesUtil.getInstance().getInt(getPagePosKey(bookId), 1);
        return new int[]{lastChapter, startPos, endPos, page};
    }

    public void removeReadProgress(String bookId) {
        SharedPreferencesUtil.getInstance()
                .remove(getChapterKey(bookId))
                .remove(getStartPosKey(bookId))
                .remove(getPagePosKey(bookId))
                .remove(getEndPosKey(bookId));
    }

    private String getChapterKey(String bookId) {
        return bookId + "-chapter";
    }

    private String getStartPosKey(String bookId) {
        return bookId + "-startPos";
    }

    private String getEndPosKey(String bookId) {
        return bookId + "-endPos";
    }

    private String getPagePosKey(String bookId) {
        return bookId + "-pagePos";
    }


    public boolean addBookMark(String bookId, BookMark mark) {
        List<BookMark> marks = SharedPreferencesUtil.getInstance().getObject(getBookMarksKey(bookId), ArrayList.class);
        if (marks != null && marks.size() > 0) {
            for (BookMark item : marks) {
                if (item.chapter == mark.chapter && item.startPos == mark.startPos) {
                    return false;
                }
            }
        } else {
            marks = new ArrayList<>();
        }
        marks.add(mark);
        SharedPreferencesUtil.getInstance().putObject(getBookMarksKey(bookId), marks);
        return true;
    }

    public List<BookMark> getBookMarks(String bookId) {
        return SharedPreferencesUtil.getInstance().getObject(getBookMarksKey(bookId), ArrayList.class);
    }

    public void removeBookMarks(String bookId, BookMark mark) {
        List<BookMark> bookMarks = getBookMarks(bookId);
        clearBookMarks(bookId);
        for (BookMark bookMark : bookMarks) {
            if (bookMark.equals(mark)) {
                continue;
            }
            addBookMark(bookId, bookMark);
        }
    }

    public void clearBookMarks(String bookId) {
        SharedPreferencesUtil.getInstance().remove(getBookMarksKey(bookId));
    }

    private String getBookMarksKey(String bookId) {
        return bookId + "-marks";
    }

    public void saveReadTheme(int theme) {
        SharedPreferencesUtil.getInstance().putInt("readTheme", theme);
    }

    public int getReadTheme() {
        if (SharedPreferencesUtil.getInstance().getBoolean(Constant.ISNIGHT, false)) {
            return ThemeManager.NIGHT;
        }
        return SharedPreferencesUtil.getInstance().getInt("readTheme", 3);
    }

    /**
     * 是否可以使用音量键翻页
     *
     * @param enable
     */
    public void saveVolumeFlipEnable(boolean enable) {
        SharedPreferencesUtil.getInstance().putBoolean("volumeFlip", enable);
    }

    public boolean isVolumeFlipEnable() {
        return SharedPreferencesUtil.getInstance().getBoolean("volumeFlip", true);
    }

    public void saveAutoBrightness(boolean enable) {
        SharedPreferencesUtil.getInstance().putBoolean("autoBrightness", enable);
    }

    public boolean isAutoBrightness() {
        return SharedPreferencesUtil.getInstance().getBoolean("autoBrightness", true);
    }


    public boolean isNoneCover() {
        return SharedPreferencesUtil.getInstance().getBoolean("isNoneCover", false);
    }

    public void saveNoneCover(boolean isNoneCover) {
        SharedPreferencesUtil.getInstance().putBoolean("isNoneCover", isNoneCover);
    }

    public void saveUserAgreeDis() {
        SharedPreferencesUtil.getInstance().putBoolean("isAgreeDis", true);
    }

    public boolean isUserAgreeDis() {
        return SharedPreferencesUtil.getInstance().getBoolean("isAgreeDis", false);
    }

    /**
     * 首页是否推荐过
     *
     * @return
     */
    public boolean hasRecommend() {
        return SharedPreferencesUtil.getInstance().getBoolean("hasRecommend", false);
    }

    /**
     * 保存推荐成功状态
     */
    public void saveRecommend() {
        SharedPreferencesUtil.getInstance().putBoolean("hasRecommend", true);
    }
}