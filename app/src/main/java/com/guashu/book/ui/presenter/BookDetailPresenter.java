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

import com.guashu.book.base.RxPresenter;
import com.guashu.book.bean.BookDetail;
import com.guashu.book.bean.BookSource;
import com.guashu.book.ui.contract.BookDetailContract;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.ToastUtils;

import javax.inject.Inject;

/**
 * @author lfh.
 * @date 2016/8/6.
 */
public class BookDetailPresenter extends RxPresenter<BookDetailContract.View> implements BookDetailContract.Presenter<BookDetailContract.View> {
    @Inject
    public BookDetailPresenter() {

    }

    public void getBookDetail(final BookSource bookSource, final String bookUrl) {
        new AsyncTask<Void, Void, BookDetail>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mView.showLoading();
            }

            @Override
            protected BookDetail doInBackground(Void... params) {
                return getBookApi(bookSource).getBookDetail(bookUrl);
            }

            @Override
            protected void onPostExecute(BookDetail bookDetail) {
                try {
                    mView.showBookDetail(bookDetail);
                } catch (NullPointerException e) {
                    mView.showError();
                    return;
                } finally {
                    if (mView != null) {
                        mView.hideLoading();
                    }
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
