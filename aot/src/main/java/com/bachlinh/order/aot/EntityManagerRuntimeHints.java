package com.bachlinh.order.aot;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.util.ClassUtils;

public class EntityManagerRuntimeHints implements RuntimeHintsRegistrar {

    private static final String HIBERNATE_SESSION_FACTORY_CLASS_NAME = "org.hibernate.SessionFactory";

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        if (ClassUtils.isPresent(HIBERNATE_SESSION_FACTORY_CLASS_NAME, classLoader)) {
            hints.proxies().registerJdkProxy(TypeReference.of(HIBERNATE_SESSION_FACTORY_CLASS_NAME),
                    TypeReference.of(EntityManagerFactoryInfo.class));
            hints.proxies().registerJdkProxy(TypeReference.of("org.hibernate.Session"),
                    TypeReference.of(EntityManagerProxy.class));
        }
    }
}
