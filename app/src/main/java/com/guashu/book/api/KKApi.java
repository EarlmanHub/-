package com.guashu.book.api;//package com.guashu.book.api;
//
//import com.guashu.book.base.Constant;
//import com.guashu.book.base.IBookApi;
//import com.guashu.book.bean.BookDetail;
//import com.guashu.book.bean.BookMixAToc;
//import com.guashu.book.bean.BookSource;
//import com.guashu.book.bean.ChapterRead;
//import com.guashu.book.bean.Recommend;
//import com.guashu.book.bean.SearchDetail;
//import com.sinovoice.hcicloudsdk.common.utils.Md5Util;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import rx.Observable;
//import rx.Subscriber;
//
///**
// * 2K小说
// *
// * @author user
// */
//public class KKApi implements IBookApi {
//    private static final BookSource BOOK_SOURCE = BookSource.KK;
//
//    @Override
//    public List<Recommend.RecommendBook> getRecommend(String bookType) {
//        return null;
//    }
//
//    @Override
//    public List<SearchDetail.SearchBooks> searchBooKFuzzy(String name) {
//        List<SearchDetail.SearchBooks> list = new ArrayList<SearchDetail.SearchBooks>();
//        String url = "http://zhannei.baidu.com/cse/search?s=5383193680697477613&entry=1&q=";
//        try {
//            Document document = Jsoup.connect(url + name).get();
//            Elements select = document
//                    .select(".result-item-title.result-game-item-title a");
//            Elements select2 = document.select(".result-game-item-info span");
//            Elements select4 = document.select(".result-game-item-pic a img");
//            for (int i = 1; i < select.size(); i++) {
//                SearchDetail.SearchBooks books = new SearchDetail.SearchBooks();
//                books.url = select.get(i - 1).attr("href");
//                books.title = select.get(i - 1).attr("title");
//                books.author = select2.get(7 * (i - 1) + 1).text();
//                books.bookType = null;
//                books.updateTime = null;
//                String cover = select4.get(i - 1).attr("src");
//                if (cover.startsWith("http:") || cover.startsWith("HTTP:")) {
//                    books.cover = cover;
//                } else {
//                    books.cover = "http://www.2kxs.com" + cover;
//                }
//                books.bookSource = BOOK_SOURCE;
//                books._id = Md5Util.MD5(books.url);
//                list.add(books);
//            }
//            return list;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public SearchDetail.SearchBooks searchBooKExact(String name) {
//        String url = "http://zhannei.baidu.com/cse/search?s=5383193680697477613&entry=1&q=";
//        try {
//            Document document = Jsoup.connect(url + name).get();
//            Elements select = document
//                    .select(".result-item-title.result-game-item-title a");
//            Elements select2 = document.select(".result-game-item-info span");
//            Elements select4 = document.select(".result-game-item-pic a img");
//            for (int i = 1; i < select.size(); i++) {
//                SearchDetail.SearchBooks books = new SearchDetail.SearchBooks();
//                books.title = select.get(i - 1).attr("title");
//                if (!name.equals(books.title)) {
//                    continue;
//                }
//                books.url = select.get(i - 1).attr("href");
//                books.author = select2.get(7 * (i - 1) + 1).text();
//                books.bookType = null;
//                books.updateTime = Constant.UNKNOW;
//                String cover = select4.get(i - 1).attr("src");
//                if (cover.startsWith("http:") || cover.startsWith("HTTP:")) {
//                    books.cover = cover;
//                } else {
//                    books.cover = "http://www.2kxs.com" + cover;
//                }
//                books.bookSource = BOOK_SOURCE;
//                books._id = Md5Util.MD5(books.url);
//                return books;
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public List<BookMixAToc.mixToc.Chapters> getChapterList(String bookUrl) {
//        try {
//            List<BookMixAToc.mixToc.Chapters> list = new ArrayList<BookMixAToc.mixToc.Chapters>();
//            Document document = Jsoup.connect(bookUrl).get();
//            Elements select = document.select("dd a");
//
//            for (int i = 4; i < select.size(); i++) {
//                Element element = select.get(i);
//                BookMixAToc.mixToc.Chapters chapters = new BookMixAToc.mixToc.Chapters(i + 1);
//                chapters.link = bookUrl + element.attr("href");
//                chapters.title = element.text();
//                list.add(chapters);
//            }
//            return list;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public Observable<ChapterRead.Chapter> getChapter(final String chapterUrl) {
//        Observable<ChapterRead.Chapter> observable = Observable.create(new Observable.OnSubscribe<ChapterRead.Chapter>() {
//            @Override
//            public void call(Subscriber<? super ChapterRead.Chapter> subscriber) {
//                subscriber.onNext(getChapterRead(chapterUrl));
//                subscriber.onCompleted();
//            }
//        });
//        return observable;
//    }
//
//    @Override
//    public BookDetail getBookDetail(String bookUrl) {
//        String[] split = bookUrl.split("/");
//        String url = split[0] + "//" + split[2] + "/" + split[5] + "/";
//        try {
//            BookDetail bookDetail = new BookDetail();
//            Document document = Jsoup.connect(url).get();
//            Elements select = document.select("h2 a");
//            // 书名
//            bookDetail.title = select.get(0).text();
//            // 作者
//            bookDetail.author = select.get(1).text();
//            Elements select2 = document.select("li span");
//            // 类型
//            bookDetail.type = select2.get(0).text();
//            // 更新时间
//            bookDetail.updated = select2.get(3).text();
//            // 封面
//            bookDetail.cover = document.select(".bortable.wleft img").attr(
//                    "src");
//            // 简介
//            bookDetail.longIntro = document.select(".Text").text();
//            // 状态
//            bookDetail.status = document.select("dd span").get(9).text();
//            // 最后一章
//            bookDetail.lastChapter = document.select(".readlast a").text();
//            // 书籍链接
//            bookDetail.url = bookUrl;
//            bookDetail.bookSource = BOOK_SOURCE;
//            bookDetail._id = Md5Util.MD5(bookDetail.url);
//            return bookDetail;
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        return null;
//    }
//
//    @Override
//    public List<Recommend.RecommendBook> getRecommendBooks() {
//        return null;
//    }
//
//    @Override
//    public ChapterRead.Chapter getChapterRead(String chapterUrl) {
//        try {
//            Document document = Jsoup.connect(chapterUrl).get();
//            String text = document.select(".summary").text();
//            String[] split = text.split(" ");
//            String title = split[3] + "." + split[4];
//            String body = document.select(".Text").text();
//            return new ChapterRead.Chapter(title, body, body);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
