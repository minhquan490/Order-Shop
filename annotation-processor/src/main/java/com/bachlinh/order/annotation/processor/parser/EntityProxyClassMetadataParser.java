package com.bachlinh.order.annotation.processor.parser;

import com.bachlinh.order.annotation.processor.meta.FieldMeta;
import com.bachlinh.order.core.annotation.ActiveReflection;

import javax.lang.model.element.Element;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class EntityProxyClassMetadataParser implements ClassMetadataParser {
    private final Element classElement;
    private String cachedPackage;
    private Collection<String> cachedImport;
    private String cachedClassName;
    private FieldMeta cachedField;

    EntityProxyClassMetadataParser(Element classElement) {
        this.classElement = classElement;
    }

    @Override
    public String getPackage() {
        if (cachedPackage == null) {
            cachedPackage = "com.bachlinh.order.entity.proxy";
        }
        return cachedPackage;
    }

    @Override
    public Collection<String> getImports() {
        if (cachedImport == null || cachedImport.isEmpty()) {
            var type = this.classElement.asType();
            cachedImport = new LinkedList<>();
            cachedImport.add(ActiveReflection.class.getName());
            cachedImport.add(type.toString());
            cachedImport.add("com.bachlinh.order.entity.proxy.AbstractEntityProxy");
            cachedImport.add(Map.class.getName());
            cachedImport.add(HashMap.class.getName());
        }
        return cachedImport;
    }

    @Override
    public String getClassName() {
        if (cachedClassName == null) {
            var template = "{0}.{1}{2}";
            var entityClassName = this.classElement.getSimpleName();
            cachedClassName = MessageFormat.format(template, getPackage(), entityClassName, "Proxy");
        }
        return cachedClassName;
    }

    @Override
    public List<FieldMeta> getFields() {
        if (cachedField == null) {
            cachedField = new FieldMeta(this.classElement.getSimpleName(), "delegate");
        }
        return Collections.singletonList(cachedField);
    }
}
