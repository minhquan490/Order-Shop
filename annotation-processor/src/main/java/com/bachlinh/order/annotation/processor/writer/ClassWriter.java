package com.bachlinh.order.annotation.processor.writer;

import javax.lang.model.element.Element;
import javax.tools.JavaFileObject;

import java.io.IOException;

public interface ClassWriter {
    void write(Element element) throws IOException;

    static ClassWriter entityProxyWriter(JavaFileObject source) {
        return new EntityProxyClassWriter(source);
    }
}
