package com.guashu.book.base;

import com.guashu.book.bean.BookDetail;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.ChapterRead;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.SearchDetail;
import com.guashu.book.manager.BookApiManager;

import java.util.List;

import rx.Observable;

/**
 * Created by ws on 17-7-28.
 * <p>
 * 所有书源都要实现此接口，所有方法都是共有方法
 */

public interface IBookApi {
//    BookSource BOOKSOURCE = ;

    /**
     * 根据分类获取书籍列表
     *
     * @param bookType
     * @return
     */
    List<Recommend.RecommendBook> getRecommend(String bookType);

    /**
     * 根据书名模糊搜索书籍
     *
     * @param name
     * @return
     */
    List<SearchDetail.SearchBooks> searchBooKFuzzy(String name);

    /**
     * 根据书名准确搜索书籍
     *
     * @param name
     * @return
     */
    SearchDetail.SearchBooks searchBooKExact(String name);

    /**
     * 获取章节列表
     *
     * @param bookUrl
     * @return
     */
    List<BookMixAToc.mixToc.Chapters> getChapterList(String bookUrl);

    /**
     * 下载章节内容调用
     *
     * @param chapterUrl
     * @return
     */
    Observable<ChapterRead.Chapter> getChapter(String chapterUrl);

    /**
     * 根据书籍链接获取详细信息
     *
     * @param bookUrl
     * @return
     */
    BookDetail getBookDetail(String bookUrl);

    /**
     * 获取首页推荐书籍列表
     *
     * @return
     */
    List<Recommend.RecommendBook> getRecommendBooks();

    /**
     * 获取章节内容
     *
     * @param chapterUrl
     * @return
     */
    ChapterRead.Chapter getChapterRead(String chapterUrl);
}
