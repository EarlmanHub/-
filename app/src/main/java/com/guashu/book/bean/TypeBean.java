package com.guashu.book.bean;

/**
 * Created by user on 17-7-25.
 */

public class TypeBean {
    private BookType type;
    private int iconResId;


    public TypeBean(BookType type, int iconResId) {
        this.type = type;
        this.iconResId = iconResId;
    }

    public BookType getType() {
        return type;
    }

    public void setType(BookType type) {
        this.type = type;
    }

    public int getIconResId() {
        return iconResId;
    }
}
