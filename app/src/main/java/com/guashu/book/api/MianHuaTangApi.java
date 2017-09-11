package com.guashu.book.api;

import com.guashu.book.base.IBookApi;
import com.guashu.book.bean.BookDetail;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.ChapterRead;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.SearchDetail;
import com.sinovoice.hcicloudsdk.common.utils.Md5Util;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import rx.Observable;
import rx.Subscriber;

/**
 * 棉花糖小说
 *
 * @author user
 */
public class MianHuaTangApi implements IBookApi {
    private static final BookSource BOOK_SOURCE = BookSource.MIANHUATANG;

    @Override
    public List<Recommend.RecommendBook> getRecommend(String bookType) {
        return null;
    }

    @Override
    public List<SearchDetail.SearchBooks> searchBooKFuzzy(String name) {
        List<SearchDetail.SearchBooks> list = new ArrayList<SearchDetail.SearchBooks>();
        String url = "http://zhannei.baidu.com/cse/search?s=7965856832468911224&entry=1&q=";
        try {
            Document document = Jsoup.connect(url + name).get();
            Elements select = document
                    .select(".result-item-title.result-game-item-title a");
            Elements select2 = document.select(".result-game-item-info span");
            Elements select4 = document.select(".result-game-item-pic a img");
            for (int i = 1; i < select.size(); i++) {
                SearchDetail.SearchBooks books = new SearchDetail.SearchBooks();
                books.url = select.get(i - 1).attr("href");
                books.title = select.get(i - 1).attr("title");
                books.author = select2.get(8 * (i - 1) + 1).text();
                books.bookType = select2.get(8 * (i - 1) + 3).text();
                books.updateTime = select2.get(8 * (i - 1) + 5).text();
                books.cover = select4.get(i - 1).attr("src");
                books.bookSource = BOOK_SOURCE;
                books._id = Md5Util.MD5(books.url);
                list.add(books);
                System.err.println(books.toString());
            }
            return list;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SearchDetail.SearchBooks searchBooKExact(String name) {
        String url = "http://zhannei.baidu.com/cse/search?s=7965856832468911224&entry=1&q=";
        try {
            Document document = Jsoup.connect(url + name).get();
            Elements select = document
                    .select(".result-item-title.result-game-item-title a");
            Elements select2 = document.select(".result-game-item-info span");
            Elements select4 = document.select(".result-game-item-pic a img");
            for (int i = 1; i < select.size(); i++) {
                SearchDetail.SearchBooks books = new SearchDetail.SearchBooks();
                books.url = select.get(i - 1).attr("href");
                books.title = select.get(i - 1).attr("title");
                books.author = select2.get(8 * (i - 1) + 1).text();
                books.bookType = select2.get(8 * (i - 1) + 3).text();
                books.updateTime = select2.get(8 * (i - 1) + 5).text();
                books.cover = select4.get(i - 1).attr("src");
                books.bookSource = BOOK_SOURCE;
                books._id = Md5Util.MD5(books.url);
                return books;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BookMixAToc.mixToc.Chapters> getChapterList(String bookUrl) {
        List<BookMixAToc.mixToc.Chapters> list = new ArrayList<BookMixAToc.mixToc.Chapters>();
        try {
            Document document = Jsoup.connect(bookUrl).get();
            Elements select = document.select("dd a");
            for (int i = 9; i < select.size(); i++) {
                BookMixAToc.mixToc.Chapters chapter = new BookMixAToc.mixToc.Chapters(i - 8);
                chapter.link = bookUrl + select.get(i).attr("href");
                chapter.title = select.get(i).text();
                chapter.id = Md5Util.MD5(chapter.link);
                list.add(chapter);
            }
            return list;
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
        try {
            Document document = Jsoup.connect(bookUrl).get();

            Elements select = document.select("meta");
            if (select.size() < 20) {
                return null;
            }
            BookDetail book = new BookDetail();
            // 书名
            Element element = select.get(15);
            book.title = (element.attr("content").toString());
            // 简介
            Element element2 = select.get(6);
            book.longIntro = (element2.attr("content").toString());
            // 封面链接
            Element element3 = select.get(11);
            book.cover = (element3.attr("content").toString());
            // 分类
            Element element4 = select.get(12);
            book.type = (element4.attr("content").toString());
            // 作者
            Element element5 = select.get(14);
            book.author = (element5.attr("content").toString());
            // 书籍链接
            Element element6 = select.get(16);
            book.url = (element6.attr("content").toString());
            // 状态
            Element element7 = select.get(13);
            book.status = (element7.attr("content").toString());
            // 更新时间
            Element element8 = select.get(18);
            book.updated = (element8.attr("content").toString());
            // 最后一章
            Element element9 = select.get(19);
            book.lastChapter = (element9.attr("content").toString());

            book.bookSource = BOOK_SOURCE;
            book._id = Md5Util.MD5(book.url);
            return book;
        } catch (Exception e) {
            // TODO Auto-generated catch block
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
            Document document = Jsoup.connect(
                    chapterUrl).get();
            String body = document.select(".content").text();
            Elements select = document.select("meta");
            String text = select.get(3).attr("content");
            String[] split1 = text.split("，");
            String title = split1[0];
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
