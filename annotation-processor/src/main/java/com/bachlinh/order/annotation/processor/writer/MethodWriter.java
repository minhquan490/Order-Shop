package com.bachlinh.order.annotation.processor.writer;

import javax.lang.model.element.Element;

import java.io.IOException;

public interface MethodWriter {
    void write(Element element) throws IOException;
}
