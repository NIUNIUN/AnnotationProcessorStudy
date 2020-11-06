package com.qinglianyun.api;

import android.app.Activity;
import android.view.View;

/**
 * Activity UI查找提供者
 * Created by tang_xqing on 2020/11/5.
 */
public class ActivityViewFinder implements ViewFinder {
    @Override
    public View findView(Object object, int id) {
        return ((Activity) object).findViewById(id);
    }
}
