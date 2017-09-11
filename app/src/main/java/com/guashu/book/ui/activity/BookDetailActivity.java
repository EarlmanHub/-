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

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guashu.book.R;
import com.guashu.book.base.BaseActivity;
import com.guashu.book.bean.BookDetail;
import com.guashu.book.bean.BookSource;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.support.RefreshCollectionIconEvent;
import com.guashu.book.component.AppComponent;
import com.guashu.book.component.DaggerBookComponent;
import com.guashu.book.manager.CollectionsManager;
import com.guashu.book.ui.contract.BookDetailContract;
import com.guashu.book.ui.easyadapter.glide.GlideRoundTransform;
import com.guashu.book.ui.presenter.BookDetailPresenter;
import com.guashu.book.utils.ToastUtils;
import com.guashu.book.view.DrawableCenterButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by lfh on 2016/8/6.
 */
public class BookDetailActivity extends BaseActivity implements BookDetailContract.View {

    public static String INTENT_BOOK_URL = "bookUrl";
    public static String INTENT_BOOK_SRC = "bookSource";

    public static void startActivity(Context context, BookSource bookSource, String bookUrl) {
        context.startActivity(new Intent(context, BookDetailActivity.class)
                .putExtra(INTENT_BOOK_URL, bookUrl)
                .putExtra(INTENT_BOOK_SRC, bookSource)
        );
    }


    @Bind(R.id.view_root)
    LinearLayout rootView;
    @Bind(R.id.ivBookCover)
    ImageView mIvBookCover;
    @Bind(R.id.tvBookListTitle)
    TextView mTvBookTitle;
    @Bind(R.id.tvBookListAuthor)
    TextView mTvAuthor;
    @Bind(R.id.tvCatgory)
    TextView mTvCatgory;
    @Bind(R.id.tvStatus)
    TextView mTvStatus;
    @Bind(R.id.tvLatelyUpdate)
    TextView mTvLatelyUpdate;
    @Bind(R.id.btnRead)
    DrawableCenterButton mBtnRead;
    @Bind(R.id.btnJoinCollection)
    DrawableCenterButton mBtnJoinCollection;
    @Bind(R.id.tvLastChapter)
    TextView mTvLastChapter;
    @Bind(R.id.tvlongIntro)
    TextView mTvlongIntro;
    @Inject
    BookDetailPresenter mPresenter;

    private String bookUrl;
    private BookSource bookSource;

    private boolean collapseLongIntro = true;
    private Recommend.RecommendBook recommendBook;
    private boolean isJoinedCollections = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_book_detail;
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
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
        mCommonToolbar.setTitle(R.string.book_detail);
    }

    @Override
    public void initDatas() {
        bookUrl = getIntent().getStringExtra(INTENT_BOOK_URL);
        bookSource = (BookSource) getIntent().getSerializableExtra(INTENT_BOOK_SRC);
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        mPresenter.attachView(this);
        mPresenter.getBookDetail(bookSource, bookUrl);
    }

    @Override
    public void showBookDetail(BookDetail data) {
        Glide.with(mContext)
                .load(data.cover)
                .placeholder(R.drawable.cover_default)
                .transform(new GlideRoundTransform(mContext))
                .into(mIvBookCover);

        mTvBookTitle.setText(data.title);
        mTvAuthor.setText(String.format(getString(R.string.book_detail_author), data.author));
        mTvCatgory.setText(String.format(getString(R.string.book_detail_category), data.type));
        mTvStatus.setText(data.status);
        mTvLatelyUpdate.setText(data.updated);
        mTvLastChapter.setText(data.lastChapter);

        mTvlongIntro.setText(data.longIntro);

        recommendBook = new Recommend.RecommendBook();
        recommendBook.title = data.title;
        recommendBook._id = data._id;
        recommendBook.url = bookUrl;
        recommendBook.cover = data.cover;
        recommendBook.lastChapter = data.lastChapter;
        recommendBook.updated = data.updated;
        recommendBook.bookSource = data.bookSource;

        refreshCollectionIcon();
    }

    @Override
    public void showLoading() {
        rootView.setVisibility(View.GONE);
        showDialog();
    }

    @Override
    public void hideLoading() {
        rootView.setVisibility(View.VISIBLE);
        hideDialog();
    }

    /**
     * 刷新收藏图标
     */
    private void refreshCollectionIcon() {
        if (CollectionsManager.getInstance().isCollected(recommendBook._id)) {
            initCollection(false);
        } else {
            initCollection(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RefreshCollectionIcon(RefreshCollectionIconEvent event) {
        refreshCollectionIcon();
    }

    /**
     * 追更新
     */
    @OnClick(R.id.btnJoinCollection)
    public void onClickJoinCollection() {
        if (!isJoinedCollections) {
            if (recommendBook != null) {
                CollectionsManager.getInstance().add(recommendBook);
                ToastUtils.showToast(String.format(getString(
                        R.string.book_detail_has_joined_the_book_shelf), recommendBook.title));
                initCollection(false);
            }
        } else {
            CollectionsManager.getInstance().remove(recommendBook._id);
            ToastUtils.showToast(String.format(getString(
                    R.string.book_detail_has_remove_the_book_shelf), recommendBook.title));
            initCollection(true);
        }
    }

    private void initCollection(boolean coll) {
        if (coll) {
            mBtnJoinCollection.setText(R.string.book_detail_join_collection);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.book_detail_info_add_img);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBtnJoinCollection.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.shape_common_btn_solid_normal));
            mBtnJoinCollection.setCompoundDrawables(drawable, null, null, null);
            mBtnJoinCollection.postInvalidate();
            isJoinedCollections = false;
        } else {
            mBtnJoinCollection.setText(R.string.book_detail_remove_collection);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.book_detail_info_del_img);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mBtnJoinCollection.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.btn_join_collection_pressed));
            mBtnJoinCollection.setCompoundDrawables(drawable, null, null, null);
            mBtnJoinCollection.postInvalidate();
            isJoinedCollections = true;
        }
    }

    /**
     * 开始阅读
     */
    @OnClick(R.id.btnRead)
    public void onClickRead() {
        if (recommendBook == null) return;
        ReadActivity.startActivity(this, recommendBook);
    }

    @OnClick(R.id.tvlongIntro)
    public void collapseLongIntro() {
        if (collapseLongIntro) {
            mTvlongIntro.setMaxLines(20);
            collapseLongIntro = false;
        } else {
            mTvlongIntro.setMaxLines(4);
            collapseLongIntro = true;
        }
    }

    @Override
    public void showError() {
        ToastUtils.showToast("请求数据失败，请重试");
        finish();
    }

    @Override
    public void complete() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }
}
