package com.bachlinh.order.core.http.handler;

public interface ThrowableHandler<T extends Throwable, U> {
    U handle(T throwable);

    Class<T>[] activeOnTypes();

    boolean isErrorHandler();
}
