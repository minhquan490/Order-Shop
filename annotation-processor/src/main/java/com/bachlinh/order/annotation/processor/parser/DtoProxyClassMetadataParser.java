package com.bachlinh.order.annotation.processor.parser;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.bachlinh.order.annotation.processor.meta.FieldMeta;
import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DtoProxy;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class DtoProxyClassMetadataParser implements ClassMetadataParser {
    private final Element element;
    private final String packageName;
    private final TypeElement delegateType;

    DtoProxyClassMetadataParser(Element element, Elements elements, TypeElement delegateType) {
        this.element = element;
        this.packageName = elements.getPackageOf(element).getQualifiedName().toString();
        this.delegateType = delegateType;
    }

    @Override
    public String getPackage() {
        return packageName;
    }

    @Override
    public Collection<String> getImports() {
        var importCollection = new ArrayList<String>();
        importCollection.add(JsonInclude.Include.class.getName().replace("$", "."));
        importCollection.add(JsonInclude.class.getName());
        importCollection.add(JsonIgnore.class.getName());
        importCollection.add(JsonProperty.class.getName());
        importCollection.add(ActiveReflection.class.getName());
        importCollection.add(DtoProxy.class.getName());
        importCollection.add("com.bachlinh.order.dto.proxy.Proxy");
        importCollection.add(delegateType.getQualifiedName().toString());
        return importCollection;
    }

    @Override
    public String getClassName() {
        var template = "{0}.{1}{2}";
        return MessageFormat.format(template, getPackage(), element.getSimpleName().toString(), "Proxy");
    }
    
    @Override
    public List<FieldMeta> getFields() {
        return Collections.singletonList(new FieldMeta(this.delegateType.getSimpleName(), "delegate"));
    }
}
