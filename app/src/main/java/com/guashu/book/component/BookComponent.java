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
package com.guashu.book.component;

import com.guashu.book.manager.BookApiManager;
import com.guashu.book.ui.activity.BookDetailActivity;
import com.guashu.book.ui.activity.BookSourceActivity;
import com.guashu.book.ui.activity.ReadActivity;
import com.guashu.book.ui.activity.SearchActivity;

import dagger.Component;

@Component(dependencies = AppComponent.class)
public interface BookComponent {
    BookDetailActivity inject(BookDetailActivity activity);

    ReadActivity inject(ReadActivity activity);

    BookSourceActivity inject(BookSourceActivity activity);

    SearchActivity inject(SearchActivity activity);

    BookApiManager inject(BookApiManager bookApiManager);
}