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
import android.view.View;
import android.view.ViewGroup;

import com.guashu.book.R;
import com.guashu.book.bean.SourceInfo;
import com.guashu.book.view.recyclerview.adapter.BaseViewHolder;
import com.guashu.book.view.recyclerview.adapter.RecyclerArrayAdapter;

/**
 * 查询
 *
 * @author yuyh.
 * @date 16/9/3.
 */
public class BookSourceAdapter extends RecyclerArrayAdapter<SourceInfo> {


    public BookSourceAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new BaseViewHolder<SourceInfo>(parent, R.layout.item_source) {
            @Override
            public void setData(SourceInfo item) {
                holder.setText(R.id.tv_source_title, item.bookSource.getName())
                        .setText(R.id.tv_recent_updata, String.format(mContext.getString(R.string.recent_chapter), item.book.updateTime))
                        .setText(R.id.tv_source_link, item.bookSource.getLink());
                View isFreeView = holder.getView(R.id.tv_is_free);
                isFreeView.setVisibility(item.bookSource.isFree() ? View.GONE : View.VISIBLE);
            }
        };
    }
}
