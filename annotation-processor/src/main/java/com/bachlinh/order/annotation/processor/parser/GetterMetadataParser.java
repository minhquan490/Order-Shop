package com.bachlinh.order.annotation.processor.parser;

import javax.lang.model.element.Element;
import com.bachlinh.order.annotation.processor.meta.GetterMetadata;

import java.util.List;

public interface GetterMetadataParser {
    List<GetterMetadata> parse(String pattern, Element parent);

    static GetterMetadataParser dtoGetterParser() {
        return new DtoGetterMetadataParser();
    }
}
