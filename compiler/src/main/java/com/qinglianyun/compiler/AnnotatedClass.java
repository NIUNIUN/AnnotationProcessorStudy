package com.qinglianyun.compiler;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.NullType;
import javax.lang.model.util.Elements;

/**
 * 用于保存哪些被注解的对象
 * Created by tang_xqing on 2020/11/5.
 */
public class AnnotatedClass {
    private static class TypeUtil {
        static final ClassName BINDER = ClassName.get("com.qinglianyun.api", "ViewBinder");
        static final ClassName PROVIDER = ClassName.get("com.qinglianyun.api", "ViewFinder");
    }

    private TypeElement mTypeElement;
    private ArrayList<BindViewField> mFields;
    private Elements mElements;

    public AnnotatedClass(TypeElement typeElement, Elements elements) {
        mTypeElement = typeElement;
        mElements = elements;
        mFields = new ArrayList<>();
    }

    void addField(BindViewField field) {
        mFields.add(field);
    }

    JavaFile generateFile() {
        // 生成bindView(host,source,finder)方法
        //.asType()： 返回TypeMirror,TypeMirror是元素的类型信息，包括包名，类(或方法，或参数)名/类型

        // 生成参数
        ParameterSpec savaParame = ParameterSpec.builder(ClassName.get("android.os", "Bundle"), "savedInstanceState")
                .addAnnotation(ClassName.get("android.support.annotation","Nullable"))
                .build();

        ParameterSpec.Builder builder = ParameterSpec.builder(TypeName.get(mTypeElement.asType()), "host");

        MethodSpec.Builder bindViewMethod = MethodSpec.methodBuilder("bindView")
                .addModifiers(Modifier.PUBLIC) // 修饰类型
                .addAnnotation(Override.class)  //添加注解
                .addParameter(builder.build())  // 添加参数
                .addParameter(TypeName.OBJECT, "source")
                .addParameter(TypeUtil.PROVIDER, "finder")
                .returns(TypeName.VOID);    // 添加返回值

        for (BindViewField field : mFields) {
            // 添加方法体的代码块
            bindViewMethod.addStatement("host.$N = ($T)finder.findView(source,$L)",
                    field.getFieldName(), ClassName.get(field.getFieldType()), field.getResId());
        }

        // 生成unBindView(host) 方法
        MethodSpec.Builder unbindViewMethod = MethodSpec.methodBuilder("unBindView")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(mTypeElement.asType()), "host");
        for (BindViewField field : mFields) {
            unbindViewMethod.addStatement("host.$N = null", field.getFieldName());
        }


        // 生成class
        TypeSpec injectClass = TypeSpec.classBuilder(mTypeElement.getSimpleName() + "$$ViewBinder")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.BINDER, TypeName.get(mTypeElement.asType())))
                .addMethod(bindViewMethod.build())
                .addMethod(unbindViewMethod.build())
                .build();

        // getPackageOf() 获取包名
        String packageName = mElements.getPackageOf(mTypeElement).getQualifiedName().toString();

        // 生成java代码
        return JavaFile.builder(packageName, injectClass).build();
    }
}
