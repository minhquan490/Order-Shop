package com.bachlinh.order.annotation.processor;

import com.bachlinh.order.annotation.processor.writer.ClassWriter;
import com.bachlinh.order.core.annotation.EnableFullTextSearch;
import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

@AutoService(Processor.class)
public class EntityProxyProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        messager.printMessage(Diagnostic.Kind.NOTE, "Generate entity proxy.");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(EnableFullTextSearch.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var sourceTemplate = "{0}.{1}Proxy";
        for (var element : filter(roundEnv.getRootElements())) {
            var annotation = element.getAnnotation(EnableFullTextSearch.class);
            var sourceName = MessageFormat.format(sourceTemplate, annotation.proxyPackage(), element.getSimpleName());
            try {
                var writer = ClassWriter.entityProxyWriter(filer.createSourceFile(sourceName));
                writer.write(element);
            } catch (Exception e) {
                messager.printMessage(Diagnostic.Kind.ERROR, String.format("Can not create proxy for entity [%s]", element.getSimpleName()));
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private Collection<? extends Element> filter(Set<? extends Element> elements) {
        return elements.stream()
                .filter(element -> element.getAnnotation(EnableFullTextSearch.class) != null)
                .toList();
    }
}
