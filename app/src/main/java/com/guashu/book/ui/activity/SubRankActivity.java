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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.guashu.book.bean.BookType;
import com.guashu.book.R;
import com.guashu.book.base.BaseActivity;
import com.guashu.book.component.AppComponent;
import com.guashu.book.ui.fragment.RecommendFragment;
import com.guashu.book.view.RVPIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * @author yuyh.
 * @date 16/9/1.
 */
public class SubRankActivity extends BaseActivity {


    public static final String INTENT_TYPE = "type";

    public static void startActivity(Context context, BookType type) {
        context.startActivity(new Intent(context, SubRankActivity.class)
                .putExtra(INTENT_TYPE, type));
    }

    private BookType type;

    @Bind(R.id.viewpagerSubRank)
    ViewPager mViewPager;

    private List<Fragment> mTabContents;
    private FragmentPagerAdapter mAdapter;


    @Override
    public int getLayoutId() {
        return R.layout.activity_sub_rank;
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
    }

    @Override
    public void initToolBar() {
        type = (BookType) getIntent().getSerializableExtra(INTENT_TYPE);
        mCommonToolbar.setTitle(type.getName());
        mCommonToolbar.setNavigationIcon(R.drawable.ab_back);
    }

    @Override
    public void initDatas() {

        mTabContents = new ArrayList<>();
        mTabContents.add(RecommendFragment.newInstance(type));

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return mTabContents.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabContents.get(position);
            }
        };
    }

    @Override
    public void configViews() {
        mViewPager.setAdapter(mAdapter);
    }
}
