package com.guashu.book.base;

import com.guashu.book.bean.BookSource;

import java.io.Serializable;

/**
 * Created by user on 17-7-28.
 */

public interface IBase extends Serializable {
    BookSource getBookSource();
}
