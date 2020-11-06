package com.qinglianyun.compiler;

import com.google.auto.service.AutoService;
import com.qinglianyun.annotationn.BindView;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;


/**
 * Filer ：注解处理器可用此创建新文件（源文件、类文件、辅助资源文件）。由此方法创建的源文件和类文件将由管理它们的工具（javac）处理。
 */

@AutoService(Processor.class) // 自动注册
@SupportedSourceVersion(SourceVersion.RELEASE_8)  // 指定java版本
@SupportedOptions(value = {"eventBusIndex", "verbose"})  // 额外的配置参数
public class InterfaceProcessor extends AbstractProcessor {
    private static String TAG = InterfaceProcessor.class.getCanonicalName();
    private Messager mMessager;  // 日志管理工具
    private Filer mFiler;   // 文件管理工具，用于生成java文件
    private Elements mElementUtils;   // 元素管理工具
    private Map<String, AnnotatedClass> mAnnotatedClassMap;
    private Types mTypeUtils;  // 类型处理管理工具
    private Map<String, String> mOptions;

    /**
     * @param processingEnv
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mOptions = processingEnv.getOptions();   // 获取build.gradle配置的常量
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mAnnotatedClassMap = new TreeMap<>();   // 为什么使用TreeMap,而不是HashMap

        error("自定义注解处理器（初始化）");
    }

    /**
     * 相当于main()。写扫描、评估和处理注解的代码，以及生成java文件。
     *
     * @param annotations 指定的注解类
     * @param roundEnv    可以查询包含特定注解的被注解元素
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        error("自定义注解处理器（执行）");
        mAnnotatedClassMap.clear();
        try {
            processBindView(roundEnv);
        } catch (Exception e) {
            e.printStackTrace();
            error(e.getMessage());
        }

        try {
            for (AnnotatedClass annotatedClass : mAnnotatedClassMap.values()) {
                // 将生成的注解代码 写入文件中
                annotatedClass.generateFile().writeTo(mFiler);
            }
        } catch (IOException e) {
            e.printStackTrace();
            error("Generate file failed, reason: %s", e.getMessage());
        }

        return true;
    }

    /**
     * 循环遍历已添加的注解，
     *
     * @param roundEnvironment
     */
    private void processBindView(RoundEnvironment roundEnvironment) {

        // getElementsAnnotatedWith() 获取特定注解对应的Element信息
        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindView.class)) {
            AnnotatedClass annotatedClas = getAnnotatedClas(element);
            BindViewField bindViewField = new BindViewField(element);
            annotatedClas.addField(bindViewField);
        }
    }

    /**
     * @param element 注解是作用在成员变量上，所以它是成员变量元素。
     * @return
     */
    private AnnotatedClass getAnnotatedClas(Element element) {
        /**
         * getEnclosedElements(): 获取属性、方法
         * getEnclosingElement(): 获取该元素的父元素。
         *    如果是PackageElement则返回null，如果是TypeElement则返回PackageElement，如果是TypeParameterElement则返回泛型Element
         *
         * VariableElement 成员变量元素
         * TypeElement 表示一个类或者接口元素
         * TypeParameterElement:表示类，接口，方法的泛型类型例如T
         *
         * getQualifiedName(): 获取类的全限定名，Element没有这个方法它的子类有，例如TypeElement，得到的就是类的全类名（包名）
         */
        TypeElement typeElement = (TypeElement) element.getEnclosingElement();
        String fullName = typeElement.getQualifiedName().toString();  // 类的全限定名
        AnnotatedClass annotatedClass = mAnnotatedClassMap.get(fullName);
        if (null == annotatedClass) {
            annotatedClass = new AnnotatedClass(typeElement, mElementUtils);
            mAnnotatedClassMap.put(fullName, annotatedClass);
        }
        return annotatedClass;
    }

    /**
     * 输出日志
     *
     * @param msg
     * @param args
     */
    private void error(String msg, Object... args) {
        // Diagnostic.Kind.ERROR 不要随便使用ERROR，因为会终止编译😂
        mMessager.printMessage(Diagnostic.Kind.WARNING, String.format(msg, args));
    }

    /**
     * 必须指定注解，以便注解处理器注册到指定注解上
     * 可以使用注解@SupportedAnnotationTypes() 来指定支持的注解
     *
     * @return
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        set.add(BindView.class.getCanonicalName());
        return set;
    }

    /**
     * 指定使用Java版本
     *
     * @return
     */
  /*  @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }*/
}
