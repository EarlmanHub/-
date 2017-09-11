package com.guashu.book.api;

import com.guashu.book.base.IBookApi;
import com.guashu.book.bean.BookDetail;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.ChapterRead;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.SearchDetail;
import com.guashu.book.utils.LogUtils;
import com.sinovoice.hcicloudsdk.common.utils.Md5Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * 大书包小说网
 *
 * @author user
 */
public class DaShuBaoApi implements IBookApi {
    private static final BookSource BOOK_SOURCE = BookSource.DASHUBAO;

    /**
     * 获取章节列表
     *
     * @param bookUrl
     * @return
     */
    @Override
    public List<BookMixAToc.mixToc.Chapters> getChapterList(String bookUrl) {
        List<BookMixAToc.mixToc.Chapters> list = new ArrayList<BookMixAToc.mixToc.Chapters>();
        String[] split = bookUrl.split("index");

        try {
            Document document = Jsoup.connect(bookUrl).get();
            Elements select = document.select("dd a");
            for (int i = 0; i < select.size(); i++) {
                BookMixAToc.mixToc.Chapters chapters = new BookMixAToc.mixToc.Chapters(i + 1);
                chapters.link = split[0] + select.get(i).attr("href");
                chapters.title = select.get(i).attr("title");
                chapters.id = Md5Util.MD5(chapters.link);
                list.add(chapters);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取书籍详情
     *
     * @param bookUrl
     * @return
     */
    public BookDetail getBookInfo(String bookUrl) {
        String[] split = bookUrl.split("/");
        String url = split[0] + "//" + split[2] + "/jieshaoinfo/" + split[4]
                + "/" + split[5] + ".htm";
        BookDetail bookDetail = new BookDetail();
        try {
            Document document = Jsoup.connect(url).get();
            // 状态
            String status = document.select(".state").text();
            bookDetail.status = status;
            // 书名
            String title = document.select("h1").text();
            bookDetail.title = title;
            // 作者
            String text = document.select(".author").text();
            String[] split2 = text.split("：");
            String author = split2[1];
            bookDetail.author = author;

            Elements select = document.select(".info_m span");
            // 类型
            String text2 = select.get(0).text();
            String[] split3 = text2.split("：");
            String type = split3[1];
            bookDetail.type = type;
            // 更新时间
            String text3 = select.get(1).text();
            String[] split4 = text3.split("：");
            String updateTime = split4[1];
            bookDetail.updated = updateTime;
            // 封面
            String cover = document.select(".fengmian a").attr("href");
            bookDetail.cover = cover;
            // 最新章节
            String lastChapter = document.select(".lastzj a").text();
            bookDetail.lastChapter = lastChapter;
            // 书籍链接
            bookDetail.url = bookUrl;
            // 简介
            String text4 = document.select("#BookIntro").text();
            String[] split5 = text4.split("......展开全部");
            String summary = split5[0];
            bookDetail.longIntro = summary;
            bookDetail._id = Md5Util.MD5(bookDetail.url);
            bookDetail.bookSource = BOOK_SOURCE;
            bookDetail._id = Md5Util.MD5(bookUrl);

            return bookDetail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }


    @Override
    public List<Recommend.RecommendBook> getRecommend(String bookType) {
        return null;
    }

    @Override
    public List<SearchDetail.SearchBooks> searchBooKFuzzy(String name) {
        List<SearchDetail.SearchBooks> list = new ArrayList<SearchDetail.SearchBooks>();
        try {
            Document document = Jsoup.connect(
                    "http://zn.DaShuBaoApi.net/cse/bookNamesearch?s=9410583021346449776&entry=1&q="
                            + name).get();
            Elements select = document
                    .select(".result-item-title.result-game-item-title a");
            Elements select2 = document.select(".result-game-item-info span");
            Elements select4 = document.select(".result-game-item-pic a img");
            for (int i = 1; i < select.size(); i++) {
                SearchDetail.SearchBooks books = new SearchDetail.SearchBooks();
                books.url = select.get(i - 1).attr("href");
                books.title = select.get(i - 1).attr("title");
                books.author = select2.get(7 * (i - 1) + 1).text();
                books.bookType = select2.get(7 * (i - 1) + 3).text();
                books.updateTime = select2.get(7 * (i - 1) + 5).text();
                books.cover = select4.get(i).attr("src");
                books.bookSource = BOOK_SOURCE;
                books._id = Md5Util.MD5(books.url);
                list.add(books);
            }
            return list;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SearchDetail.SearchBooks searchBooKExact(String bookName) {
        try {
            Document document = Jsoup.connect(
                    "http://zn.dashubao.net/cse/search?s=9410583021346449776&entry=1&q="
                            + bookName).get();
            Elements select = document
                    .select(".result-item-title.result-game-item-title a");
            Elements select2 = document.select(".result-game-item-info span");
            Elements select4 = document.select(".result-game-item-pic a img");
            for (int i = 1; i < select.size(); i++) {
                SearchDetail.SearchBooks books = new SearchDetail.SearchBooks();
                books.title = select.get(i - 1).attr("title");
                if (!bookName.equals(books.title)) {
                    continue;
                }
                books.url = select.get(i - 1).attr("href");
                books.author = select2.get(7 * (i - 1) + 1).text();
                books.bookType = select2.get(7 * (i - 1) + 3).text();
                books.updateTime = select2.get(7 * (i - 1) + 5).text();
                books.cover = select4.get(i).attr("src");
                books._id = Md5Util.MD5(books.url);
                books.bookSource = BOOK_SOURCE;
                return books;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public Observable<ChapterRead.Chapter> getChapter(final String chapterUrl) {
        Observable<ChapterRead.Chapter> observable = Observable.create(new Observable.OnSubscribe<ChapterRead.Chapter>() {
            @Override
            public void call(Subscriber<? super ChapterRead.Chapter> subscriber) {
                subscriber.onNext(getChapterRead(chapterUrl));
                subscriber.onCompleted();
            }
        });
        return observable;
    }

    @Override
    public BookDetail getBookDetail(String bookUrl) {
        String[] split = bookUrl.split("/");
        String url = split[0] + "//" + split[2] + "/jieshaoinfo/" + split[4]
                + "/" + split[5] + ".htm";
        BookDetail bookDetail = new BookDetail();
        try {
            Document document = Jsoup.connect(url).get();
            // 状态
            String status = document.select(".state").text();
            bookDetail.status = status;
            // 书名
            String title = document.select("h1").text();
            bookDetail.title = title;
            // 作者
            String text = document.select(".author").text();
            String[] split2 = text.split("：");
            String author = split2[1];
            bookDetail.author = author;

            Elements select = document.select(".info_m span");
            // 类型
            String text2 = select.get(0).text();
            String[] split3 = text2.split("：");
            String type = split3[1];
            bookDetail.type = type;
            // 更新时间
            String text3 = select.get(1).text();
            String[] split4 = text3.split("：");
            String updateTime = split4[1];
            bookDetail.updated = updateTime;
            // 封面
            String cover = document.select(".fengmian a").attr("href");
            bookDetail.cover = cover;
            // 最新章节
            String lastChapter = document.select(".lastzj a").text();
            bookDetail.lastChapter = lastChapter;
            // 书籍链接
            bookDetail.url = bookUrl;
            // 简介
            String text4 = document.select("#BookIntro").text();
            String[] split5 = text4.split("......展开全部");
            String summary = split5[0];
            bookDetail.longIntro = summary;
            bookDetail._id = Md5Util.MD5(bookDetail.url);
            bookDetail.bookSource = BOOK_SOURCE;
            bookDetail._id = Md5Util.MD5(bookUrl);

            return bookDetail;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Recommend.RecommendBook> getRecommendBooks() {
        return null;
    }

    @Override
    public ChapterRead.Chapter getChapterRead(String chapterUrl) {
        try {
            Document document = Jsoup.connect(chapterUrl).get();
            String body = document.select(".yd_text2").text();
            String title = document.select("h2").text();
            String[] split = body.split("     ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                String string = "     " + split[i] + "\n";
                sb.append(string);
            }
            return new ChapterRead.Chapter(title, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
