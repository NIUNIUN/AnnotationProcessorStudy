package com.qinglianyun.api;

/**
 * 绑定解绑接口
 * Created by tang_xqing on 2020/11/5.
 */
public interface ViewBinder<T> {
    void bindView(T host, Object obj, ViewFinder viewFinder);

    void unBindView(T host);
}
