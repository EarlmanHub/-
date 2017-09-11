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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by ws on 17-7-24.
 * 笔趣阁小说API
 */
public class BiQuGeApi implements IBookApi {
    private static final BookSource BOOKSOURCE = BookSource.BIQUGE;

    /**
     * 根据小说类型获取小说列表
     *
     * @param bookType
     * @return
     */
    @Override
    public List<Recommend.RecommendBook> getRecommend(String bookType) {
        List<Recommend.RecommendBook> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect("http://www.biquzi.com/" + bookType + "/")
                    .get();
            Elements ls = doc.select("li").addClass("s5");
            for (int i = 10; i < ls.size(); i++) {
                Element element = ls.get(i);
                String text = element.getElementsByClass("s5").text();
                String[] split = text.split(" ");

                Recommend.RecommendBook recommendBook = new Recommend.RecommendBook();
                if (split.length == 3) {
                    recommendBook.author = split[2];
                    recommendBook.lastChapter = split[1];
                } else {
                    continue;
                }
                String bookName = element.getElementsByClass("s2").text();
                Element sElements = element.select("a").first();
                String bookUrl = sElements.attr("href");
                recommendBook.title = bookName;
                recommendBook.url = bookUrl;
                recommendBook._id = Md5Util.MD5(bookUrl);
                recommendBook.bookSource = BOOKSOURCE;
                list.add(recommendBook);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取小说章节列表
     *
     * @param bookUrl
     * @return
     */
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
                chapter.link = "http://www.biquge.com.tw"
                        + ((Node) str[i]).attr("href");
                list.add(chapter);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取章节内容
     *
     * @param chapterUrl
     * @return
     */
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

    /**
     * 获取章节内容
     *
     * @param chapterUrl
     * @return
     */
    @Override
    public ChapterRead.Chapter getChapterRead(String chapterUrl) {
        try {
            Document doc = Jsoup.connect(chapterUrl).get();
            Elements bookContent = doc.select("#content");
            Elements bookName = doc.select(".bookname h1");
            String body = bookContent.text();

            String[] split = body.split("     ");
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                String string = "     " + split[i] + "\n";
                sb.append(string);
            }
            return new ChapterRead.Chapter(bookName.text(), sb.toString());
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
    @Override
    public BookDetail getBookDetail(String bookUrl) {
        try {
            Document document = Jsoup.connect(bookUrl).get();
            Elements select = document.select("meta");
            if (select.size() < 20) {
                return null;
            }
            BookDetail bookDetail = new BookDetail();

            bookDetail.latelyFollower = 2338;//

            // 书名
            Element element = select.get(8);
            bookDetail.title = element.attr("content").toString();
            // 简介
            Element element2 = select.get(9);
            bookDetail.longIntro = element2.attr("content").toString();
            // 封面链接
            Element element3 = select.get(10);
            bookDetail.cover = element3.attr("content").toString();
            // 分类
            Element element4 = select.get(11);
            bookDetail.type = element4.attr("content").toString();
            bookDetail.cat = element4.attr("content").toString();
            // 作者
            Element element5 = select.get(12);
            bookDetail.author = element5.attr("content").toString();
            // 书籍链接
            Element element6 = select.get(14);
            bookDetail.url = element6.attr("content").toString();
            // 状态
            Element element7 = select.get(16);
            bookDetail.status = element7.attr("content").toString();
            // 更新时间
            Element element8 = select.get(17);
            bookDetail.updated = element8.attr("content").toString();
            // 最后一章
            Element element9 = select.get(18);
            bookDetail.lastChapter = element9.attr("content").toString();

            bookDetail._id = Md5Util.MD5(bookDetail.url);
            bookDetail.bookSource = BOOKSOURCE;
            return bookDetail;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据名称搜索书籍
     *
     * @param name
     * @return
     */
    @Override
    public List<SearchDetail.SearchBooks> searchBooKFuzzy(String name) {
        String url = "http://zhannei.baidu.com/cse/search?q=" + name
                + "&click=1&entry=1&s=10048850760735184192&nsid=";
        List<SearchDetail.SearchBooks> list = new ArrayList<>();
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
                book.url = ((Node) a1[i]).attr("href");
                book._id = Md5Util.MD5(book.url);
                book.bookSource = BOOKSOURCE;
                list.add(book);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public SearchDetail.SearchBooks searchBooKExact(String name) {
        String url = "http://zhannei.baidu.com/cse/search?q=" + name
                + "&click=1&entry=1&s=10048850760735184192&nsid=";
        List<SearchDetail.SearchBooks> list = new ArrayList<>();
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
                if (!name.equals(book.title)) {
                    continue;
                }

                book.author = jie.get(i * j);
                book.bookType = jie.get(i * j + 1);
                book.updateTime = jie.get(i * j + 2);
                book.cover = ((Node) a5[i]).attr("src");
                book.url = ((Node) a1[i]).attr("href");
                book._id = Md5Util.MD5(book.url);
                book.bookSource = BOOKSOURCE;
                return book;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取书架推荐书籍
     *
     * @return
     */
    @Override
    public List<Recommend.RecommendBook> getRecommendBooks() {
        List<Recommend.RecommendBook> list = new ArrayList<>();
        try {
            Document document = Jsoup.connect("http://www.biquzi.com/quanben/")
                    .get();
            Elements addClass = document.select(".image");

            Elements select = document.select("dt");

            Elements select2 = document.select("dd");

            for (int i = 0; i < addClass.size(); i++) {
                Recommend.RecommendBook book = new Recommend.RecommendBook();
                Element element = addClass.get(i);
                Elements select3 = element.select("a[href]");
                Elements attr = select3.select("img[src]");
                //封面url
                book.cover = attr.attr("src");
                //书名
                book.title = attr.attr("alt");

                Element element2 = select.get(i);
                //书url
                Elements select4 = element2.select("a[href]");
                book.url = select4.attr("href");
                //作者
                Elements select5 = element2.select("span");
                book.author = select5.text();
                //摘要
                book.lastChapter = select2.get(i).text();

                book._id = Md5Util.MD5(book.url);
                book.bookSource = BOOKSOURCE;
                list.add(book);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
