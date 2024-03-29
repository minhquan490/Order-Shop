package com.bachlinh.order.annotation.processor;

import com.google.auto.service.AutoService;
import jakarta.persistence.Entity;

import com.bachlinh.order.annotation.processor.writer.ClassWriter;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Set;

@AutoService(Processor.class)
public class EntityMetaModelProcessor extends AbstractProcessor {

    private Elements elements;
    private Filer filer;
    private Messager messager;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.elements = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        this.types = processingEnv.getTypeUtils();
        messager.printMessage(Diagnostic.Kind.NOTE, "Generate entity metamodel.");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(Entity.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var sourceTemplate = "{0}.{1}_";
        var filteredResults = filter(roundEnv.getRootElements());
        for (var element : filteredResults) {
            try {
                write(element, sourceTemplate);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private Collection<? extends Element> filter(Set<? extends Element> elements) {
        return elements.stream()
                .filter(element -> element.getAnnotation(Entity.class) != null)
                .toList();
    }

    private void write(Element element, String sourceTemplate) throws IOException {
        var sourceName = MessageFormat.format(sourceTemplate, elements.getPackageOf(element).getQualifiedName().toString(), element.getSimpleName());
        try {
            var writer = ClassWriter.entityMetaModelWriter(filer.createSourceFile(sourceName), elements, types);
            writer.write(element);
        } catch (Exception e) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Can not create proxy for entity [%s]", element.getSimpleName()));
            throw e;
        }
    }
}
