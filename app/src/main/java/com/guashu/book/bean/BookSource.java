package com.guashu.book.bean;

import com.guashu.book.api.BaYi;
import com.guashu.book.api.BiQuGeApi;
import com.guashu.book.api.DaShuBaoApi;
import com.guashu.book.api.DingDianApi;
import com.guashu.book.api.EApi;
import com.guashu.book.api.KuWenApi;
import com.guashu.book.api.LieWenApi;
import com.guashu.book.api.MianHuaTangApi;
import com.guashu.book.api.PinShuApi;
import com.guashu.book.api.QianQianApi;
import com.guashu.book.api.WuLinApi;
import com.guashu.book.base.IBookApi;

import java.io.Serializable;

/**
 * Created by user on 17-7-28.
 * <p>
 * BookSource不能出现IBookApi对象,否侧会造成两个对象死锁
 * 所以在BookSource中保留IBook.class对象，BookApiManager利用反射生成对象
 */

public enum BookSource implements Serializable {

    BIQUGE("www.biquzi.com",
            "笔趣阁",
            BiQuGeApi.class,
            true),

    DINGDIAN("www.23us.so",
            "顶点小说",
            DingDianApi.class,
            true),

    DASHUBAO("www.dashubao.com",
            "大书包",
            DaShuBaoApi.class,
            true),

    EXIAOSHUO("www.zwda.com",
            "E小说",
            EApi.class,
            true),

//    KK("www.2kxs.com",
//            "2K小说",
//            KKApi.class,
//            true),

    KUWEN("www.kuwen.net",
            "酷文小说",
            KuWenApi.class,
            true),

    MIANHUATANG("www.mianhuatang.la",
            "棉花糖小说",
            MianHuaTangApi.class,
            true),

    PINSHU("www.vodtw.com",
            "品书网",
            PinShuApi.class,
            true),

    BAYI("www.zwdu.com",
            "八一中文网",
            BaYi.class,
            true),

    LIEWEN("www.liewen.cc",
            "猎文网",
            LieWenApi.class,
            true),

    QIANQIAN("www.qqxs.la",
            "千千小说",
            QianQianApi.class,
            true),

    WULIN("www.50zw.la",
            "武林中文网",
            WuLinApi.class,
            true);

    private String link;//域名网址
    private String name;//名称
    private Class<? extends IBookApi> apiClass;//对应书源api.class
    private boolean isFree;//是否免费

    BookSource(String link,
               String name,
               Class<? extends IBookApi> apiClazz,
               boolean isFree) {
        this.link = link;
        this.name = name;
        this.apiClass = apiClazz;
        this.isFree = isFree;
    }

    public String getLink() {
        return link;
    }

    public String getName() {
        return name;
    }

    public boolean isFree() {
        return isFree;
    }

    public Class<? extends IBookApi> getApiClass() {
        return apiClass;
    }
}
