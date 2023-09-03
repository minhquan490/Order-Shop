package com.bachlinh.order.entity.context;

import java.util.function.Supplier;

public record FieldUpdated(String fieldName, Object oldValue, Supplier<Object> newValue) {
}
