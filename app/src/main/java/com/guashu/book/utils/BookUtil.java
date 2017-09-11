package com.guashu.book.utils;

import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.SearchDetail;

/**
 * Created by user on 17-8-10.
 */

public class BookUtil {
    /**
     * 根据搜索结果返回小说对象
     *
     * @return
     */
    public static Recommend.RecommendBook obtionBook(SearchDetail.SearchBooks data) {
        Recommend.RecommendBook recommendBook = new Recommend.RecommendBook();

        recommendBook.cover = data.cover;
        recommendBook.lastChapter = data.updateTime;
        recommendBook.updated = data.updateTime;
        recommendBook.title = data.title;
        recommendBook._id = data._id;
        recommendBook.url = data.url;
        recommendBook.bookSource = data.bookSource;
        return recommendBook;
    }

/**
 * 根据url返回小说对象
 *
 * @return
 */
//    public static Recommend.RecommendBook obtionBook(String url) {
//        Recommend.RecommendBook recommendBook = new Recommend.RecommendBook();
//        recommendBook.title = data.title;
//        recommendBook._id = data._id;
//        recommendBook.url = data.url;
//        recommendBook.cover = data.cover;
//        recommendBook.bookSource = data.bookSource;
//        LogUtils.e("TEST", "recommendBook = " + recommendBook);
//        return recommendBook;
//    }
}
