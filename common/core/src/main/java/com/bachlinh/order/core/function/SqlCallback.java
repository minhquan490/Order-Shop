package com.bachlinh.order.core.function;

@FunctionalInterface
public interface SqlCallback {
    String apply(Object param0, Object param1, Object param2);
}
