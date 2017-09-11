package com.guashu.book.bean;

import android.support.annotation.NonNull;

import com.guashu.book.base.Constant;

/**
 * Created by user on 17-8-10.
 */

public class SourceInfo implements Comparable<SourceInfo> {
    public BookSource bookSource;
    public SearchDetail.SearchBooks book;

    public SourceInfo(BookSource bookSource, SearchDetail.SearchBooks book) {
        this.bookSource = bookSource;
        this.book = book;
    }

    @Override
    public int compareTo(@NonNull SourceInfo another) {
        String anotherTime = another.book.updateTime;
        String thisTime = this.book.updateTime;
        if (Constant.UNKNOW.equals(anotherTime)) {
            return 1;
        }
        return -thisTime.compareTo(anotherTime);
    }
}
