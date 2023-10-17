package com.bachlinh.order.annotation.processor.parser;

import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

import com.bachlinh.order.annotation.processor.meta.FieldMeta;

import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EntityClassMetadataParser implements ClassMetadataParser {

    private final Element element;
    private final String packageName;
    private final Types types;

    EntityClassMetadataParser(Element element, Elements elements, Types types) {
        this.element = element;
        this.packageName = elements.getPackageOf(element).getQualifiedName().toString();
        this.types = types;
    }

    @Override
    public String getPackage() {
        return packageName;
    }

    @Override
    public Collection<String> getImports() {
        return Collections.emptyList();
    }

    @Override
    public String getClassName() {
        var template = "{0}_";
        return MessageFormat.format(template, element.getSimpleName().toString());
    }

    @Override
    public List<FieldMeta> getFields() {
        List<FieldMeta> results = new LinkedList<>();
        extractFields(results, element);
        return results;
    }

    private void extractFields(List<FieldMeta> results, Element element) {
        if (element == null) {
            return;
        }
        if (!element.getKind().isInterface()) {
            List<? extends Element> fieldElements = extractFields(element);

            List<Element> combinedElements = new LinkedList<>(fieldElements);

            for (TypeMirror typeMirror : types.directSupertypes(element.asType())) {
                DeclaredType declaredType = (DeclaredType) typeMirror;
                Element superElement = declaredType.asElement();
                combinedElements.addAll(extractFields(superElement));
            }

            for (var fieldElement : combinedElements) {
                FieldMeta fieldMeta = new FieldMeta("String", fieldElement.getSimpleName());
                results.add(fieldMeta);
            }
        }
    }

    private List<? extends Element> extractFields(Element element) {
        return element.getEnclosedElements()
                .stream()
                .filter(ele -> ele.getKind().isField())
                .filter(this::isMetaModelField)
                .toList();
    }

    private boolean isMetaModelField(Element ele) {
        return ele.getAnnotation(Column.class) != null ||
                ele.getAnnotation(OneToMany.class) != null ||
                ele.getAnnotation(ManyToOne.class) != null ||
                ele.getAnnotation(ManyToMany.class) != null ||
                ele.getAnnotation(OneToOne.class) != null;
    }
}
