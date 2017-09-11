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

import com.guashu.book.bean.BookSource;
import com.guashu.book.base.RxPresenter;
import com.guashu.book.bean.SourceInfo;
import com.guashu.book.ui.contract.BookSourceContract;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class BookSourcePresenter extends RxPresenter<BookSourceContract.View> implements BookSourceContract.Presenter {
    @Inject
    public BookSourcePresenter() {
    }

    @Override
    public void getBookSource(final String name, final BookSource source) {
        new AsyncTask<Void, Void, List<SourceInfo>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                mView.showLoading();
            }

            @Override
            protected List<SourceInfo> doInBackground(Void... params) {
                List<SourceInfo> sourceInfos = bookApiManager.matchSource(name, source);
                Collections.sort(sourceInfos);
                return sourceInfos;
            }

            @Override
            protected void onPostExecute(List<SourceInfo> bookSources) {
                if (mView != null) {
                    mView.showBookSource(bookSources);
                    mView.hideLoading();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
