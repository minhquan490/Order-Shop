package com.bachlinh.order.annotation.processor.parser;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import com.bachlinh.order.annotation.processor.meta.FieldMeta;

import java.util.Collection;

public interface ClassMetadataParser {
    String getPackage();

    Collection<String> getImports();

    String getClassName();

    FieldMeta getField();

    static ClassMetadataParser entityParser(Element entity) {
        return new EntityProxyClassMetadataParser(entity);
    }

    static ClassMetadataParser dtoParser(Element dto, Elements elements, TypeElement delegateType) {
        return new DtoProxyClassMetadataParser(dto, elements, delegateType);
    }
}
