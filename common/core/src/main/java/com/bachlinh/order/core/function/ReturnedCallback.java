package com.bachlinh.order.core.function;

@FunctionalInterface
public interface ReturnedCallback {
    Object execute(Object... params);
}
