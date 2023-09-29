package com.bachlinh.order.handler.service;

public interface ServiceManager {

    boolean isServiceAvailable(Class<?> serviceType);

    <T> T getService(Class<T> serviceType);
}
