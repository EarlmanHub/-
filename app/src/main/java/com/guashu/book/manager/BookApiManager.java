package com.guashu.book.manager;

import com.guashu.book.base.IBookApi;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.SearchDetail;
import com.guashu.book.bean.SourceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ws on 17-7-31.
 * <p>
 * 添加新的书源步骤：在BookSource中添加对应的枚举对象
 */
public class BookApiManager {
    private static BookApiManager manager;

    public static BookApiManager getInstance() {
        return manager == null ? (manager = new BookApiManager()) : manager;
    }


    private Map<BookSource, IBookApi> allBookApi;

    /**
     * 获取所有已经注册的API实例
     *
     * @return
     */
    public Map<BookSource, IBookApi> getAllBookApi() {
        if (allBookApi != null) {
            return allBookApi;
        }
        Map<BookSource, IBookApi> allBookApi = new HashMap<>();
        for (BookSource bookSource : BookSource.values()) {
            allBookApi.put(bookSource, getBookApi(bookSource.getApiClass()));
        }
        return allBookApi;
    }

    /**
     * 获取单个BookApi
     *
     * @param clazz
     * @return
     */
    public IBookApi getBookApi(Class<? extends IBookApi> clazz) {
        if (allBookApi != null) {
            for (IBookApi iBookApi : allBookApi.values()) {
                if (iBookApi.getClass().equals(clazz)) {
                    return iBookApi;
                }
            }
        }
        try {
            IBookApi iBookApi = clazz.newInstance();
            return iBookApi;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据书名匹配所有可用书源
     *
     * @param bookName
     * @return
     */
    public List<SourceInfo> matchSource(String bookName, BookSource source) {
        ArrayList<SourceInfo> sourceArrayList = new ArrayList<>();
        Map<BookSource, IBookApi> allBookApi = getAllBookApi();
        for (BookSource bookSource : allBookApi.keySet()) {
            if (source.equals(bookSource)) {
                continue;
            }
            IBookApi bookApi = allBookApi.get(bookSource);
            SearchDetail.SearchBooks searchBook = bookApi.searchBooKExact(bookName);
            if (searchBook != null) {
                SourceInfo sourceInfo = new SourceInfo(bookSource, searchBook);
                sourceArrayList.add(sourceInfo);
            }
        }
        return sourceArrayList;
    }
}
