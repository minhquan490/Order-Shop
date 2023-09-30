package com.bachlinh.order.annotation.processor.meta;

import com.bachlinh.order.core.annotation.MappedDtoField;

import javax.lang.model.type.TypeMirror;

public record GetterMetadata(String methodName,
                             TypeMirror returnType,
                             MappedDtoField mappedDtoField) {
}