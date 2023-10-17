package com.bachlinh.order.annotation.processor.parser;

import com.bachlinh.order.annotation.processor.meta.FieldMeta;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.List;

public interface ClassMetadataParser {
    String getPackage();

    Collection<String> getImports();

    String getClassName();

    List<FieldMeta> getFields();

    static ClassMetadataParser entityParser(Element entity) {
        return new EntityProxyClassMetadataParser(entity);
    }

    static ClassMetadataParser dtoParser(Element dto, Elements elements, TypeElement delegateType) {
        return new DtoProxyClassMetadataParser(dto, elements, delegateType);
    }

    static ClassMetadataParser entityMetaModelParser(Element entity, Elements elements, Types types) {
        return new EntityClassMetadataParser(entity, elements, types);
    }
}
