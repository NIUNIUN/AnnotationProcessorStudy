package com.qinglianyun.api;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

/**
 * 查找注解生成的java类，然后调用类中的方法，执行查找view
 * Created by tang_xqing on 2020/11/5.
 */
public class LCJViewBinder {
    private static final ActivityViewFinder acticityFinder = new ActivityViewFinder();
    private static final Map<String, ViewBinder> binderMap = new HashMap<>();

    public static void bind(Activity activity) {
        bind(activity, activity, acticityFinder);
    }

    /**
     * 根据注解生成的类，去绑定
     *
     * @param host       注解所在的类
     * @param obj        查找View的地方
     * @param viewFinder ui绑定提供者接口
     */
    private static void bind(Object host, Object obj, ViewFinder viewFinder) {
        String className = host.getClass().getName();
        try {
            ViewBinder binder = binderMap.get(className);
            if (null == binder) {
                Class<?> aClass = Class.forName(className + "$$ViewBinder");
                binder = (ViewBinder) aClass.newInstance();
                binderMap.put(className, binder);
            }

            if (null != binder) {
                binder.bindView(host, obj, viewFinder);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接触注解绑定ActivityViewFinder
     *
     * @param host
     */
    public static void unBind(Object host) {
        String className = host.getClass().getName();
        ViewBinder binder = binderMap.get(className);
        if (null != binder) {
            binder.unBindView(host);
        }

        binderMap.remove(binder);
    }
}
