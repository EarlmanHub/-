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
package com.guashu.book.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.widget.TextView;

import com.guashu.book.R;
import com.guashu.book.bean.BookMixAToc;
import com.guashu.book.manager.CacheManager;
import com.guashu.book.manager.SettingManager;
import com.guashu.book.ui.easyadapter.abslistview.EasyLVAdapter;
import com.guashu.book.ui.easyadapter.abslistview.EasyLVHolder;
import com.guashu.book.utils.AppUtils;
import com.guashu.book.utils.FileUtils;
import com.guashu.book.utils.LogUtils;
import com.guashu.book.view.readview.PageFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author lfh.
 * @date 16/8/11.
 */
public class TocListAdapter extends EasyLVAdapter<BookMixAToc.mixToc.Chapters> {
    //private int currentChapter;
    private String bookId;
    private BookMixAToc.mixToc.Chapters currentChapter;
    private boolean isEpub = false;
    private boolean isReverse;
    private int currentPosition = 0;

    public TocListAdapter(Context context, List<BookMixAToc.mixToc.Chapters> list, String bookId, BookMixAToc.mixToc.Chapters currentChapter) {
        super(context, list, R.layout.item_book_read_toc_list);
        this.currentChapter = currentChapter;
        this.bookId = bookId;
    }

    @Override
    public void convert(EasyLVHolder holder, int position, BookMixAToc.mixToc.Chapters chapters) {
        TextView tvTocItem = holder.getView(R.id.tvTocItem);
        tvTocItem.setText(chapters.title);
        Drawable drawable;
//        LogUtils.e("TAG", "boolean isReverse = " + isReverse + ",currentChapter.title = " + currentChapter.title + ", mList.get(position).title = " + mList.get(position).title);

        if (currentChapter != null && currentChapter.title.equals(mList.get(position).title)) {
            tvTocItem.setTextColor(ContextCompat.getColor(mContext, R.color.light_red));
            drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_toc_item_activated);
            currentPosition = position;
            LogUtils.e("TAG","currentPosition = "+position);
        } else if (isEpub || isCached(mList.get(position), position)) {
            tvTocItem.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
            drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_toc_item_download);
        } else {
            tvTocItem.setTextColor(ContextCompat.getColor(mContext, R.color.light_black));
            drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_toc_item_normal);
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        tvTocItem.setCompoundDrawables(drawable, null, null, null);
    }

    public void setCurrentChapter(BookMixAToc.mixToc.Chapters chapter) {
        currentChapter = chapter;
//        Log.e("TAG", "setCurrentChapter", new AndroidRuntimeException());
        notifyDataSetChanged();
    }

    public void setEpub(boolean isEpub) {
        this.isEpub = isEpub;
    }

    public void reverseSort(boolean isReverse) {
        this.isReverse = isReverse;
        Collections.reverse(mList);
        notifyDataSetChanged();
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    private boolean isCached(BookMixAToc.mixToc.Chapters chapter, int position) {
        if (isReverse == false) {

            return FileUtils.getChapterFile(bookId, chapter.order).length() > 10;
        } else {
            return FileUtils.getChapterFile(bookId, mList.size() - position).length() > 10;
        }
    }
}
