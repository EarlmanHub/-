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
package com.guashu.book.ui.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guashu.book.bean.BookType;
import com.guashu.book.R;
import com.guashu.book.base.BaseRVFragment;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.bean.Recommend;
import com.guashu.book.bean.support.DownloadMessage;
import com.guashu.book.bean.support.DownloadProgress;
import com.guashu.book.bean.support.DownloadQueue;
import com.guashu.book.bean.support.RefreshCollectionListEvent;
import com.guashu.book.bean.support.AgreeDisclaimerEvent;
import com.guashu.book.component.AppComponent;
import com.guashu.book.component.DaggerMainComponent;
import com.guashu.book.manager.CollectionsManager;
import com.guashu.book.manager.SettingManager;
import com.guashu.book.service.DownloadBookService;
import com.guashu.book.ui.activity.BookDetailActivity;
import com.guashu.book.ui.activity.MainActivity;
import com.guashu.book.ui.activity.ReadActivity;
import com.guashu.book.ui.contract.RecommendContract;
import com.guashu.book.ui.easyadapter.RecommendAdapter;
import com.guashu.book.ui.presenter.RecommendPresenter;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.view.recyclerview.adapter.RecyclerArrayAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

import static com.guashu.book.base.Constant.KEY_IS_NEVEL;


public class RecommendFragment extends BaseRVFragment<RecommendPresenter, Recommend.RecommendBook> implements RecommendContract.View, RecyclerArrayAdapter.OnItemLongClickListener {
    public final static String BUNDLE_TYPE = "type";
    @Bind(R.id.llBatchManagement)
    LinearLayout llBatchManagement;
    @Bind(R.id.tvSelectAll)
    TextView tvSelectAll;
    @Bind(R.id.tvDelete)
    TextView tvDelete;
    @Bind(R.id.pb_loading)
    ProgressBar mPbLoading;

    private BookType bookType;
    private boolean isNevel;
    private boolean isSelectAll = false;

    private List<BookMixAToc.mixToc.Chapters> chaptersList = new ArrayList<>();

    public static RecommendFragment newInstance(BookType type) {
        RecommendFragment fragment = new RecommendFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_recommend;
    }

    @Override
    public void initDatas() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void configViews() {
        initAdapter(RecommendAdapter.class, true, false);
        mAdapter.setOnItemLongClickListener(this);

        mRecyclerView.setEmptyView(R.layout.bookshelf_empty_view);
        mRecyclerView.setProgressView(R.layout.common_progress_view);
        mRecyclerView.setScrollbarStyle(0);

        mRecyclerView.getEmptyView().findViewById(R.id.btnToAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) activity).setCurrentItem(1);
            }
        });
        onRefresh();
    }

    private void showRecommendList() {
        try {
            isNevel = getArguments().getBoolean(KEY_IS_NEVEL);
        } catch (NullPointerException e) {
            isNevel = false;
        }

        if (isNevel) {//当前如果是书架界面
            if (!SettingManager.getInstance().isUserAgreeDis()) {// 判断用户是否已经同意免责声明
                showRecommendList(null);
                mRecyclerView.hideAll();
                return;
            }
            if (!SettingManager.getInstance().hasRecommend()) {//判断是否已经推荐过
                mPresenter.getRecommendBooks();
            } else {
                showCollectionsRecommendList();
            }
        } else {
            bookType = (BookType) getArguments().get(BUNDLE_TYPE);
            mPresenter.getRecommendList(bookType.getValue());
        }
    }


    public boolean onBackPressed() {
        if (llBatchManagement != null && isVisible(llBatchManagement)) {
            goneBatchManagementAndRefreshUI();
            tvSelectAll.setText(activity.getString(R.string.selected_all));
            isSelectAll = false;
            return true;
        }
        return false;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerMainComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public void showCollectionsRecommendList() {
        CollectionsManager cm = CollectionsManager.getInstance();
        List<Recommend.RecommendBook> collectionList;
        collectionList = cm.getCollectionListBySort();
        showRecommendList(collectionList);
    }

    @Override
    public void showRecommendList(List<Recommend.RecommendBook> list) {
        mAdapter.clear();
        mAdapter.addAll(list);
        mAdapter.notifyDataSetChanged();
    }

//    @Override
//    public void showBookToc(String bookId, List<BookMixAToc.mixToc.Chapters> list) {
//        chaptersList.clear();
//        chaptersList.addAll(list);
//        DownloadBookService.post(new DownloadQueue(bookId, list, 1, list.size()));
//        dismissDialog();
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downloadMessage(final DownloadMessage msg) {
        mRecyclerView.setTipViewText(msg.message);
        if (msg.isComplete) {
            mRecyclerView.hideTipView(2200);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showDownProgress(DownloadProgress progress) {
        mRecyclerView.setTipViewText(progress.message);
    }

    @Override
    public void onItemClick(int position) {
        if (isVisible(llBatchManagement)) {//批量管理时
            mAdapter.checkItem(position);
            return;
        }
        Recommend.RecommendBook recommendBook = mAdapter.getItem(position);
        if (isNevel) {//如果当前为书架界面，则跳转读书界面
            ReadActivity.startActivity(activity, recommendBook, recommendBook.isFromSD);
        } else {
            BookDetailActivity.startActivity(activity, recommendBook.bookSource, recommendBook.url);
        }
    }

    @Override
    public boolean onItemLongClick(int position) {
        if (!isNevel) {
            return true;
        }
        //批量管理时，屏蔽长按事件
        if (isVisible(llBatchManagement)) return false;
        showLongClickDialog(position);
        return false;
    }

    /**
     * 显示长按对话框
     *
     * @param position
     */
    private void showLongClickDialog(final int position) {
        final boolean isTop = CollectionsManager.getInstance().isTop(mAdapter.getItem(position)._id);
        String[] items;
        DialogInterface.OnClickListener listener;
        if (mAdapter.getItem(position).isFromSD) {
            items = getResources().getStringArray(R.array.recommend_item_long_click_choice_local);
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //置顶、取消置顶
                            CollectionsManager.getInstance().top(mAdapter.getItem(position)._id, !isTop);
                            break;
                        case 1:
                            //删除
                            List<Recommend.RecommendBook> removeList = new ArrayList<>();
                            removeList.add(mAdapter.getItem(position));
                            showDeleteCacheDialog(removeList);
                            break;
                        case 2:
                            //批量管理
                            showBatchManagementLayout();
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            };
        } else {
            items = getResources().getStringArray(R.array.recommend_item_long_click_choice);
            listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Recommend.RecommendBook recommendBook = mAdapter.getItem(position);
                    switch (which) {
                        case 0:
                            //置顶、取消置顶
                            CollectionsManager.getInstance().top(recommendBook._id, !isTop);
                            break;
                        case 1:
                            //书籍详情
                            BookDetailActivity.startActivity(activity,
                                    recommendBook.bookSource, recommendBook.url);
                            break;
                        case 2:
                            //删除
                            List<Recommend.RecommendBook> removeList = new ArrayList<>();
                            removeList.add(recommendBook);
                            showDeleteCacheDialog(removeList);
                            break;
                        case 3:
                            //批量管理
                            showBatchManagementLayout();
                            break;
                        default:
                            break;
                    }
                    dialog.dismiss();
                }
            };
        }
        if (isTop) items[0] = getString(R.string.cancle_top);
        new AlertDialog.Builder(activity)
                .setTitle(mAdapter.getItem(position).title)
                .setItems(items, listener)
                .setNegativeButton(null, null)
                .create().show();
    }

    /**
     * 显示删除本地缓存对话框
     *
     * @param removeList
     */
    private void showDeleteCacheDialog(final List<Recommend.RecommendBook> removeList) {
        final boolean selected[] = {true};
        new AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.remove_selected_book))
                .setMultiChoiceItems(new String[]{activity.getString(R.string.delete_local_cache)}, selected,
                        new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                selected[0] = isChecked;
                            }
                        })
                .setPositiveButton(activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        new AsyncTask<String, String, String>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                showDialog();
                            }

                            @Override
                            protected String doInBackground(String... params) {
                                CollectionsManager.getInstance().removeSome(removeList, selected[0]);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                mRecyclerView.showTipViewAndDelayClose("成功移除书籍");
                                for (Recommend.RecommendBook bean : removeList) {
                                    mAdapter.remove(bean);
                                }
                                if (isVisible(llBatchManagement)) {
                                    //批量管理完成后，隐藏批量管理布局并刷新页面
                                    goneBatchManagementAndRefreshUI();
                                }
                                hideDialog();
                            }
                        }.execute();

                    }
                })
                .setNegativeButton(activity.getString(R.string.cancel), null)
                .create().show();
    }

    /**
     * 隐藏批量管理布局并刷新页面
     */
    public void goneBatchManagementAndRefreshUI() {
        if (mAdapter == null) return;
        gone(llBatchManagement);
        for (Recommend.RecommendBook bean :
                mAdapter.getAllData()) {
            bean.showCheckBox = false;
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 显示批量管理布局
     */
    private void showBatchManagementLayout() {
        visible(llBatchManagement);
        for (Recommend.RecommendBook bean : mAdapter.getAllData()) {
            bean.showCheckBox = true;
            bean.isSeleted = false;
        }
        mAdapter.notifyDataSetChanged();
    }


    @OnClick(R.id.tvSelectAll)
    public void selectAll() {
        isSelectAll = !isSelectAll;
        tvSelectAll.setText(isSelectAll ? activity.getString(R.string.cancel_selected_all) : activity.getString(R.string.selected_all));
        for (Recommend.RecommendBook bean : mAdapter.getAllData()) {
            bean.isSeleted = isSelectAll;
        }
        mAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.tvDelete)
    public void delete() {
        List<Recommend.RecommendBook> removeList = new ArrayList<>();
        for (Recommend.RecommendBook bean : mAdapter.getAllData()) {
            if (bean.isSeleted) removeList.add(bean);
        }
        if (removeList.isEmpty()) {
            mRecyclerView.showTipViewAndDelayClose(activity.getString(R.string.has_not_selected_delete_book));
        } else {
            showDeleteCacheDialog(removeList);
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        gone(llBatchManagement);
        //不加下面这句代码会导致，添加本地书籍的时候，部分书籍添加后直接崩溃
        //报错：Scrapped or attached views may not be recycled. isScrap:false isAttached:true
        showRecommendList();
        mAdapter.notifyDataSetChanged();
        mRecyclerView.setRefreshing(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void RefreshCollectionList(RefreshCollectionListEvent event) {
        if (!isNevel) {
            return;
        }
        showCollectionsRecommendList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void agreeDisclaimer(AgreeDisclaimerEvent event) {
        //首次进入APP，需要同意免责声明
        SettingManager.getInstance().saveUserAgreeDis();
        // 展示书籍列表
        onRefresh();
    }

    @Override
    public void showLoading() {
        if (!mPbLoading.isShown()) {
            mPbLoading.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoading() {
        if (mPbLoading.isShown()) {
            mPbLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError() {
        loaddingError();
        dismissDialog();
    }

    @Override
    public void complete() {
        mRecyclerView.setRefreshing(false);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!getUserVisibleHint()) {
            goneBatchManagementAndRefreshUI();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
