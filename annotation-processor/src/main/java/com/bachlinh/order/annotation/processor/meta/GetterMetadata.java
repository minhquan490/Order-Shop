package com.bachlinh.order.annotation.processor.meta;

import javax.lang.model.type.TypeMirror;
import com.bachlinh.order.annotation.MappedDtoField;

public record GetterMetadata(String methodName,
                             TypeMirror returnType,
                             MappedDtoField mappedDtoField) {
}