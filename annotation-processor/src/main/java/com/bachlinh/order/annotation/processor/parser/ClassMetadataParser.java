package com.bachlinh.order.annotation.processor.parser;

import javax.lang.model.element.Element;
import com.bachlinh.order.annotation.processor.meta.FieldMeta;

import java.util.Collection;

public interface ClassMetadataParser {
    String getPackage();

    Collection<String> getImports();

    String getClassName();

    FieldMeta getEntityField();

    static ClassMetadataParser entityParser(Element entity) {
        return new EntityProxyClassMetadataParser(entity);
    }
}
