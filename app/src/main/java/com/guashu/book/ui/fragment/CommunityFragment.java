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

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.guashu.book.bean.BookType;
import com.guashu.book.R;
import com.guashu.book.base.BaseFragment;
import com.guashu.book.bean.TypeBean;
import com.guashu.book.bean.support.FindBean;
import com.guashu.book.common.OnRvItemClickListener;
import com.guashu.book.component.AppComponent;
import com.guashu.book.ui.activity.SubRankActivity;
import com.guashu.book.ui.adapter.FindAdapter;
import com.guashu.book.view.SupportDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.guashu.book.base.Constant.ICON_TYPE;

public class CommunityFragment extends BaseFragment implements OnRvItemClickListener<FindBean> {

    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;

    private FindAdapter mAdapter;
    private List<TypeBean> mList = new ArrayList<>();

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_find;
    }

    @Override
    public void initDatas() {
        mList.clear();
        BookType[] values = BookType.values();
        for (int i = 0; i < values.length; i++) {
            mList.add(new TypeBean(values[i], i >= ICON_TYPE.length ? ICON_TYPE[0] : ICON_TYPE[i]));
        }
    }

    @Override
    public void configViews() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SupportDividerItemDecoration(mContext, LinearLayoutManager.VERTICAL, true));

        List<FindBean> list = new ArrayList<>();
        for (TypeBean typeBean : mList) {
            list.add(new FindBean(typeBean.getType().getName(), typeBean.getIconResId()));
        }
        mAdapter = new FindAdapter(mContext, list, this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {

    }

    @Override
    public void attachView() {

    }

    @Override
    public void onItemClick(View view, int position, FindBean data) {
        SubRankActivity.startActivity(activity, mList.get(position).getType());
    }

}
