package com.guashu.book.common;

/**
 * Created by user on 17-8-8.
 */

public interface OnCheckUpdataListener {
    void onStart();

    void onSucced(boolean isUpdata);

    void onFailed();

    void onFinish();
}
