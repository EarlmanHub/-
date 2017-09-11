package com.guashu.book.api.support;

import com.guashu.book.bean.AutoComplete;
import com.guashu.book.bean.BooksByTag;
import com.guashu.book.bean.HotWord;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

public interface BookApiService {

    /**
     * 获取推荐小说的名称，用来搜索书籍
     *
     * @return
     */
    @GET("/book/hot-word")
    Observable<HotWord> getHotWord();

    /**
     * 关键字自动补全
     *
     * @param query
     * @return
     */
    @GET("/book/auto-complete")
    Observable<AutoComplete> autoComplete(@Query("query") String query);

    /**
     * 通过标签搜索小说
     *
     * @param tags
     * @param start
     * @param limit
     * @return
     */
    @GET("/book/by-tags")
    Observable<BooksByTag> getBooksByTag(@Query("tags") String tags, @Query("start") String start, @Query("limit") String limit);
}