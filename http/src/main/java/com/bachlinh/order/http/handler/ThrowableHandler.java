package com.bachlinh.order.http.handler;

public interface ThrowableHandler<T extends Throwable, U> {
    U handle(T throwable);

    Class<T>[] activeOnTypes();

    boolean isErrorHandler();

    ThrowableHandler<T, U> newInstance();
}
