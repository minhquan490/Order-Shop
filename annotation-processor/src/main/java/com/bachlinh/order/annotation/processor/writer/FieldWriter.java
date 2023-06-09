package com.bachlinh.order.annotation.processor.writer;

import com.bachlinh.order.annotation.processor.meta.FieldMeta;

import java.io.IOException;

public interface FieldWriter {
    void write(FieldMeta meta) throws IOException;
}
