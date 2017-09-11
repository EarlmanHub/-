package com.guashu.book.view.readview;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * Created by user on 17-8-18.
 */

public interface IPageFactory {
    void setOnReadStateChangeListener(OnReadStateChangeListener listener);

    void setBgBitmap(Bitmap themeDrawable);

    int openBook(int po, int[] ints, int po1);

    void onDraw(Canvas mCurrentPageCanvas);

    BookStatus prePage();

    BookStatus nextPage();

    void cancelPage();

    void setTextFont(int fontSizePx);

    void setTextColor(int textColor, int titleColor);

    void setBattery(int battery);

    void setTime(String time);

    int[] getPosition();

    String getHeadLineStr();

    void recycle();

    void convertBetteryBitmap();

    void resetCurrentPage(boolean toAdd);
}
