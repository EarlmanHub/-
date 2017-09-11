package com.guashu.book.api;

import com.guashu.book.base.IBookApi;
import com.guashu.book.bean.BookDetail;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.ChapterRead;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.SearchDetail;
import com.sinovoice.hcicloudsdk.common.utils.Md5Util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by ws on 17-7-31.
 * 顶点小说Api
 */

public class DingDianApi implements IBookApi {
    private static final BookSource BOOKSOURCE = BookSource.DINGDIAN;

    @Override
    public List<Recommend.RecommendBook> getRecommend(String bookType) {
        List<Recommend.RecommendBook> list = new ArrayList<>();
        try {
            // http://www.biquzi.com/xiuzhen/
            Document doc = Jsoup.connect(
                    "http://www.xs222.tw/" + bookType + "/").get();
            Elements ls = doc.select("li").addClass("s5");
            for (int i = 10; i < ls.size(); i++) {
                Recommend.RecommendBook book = new Recommend.RecommendBook();
                Element element = ls.get(i);
                String text = element.getElementsByClass("s5").text();
                String[] split = text.split(" ");
                if (split.length == 3) {
                    book.author = split[2];
                    book.lastChapter = split[1];
                } else {
                    continue;
                }
                String bookName = element.getElementsByClass("s2").text();
                Element sElements = element.select("a").first();
                String bookUrl = "http://www.xs222.tw/" + sElements.attr("href");
                book.title = bookName;
                book.url = bookUrl;
                book._id = Md5Util.MD5(bookUrl);
                book.bookSource = BOOKSOURCE;
                list.add(book);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<SearchDetail.SearchBooks> searchBooKFuzzy(String name) {
        String url = "http://zhannei.baidu.com/cse/search?s=11420960148673455085&q=" + name;
        List<SearchDetail.SearchBooks> list = new ArrayList<SearchDetail.SearchBooks>();
        List<String> jie = new ArrayList<String>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements book1 = doc
                    .select(".result-item-title.result-game-item-title a"); // 书籍地址
            Elements book2 = doc.select(".result-game-item-desc"); // 简介
            Elements book3 = doc.select(".result-game-item-info span"); // 作者 类型
            Elements book4 = doc.select(".result-game-item-info a"); // 更新
            Elements book5 = doc.select(".result-game-item-pic a  img"); // 简介
            Object[] a1 = book1.toArray();
            Object[] a2 = book2.toArray();
            Object[] a3 = book3.toArray();
            Object[] a4 = book4.toArray();
            Object[] a5 = book5.toArray();
            for (int i = 0; i < a3.length; i++) {
                String ac = ((Element) a3[i]).text();
                if (ac.equals("作者：") || ac.equals("更新时间：")
                        || ac.equals("最新章节：") || ac.equals("类型：")) {
                } else {
                    jie.add(ac);
                }
            }
            for (int i = 0; i < a1.length; i++) {
                int j = 3;
                SearchDetail.SearchBooks book = new SearchDetail.SearchBooks();
                book.title = ((Node) a1[i]).attr("title");
                book.author = jie.get(i * j);
                book.bookType = jie.get(i * j + 1);
                book.updateTime = jie.get(i * j + 2);
                book.cover = ((Node) a5[i]).attr("src");
                String bookUrl = ((Node) a1[i]).attr("href");
                book.url = bookUrl;
                book._id = Md5Util.MD5(bookUrl);
                book.bookSource = BOOKSOURCE;
                list.add(book);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SearchDetail.SearchBooks searchBooKExact(String name) {
        String url = "http://zhannei.baidu.com/cse/search?s=11420960148673455085&q=" + name;
        List<String> jie = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();
            Elements book1 = doc
                    .select(".result-item-title.result-game-item-title a"); // 书籍地址
            Elements book2 = doc.select(".result-game-item-desc"); // 简介
            Elements book3 = doc.select(".result-game-item-info span"); // 作者 类型
            Elements book4 = doc.select(".result-game-item-info a"); // 更新
            Elements book5 = doc.select(".result-game-item-pic a  img"); // 简介
            Object[] a1 = book1.toArray();
            Object[] a2 = book2.toArray();
            Object[] a3 = book3.toArray();
            Object[] a4 = book4.toArray();
            Object[] a5 = book5.toArray();
            for (int i = 0; i < a3.length; i++) {
                String ac = ((Element) a3[i]).text();
                if (ac.equals("作者：") || ac.equals("更新时间：")
                        || ac.equals("最新章节：") || ac.equals("类型：")) {
                } else {
                    jie.add(ac);
                }
            }
            for (int i = 0; i < a1.length; i++) {
                int j = 3;
                SearchDetail.SearchBooks book = new SearchDetail.SearchBooks();
                book.title = ((Node) a1[i]).attr("title");
                if (!name.equals(book.title)) {
                    continue;
                }

                book.author = jie.get(i * j);
                book.bookType = jie.get(i * j + 1);
                book.updateTime = jie.get(i * j + 2);
                book.cover = ((Node) a5[i]).attr("src");
                String bookUrl = ((Node) a1[i]).attr("href");
                book.url = bookUrl;
                book._id = Md5Util.MD5(bookUrl);
                book.bookSource = BOOKSOURCE;
                return book;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BookMixAToc.mixToc.Chapters> getChapterList(String bookUrl) {
        List<BookMixAToc.mixToc.Chapters> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(bookUrl).get();
            Elements link = doc.select("dd a");
            Object[] str = link.toArray();
            for (int i = 0; i < str.length; i++) {
                BookMixAToc.mixToc.Chapters chapter = new BookMixAToc.mixToc.Chapters(i + 1);
                chapter.title = ((Element) str[i]).text();
                String chapterUrl = "http://www.xs222.tw"
                        + ((Node) str[i]).attr("href");
                chapter.link = chapterUrl;
                chapter.id = Md5Util.MD5(chapterUrl);
                list.add(chapter);
                System.err.println(chapter.toString());
            }
            return list;
        } catch (Exception e) {
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
        BookDetail book = new BookDetail();
        try {
            Document document = Jsoup.connect(bookUrl).get();
            Elements select = document.select("meta");
            if (select.size() < 20) {
                return null;
            }
            // 书名
            Element element = select.get(8);
            book.title = element.attr("content").toString();
            // 简介
            Element element2 = select.get(9);
            book.longIntro = element2.attr("content").toString();
            // 封面链接
            Element element3 = select.get(10);
            book.cover = element3.attr("content").toString();
            // 分类
            Element element4 = select.get(11);
            book.type = element4.attr("content").toString();
            book.cat = element4.attr("content").toString();
            // 作者
            Element element5 = select.get(12);
            book.author = element5.attr("content").toString();
            // 书籍链接
            Element element6 = select.get(14);
            book.url = element6.attr("content").toString();
            // 状态
            Element element7 = select.get(16);
            book.status = element7.attr("content").toString();
            // 更新时间
            Element element8 = select.get(17);
            book.updated = element8.attr("content").toString();
            // 最后一章
            Element element9 = select.get(18);
            book.lastChapter = element9.attr("content").toString();

            book._id = Md5Util.MD5(book.url);

            book.bookSource = BOOKSOURCE;
            return book;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public List<Recommend.RecommendBook> getRecommendBooks() {
        List<Recommend.RecommendBook> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect("http://www.xs222.tw/xuanhuan/").get();
            Elements addClass = document.select(".image");

            Elements select = document.select("dt");

            Elements select2 = document.select("dd");

            for (int i = 0; i < addClass.size(); i++) {
                Recommend.RecommendBook book = new Recommend.RecommendBook();
                Element element = addClass.get(i);
                Elements select3 = element.select("a[href]");
                Elements attr = select3.select("img[src]");
                // 封面url
                book.cover = "http://www.xs222.tw" + attr.attr("src");
                // 书名
                book.title = attr.attr("alt");
                //
                Element element2 = select.get(i);
                // 书url
                Elements select4 = element2.select("a[href]");
                book.url = "http://www.xs222.tw" + select4.attr("href");
                // 作者
                Elements select5 = element2.select("span");
                book.author = select5.text();
                // 摘要
                book.lastChapter = select2.get(i).text();

                book._id = Md5Util.MD5(book.url);

                book.bookSource = BOOKSOURCE;

                list.add(book);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ChapterRead.Chapter getChapterRead(String chapterUrl) {
        try {
            Document doc = Jsoup.connect(chapterUrl).get();
            String body = doc.select("#content").text();
            String title = doc.select(".bookname h1").text();
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