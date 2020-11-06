package com.qinglianyun.compiler;

import com.qinglianyun.annotationn.BindView;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

/**
 * 被注解对象的成员变量
 * Created by tang_xqing on 2020/11/5.
 */
public class BindViewField {
    private VariableElement mVariableElement;
    private int mResId;

    BindViewField(Element element) {

        TypeMirror typeMirror = element.asType();
        TypeKind kind = typeMirror.getKind();

        // 判断元素类型（）
        if (ElementKind.FIELD != element.getKind()) {
            throw new IllegalArgumentException(String.format("Only fields can be annotated with @%s",
                    BindView.class.getSimpleName()));
        }

        // VariableElement 字段元素
        mVariableElement = (VariableElement) element;
        BindView bindView = mVariableElement.getAnnotation(BindView.class);
        mResId = bindView.value();
        if (mResId < 0) {
            throw new IllegalArgumentException(String.format("value in %s field %s is not valid", BindView.class.getName(), mVariableElement.getSimpleName()));
        }
    }

    Name getFieldName(){
       return  mVariableElement.getSimpleName();
    }

    int getResId(){
        return  mResId;
    }

    /**
     * 变量类型
     * @return
     */
    TypeMirror getFieldType(){
        return mVariableElement.asType();
    }
}
