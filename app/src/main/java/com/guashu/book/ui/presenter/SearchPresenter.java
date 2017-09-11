/**
 * Copyright 2016 JustWayward Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guashu.book.ui.presenter;

import android.os.AsyncTask;

import com.guashu.book.api.support.BookApi;
import com.guashu.book.base.IBookApi;
import com.guashu.book.base.RxPresenter;
import com.guashu.book.bean.AutoComplete;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.HotWord;
import com.guashu.book.bean.SearchDetail;
import com.guashu.book.ui.contract.SearchContract;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.RxUtil;
import com.guashu.book.utils.StringUtils;
import com.guashu.book.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author lfh.
 * @date 2016/8/6.
 */
public class SearchPresenter extends RxPresenter<SearchContract.View> implements SearchContract.Presenter<SearchContract.View> {

    private BookApi bookApi;

    @Inject
    public SearchPresenter(BookApi bookApi) {
        this.bookApi = bookApi;
    }

    public void getHotWordList() {
        String key = StringUtils.creatAcacheKey("hot-word-list");
        Observable<HotWord> fromNetWork = bookApi.getHotWord()
                .compose(RxUtil.<HotWord>rxCacheListHelper(key));

        //依次检查disk、network
        Subscription rxSubscription = Observable.concat(RxUtil.rxCreateDiskObservable(key, HotWord.class), fromNetWork)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HotWord>() {
                    @Override
                    public void onNext(HotWord hotWord) {
                        List<String> list = hotWord.hotWords;
                        if (list != null && !list.isEmpty() && mView != null) {
                            mView.showHotWordList(list);
                        }
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.e("onError: " + e);
                    }
                });
        addSubscrebe(rxSubscription);
    }

    @Override
    public void getSearchResultList(final String name) {
        final char[] chars = name.toCharArray();
        new AsyncTask<Void, Void, List<SearchDetail.SearchBooks>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mView.showLoading();
            }

            @Override
            protected List<SearchDetail.SearchBooks> doInBackground(Void... params) {
                List<SearchDetail.SearchBooks> searchBookses = new ArrayList<>();
                Map<BookSource, IBookApi> allBookApi = bookApiManager.getAllBookApi();
                for (IBookApi iBookApi : allBookApi.values()) {
                    List<SearchDetail.SearchBooks> books = iBookApi.searchBooKFuzzy(name);
                    if (books != null) {
                        for (SearchDetail.SearchBooks book : books) {
                            if (checkSearchResult(book)) {
                                searchBookses.add(book);
                            }
                        }
                    }
                }
                return searchBookses;
            }

            /**
             * 检测书籍是否符合搜索结果
             * @param book
             * @return
             */
            private boolean checkSearchResult(SearchDetail.SearchBooks book) {
                for (char aChar : chars) {
                    if (book.title.contains(aChar + "") || book.author.contains(aChar + "")) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            protected void onPostExecute(List<SearchDetail.SearchBooks> searchBookses) {
                try {
                    mView.showSearchResultList(searchBookses);
                    mView.cancelLoading();
                } catch (NullPointerException e) {
                    ToastUtils.showToast("请求数据失败，请重试");
                    return;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
