package com.bachlinh.order.aot;

import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeHint;
import org.springframework.aot.hint.TypeReference;
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy;

import java.util.function.Consumer;

public class HibernateRuntimeHints implements RuntimeHintsRegistrar {

    private static final String[] NO_JTA_PLATFORM_CLASSES = {
            "org.hibernate.engine.transaction.jta.platform.internal.NoJtaPlatform",
            "org.hibernate.service.jta.platform.internal.NoJtaPlatform"};

    private static final Consumer<TypeHint.Builder> INVOKE_DECLARED_CONSTRUCTORS = TypeHint
            .builtWith(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        for (String noJtaPlatformClass : NO_JTA_PLATFORM_CLASSES) {
            hints.reflection().registerType(TypeReference.of(noJtaPlatformClass), INVOKE_DECLARED_CONSTRUCTORS);
        }
        hints.reflection().registerType(SpringImplicitNamingStrategy.class, INVOKE_DECLARED_CONSTRUCTORS);
        hints.reflection().registerType(CamelCaseToUnderscoresNamingStrategy.class, INVOKE_DECLARED_CONSTRUCTORS);
    }

}