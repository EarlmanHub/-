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
package com.guashu.book.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.guashu.book.R;
import com.guashu.book.base.BaseRVActivity;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.SearchDetail;
import com.guashu.book.bean.SourceInfo;
import com.guashu.book.component.AppComponent;
import com.guashu.book.component.DaggerBookComponent;
import com.guashu.book.manager.CollectionsManager;
import com.guashu.book.manager.SettingManager;
import com.guashu.book.ui.contract.BookSourceContract;
import com.guashu.book.ui.easyadapter.BookSourceAdapter;
import com.guashu.book.ui.presenter.BookSourcePresenter;
import com.guashu.book.utils.BookUtil;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.utils.ToastUtils;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class BookSourceActivity extends BaseRVActivity<SourceInfo> implements BookSourceContract.View {
    private static final String BOOK_NAME = "bookName";
    private static final String BOOK_ID = "bookId";
    private static final String BOOK_SOURCE = "bookSource";

    @Bind(R.id.ll_container)
    View mContainer;
    @Bind(R.id.tv_source_count)
    TextView mSourceCount;
    @Bind(R.id.tv_current_source)
    TextView mCurrentSource;

    private String mReadBookId;//当前正在阅读书籍的id
    private List<SourceInfo> mSourseInfoList;
    private BookSource mSource;//当前正在阅读书籍源

    public static void start(Activity activity, String bookId, String bookName, BookSource source) {
        activity.startActivity(new Intent(activity, BookSourceActivity.class)
                .putExtra(BOOK_ID, bookId)
                .putExtra(BOOK_NAME, bookName)
                .putExtra(BOOK_SOURCE, source));
    }

    @Inject
    BookSourcePresenter mPresenter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_common_recyclerview;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerBookComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void initToolBar() {
        mCommonToolbar.setTitle("所有书源");
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {
        initAdapter(BookSourceAdapter.class, false, false);
    }

    @Override
    public void configViews() {
        String bookName = null;
        try {
            bookName = getIntent().getStringExtra(BOOK_NAME);
            mReadBookId = getIntent().getStringExtra(BOOK_ID);
            mSource = (BookSource) getIntent().getSerializableExtra(BOOK_SOURCE);

            LogUtils.e("mReadBookId = " + mReadBookId);
            LogUtils.e("mSource = " + mSource.getName());
        } catch (NullPointerException e) {
            ToastUtils.showSingleToast("匹配源出错，请重试");
            finish();
        }
        mPresenter.attachView(this);
        mPresenter.getBookSource(bookName, mSource);

        mCurrentSource.setText(mSource.getName());
    }

    @Override
    public void onItemClick(final int position) {
        new AlertDialog.Builder(this)
                .setTitle("温馨提示")
                .setMessage("换源将导致已缓冲的章节失效，是否继续？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finishActivity(ReadActivity.class);
                        switchSource(position);
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }

    /**
     * 切换书源
     */
    private void switchSource(int position) {
        // 获取旧源正在读的小说阅读记录
        int[] readProgress = SettingManager.getInstance().getReadProgress(mReadBookId);
        // 获取新源中同名小说对象
        SearchDetail.SearchBooks book = mSourseInfoList.get(position).book;
        LogUtils.e(book);
        // 设置读数记录至新源的小说
        SettingManager.getInstance().saveReadProgress(book._id, readProgress);
        // 开启读书界面
        Recommend.RecommendBook recommendBook = BookUtil.obtionBook(book);
        ReadActivity.startActivity(this, recommendBook);
        finish();
        // 收藏新源小说
        CollectionsManager.getInstance().add(recommendBook);
        CollectionsManager.getInstance().remove(mReadBookId);
    }


    @Override
    public void showBookSource(List<SourceInfo> sourceList) {
        if (sourceList == null) {
            ToastUtils.showSingleToast("匹配源出错，请重试");
            finish();
            return;
        }
//        ToastUtils.showSingleToast("总共为您搜索到" + sourceList.size() + "个新的可用书源！");
        mSourseInfoList = sourceList;
        mSourceCount.setText(String.format(mContext.getString(
                R.string.source_count), sourceList.size()));
        mAdapter.clear();
        mAdapter.addAll(sourceList);
    }

    @Override
    public void showLoading() {
        showDialog();
    }

    @Override
    public void hideLoading() {
        visible(mContainer);
        hideDialog();
    }

    @Override
    public void showError() {
        loaddingError();
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

}
