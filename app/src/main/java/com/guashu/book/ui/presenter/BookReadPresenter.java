/**
 * Copyright 2016 JustWayward Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.guashu.book.ui.presenter;

import android.os.AsyncTask;

import com.guashu.book.base.RxPresenter;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.ChapterRead;
import com.guashu.book.ui.contract.BookReadContract;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

/**
 * @author lfh.
 * @date 2016/8/7.
 */
public class BookReadPresenter extends RxPresenter<BookReadContract.View>
        implements BookReadContract.Presenter<BookReadContract.View> {

    @Inject
    public BookReadPresenter() {
    }

    @Override
    public void getBookMixAToc(final BookSource bookSource, final String bookUrl, final String bookId) {
        new AsyncTask<String, Void, List<BookMixAToc.mixToc.Chapters>>() {
            @Override
            protected List<BookMixAToc.mixToc.Chapters> doInBackground(String... params) {
                //查看本地缓存
                List<BookMixAToc.mixToc.Chapters> tocList
                        = cacheManager.getTocList(AppUtils.getAppContext(), bookId);
                if (null == tocList) {
                    tocList = getBookApi(bookSource).getChapterList(bookUrl);
                    if (null == tocList) {
                        return null;
                    }
                    BookMixAToc bookMixAToc = new BookMixAToc();
                    BookMixAToc.mixToc mixToc = new BookMixAToc.mixToc();
                    mixToc.chapters = tocList;
                    bookMixAToc.mixToc = mixToc;
                    cacheManager.saveTocList(AppUtils.getAppContext(), bookId, bookMixAToc);
                }
                return tocList;
            }

            @Override
            protected void onPostExecute(List<BookMixAToc.mixToc.Chapters> chapters) {
                try {
                    mView.showBookToc(chapters);
                } catch (NullPointerException e) {
                    ToastUtils.showToast("请求章节列表失败，请重试");
                    if (mView != null) {
                        mView.netError(-1);
                    }
                    return;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void getChapterRead(final BookSource bookSource, final String url, final int chapter) {
        new AsyncTask<String, Void, ChapterRead.Chapter>() {
            @Override
            protected ChapterRead.Chapter doInBackground(String... params) {
                LogUtils.i("bookSource = " + bookSource);
                LogUtils.i("TAG","url = " + url);
                ChapterRead.Chapter chapterRead = getBookApi(bookSource).getChapterRead(url);
                return chapterRead;
            }

            @Override
            protected void onPostExecute(ChapterRead.Chapter chapterStr) {
                try {
                    mView.showChapterRead(chapterStr, chapter);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    LogUtils.i("mView = " + mView);
                    if (mView != null) {
                        mView.netError(chapter);
                    }
                    return;
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}