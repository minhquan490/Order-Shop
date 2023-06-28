package com.bachlinh.order.annotation.processor.writer;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.JavaFileObject;

import java.io.IOException;

public interface ClassWriter {
    void write(Element element) throws IOException;

    static ClassWriter entityProxyWriter(JavaFileObject source) {
        return new EntityProxyClassWriter(source);
    }

    static ClassWriter dtoProxyWriter(JavaFileObject source, Elements elements, TypeElement delegateType) {
        return new DtoProxyClassWriter(source, elements, delegateType);
    }
}
