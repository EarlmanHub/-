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
package com.guashu.book.ui.easyadapter;

import android.content.Context;
import android.view.ViewGroup;

import com.guashu.book.R;
import com.guashu.book.bean.SearchDetail;
import com.guashu.book.view.recyclerview.adapter.BaseViewHolder;
import com.guashu.book.view.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 查询
 *
 * @author yuyh.
 * @date 16/9/3.
 */
public class SearchAdapter extends RecyclerArrayAdapter<SearchDetail.SearchBooks> {


    public SearchAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<SearchDetail.SearchBooks>(parent, R.layout.item_search_result_list) {
            @Override
            public void setData(SearchDetail.SearchBooks item) {
                holder.setRoundImageUrl(R.id.ivBookCover, item.cover, R.drawable.cover_default)
                        .setText(R.id.tvBookListTitle, item.title)
                        .setText(R.id.tvSearchInfo, String.format(mContext.getString(R.string.search_result_info), item.bookType + " ", item.updateTime + " ", item.author))
                        .setText(R.id.tvSource, String.format(mContext.getString(
                                R.string.book_detail_source), item.bookSource.getLink()));
            }
        };
    }
}
