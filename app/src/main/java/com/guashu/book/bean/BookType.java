package com.guashu.book.bean;

import java.io.Serializable;

/**
 * Created by user on 17-7-24.
 */

public enum BookType implements Serializable {
    XIUZHEN("修真", "xiuzhen"),
    XUANHUAN("玄幻", "xuanhuan"),
    DUSHI("都市", "dushi"),
    KEHUAN("科幻", "kehuan"),
    KONGBU("恐怖", "kongbu");


    private String name;
    private String value;

    BookType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
