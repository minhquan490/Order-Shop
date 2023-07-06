package com.bachlinh.order.aot;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.ReflectionHints;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.lang.Nullable;

public class WebsocketRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, @Nullable ClassLoader classLoader) {
        ReflectionHints reflectionHints = hints.reflection();
        registerType(reflectionHints, "org.springframework.web.socket.server.jetty.JettyRequestUpgradeStrategy");
    }

    private void registerType(ReflectionHints reflectionHints, String className) {
        reflectionHints.registerType(TypeReference.of(className),
                builder -> builder.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS));
    }
}
