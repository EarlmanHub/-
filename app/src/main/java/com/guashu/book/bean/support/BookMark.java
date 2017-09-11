package com.guashu.book.bean.support;

import java.io.Serializable;

/**
 * @author yuyh.
 * @date 2016/11/18.
 */
public class BookMark implements Serializable {

    public int chapter;

    public String title;

    public int startPos;

    public int endPos;

    public String desc = "";

    @Override
    public String toString() {
        return "BookMark{" +
                "chapter=" + chapter +
                ", title='" + title + '\'' +
                ", startPos=" + startPos +
                ", endPos=" + endPos +
                ", desc='" + desc + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        return toString().equals(o.toString());
    }
}
