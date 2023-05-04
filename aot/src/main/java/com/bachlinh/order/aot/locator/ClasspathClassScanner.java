package com.bachlinh.order.aot.locator;

import org.springframework.lang.NonNull;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Native;
import com.bachlinh.order.annotation.Reachable;
import com.bachlinh.order.core.scanner.ApplicationScanner;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

class ClasspathClassScanner implements Scanner<Collection<Class<?>>> {
    private final ApplicationScanner applicationScanner = new ApplicationScanner();

    @Override
    @NonNull
    public ScanResult<Collection<Class<?>>> scan(Collection<String> packages) {
        Set<Class<?>> resultSet = applicationScanner.findComponents()
                .stream()
                .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                .filter(clazz -> !Modifier.isInterface(clazz.getModifiers()))
                .filter(clazz -> !clazz.isAnnotation())
                .filter(this::shouldRegister)
                .collect(Collectors.toSet());
        return new ScanResult<>(resultSet);
    }

    private boolean shouldRegister(Class<?> clazz) {
        return clazz.isAnnotationPresent(ActiveReflection.class) ||
                clazz.isAnnotationPresent(Native.class) ||
                clazz.isAnnotationPresent(Reachable.class) ||
                Serializable.class.isAssignableFrom(clazz);
    }
}

