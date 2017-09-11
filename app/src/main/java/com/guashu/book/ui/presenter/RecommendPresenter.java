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

import com.guashu.book.base.IBookApi;
import com.guashu.book.base.RxPresenter;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.Recommend;
import com.guashu.book.manager.CollectionsManager;
import com.guashu.book.manager.SettingManager;
import com.guashu.book.ui.contract.RecommendContract;
import com.guashu.book.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

/**
 * @author yuyh.
 * @date 2016/8/3.
 */
public class RecommendPresenter extends RxPresenter<RecommendContract.View>
        implements RecommendContract.Presenter<RecommendContract.View> {

    @Inject
    public RecommendPresenter() {
    }

    @Override
    public void getRecommendList(final String bookType) {
        new AsyncTask<Void, Void, List<Recommend.RecommendBook>>() {
            @Override
            protected void onPreExecute() {
                mView.showLoading();
            }

            @Override
            protected List<Recommend.RecommendBook> doInBackground(Void... params) {
                List<Recommend.RecommendBook> books = new ArrayList<>();
                Map<BookSource, IBookApi> allBookApi = bookApiManager.getAllBookApi();
                for (BookSource bookSource : allBookApi.keySet()) {
                    List<Recommend.RecommendBook> recommends = allBookApi.get(bookSource).getRecommend(bookType);
                    if (recommends != null) {
                        books.addAll(recommends);
                    }
                }
                return books;
            }

            @Override
            protected void onPostExecute(List<Recommend.RecommendBook> recommendBooks) {
                if (recommendBooks == null) {
                    recommendBooks = new ArrayList<>();
                }
                if (mView != null) {
                    mView.showRecommendList(recommendBooks);
                    mView.hideLoading();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void getRecommendBooks() {
        new AsyncTask<Void, Void, List<Recommend.RecommendBook>>() {
            @Override
            protected void onPreExecute() {
                mView.showLoading();
            }

            @Override
            protected List<Recommend.RecommendBook> doInBackground(Void... params) {
                List<Recommend.RecommendBook> recommendBooks = new ArrayList<>();
                Map<BookSource, IBookApi> allBookApi = bookApiManager.getAllBookApi();
                for (IBookApi bookApi : allBookApi.values()) {
                    List<Recommend.RecommendBook> books = bookApi.getRecommendBooks();
                    if (books != null) {
                        recommendBooks.addAll(books);
                    }
                }
                CollectionsManager.getInstance().add(recommendBooks);
                return recommendBooks;
            }

            @Override
            protected void onPostExecute(List<Recommend.RecommendBook> recommendBooks) {
                if (mView != null) {
                    if (recommendBooks == null) {
                        recommendBooks = new ArrayList<>();
                    } else {
                        SettingManager.getInstance().saveRecommend();
                    }
                    LogUtils.e("SOURCE_CHECK", recommendBooks);
                    mView.showRecommendList(recommendBooks);
                    mView.hideLoading();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
