package com.bachlinh.order.annotation;

import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.core.annotation.AliasFor;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootApplication
@EnableWebSecurity
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ImportRuntimeHints(value = {})
@Import(value = {})
public @interface NettyApplication {

    @AliasFor(annotation = SpringBootApplication.class)
    Class<?>[] exclude() default {DataSourceAutoConfiguration.class, WebMvcAutoConfiguration.class, MultipartAutoConfiguration.class, ErrorMvcAutoConfiguration.class, ThymeleafAutoConfiguration.class};

    @AliasFor(annotation = SpringBootApplication.class)
    String[] excludeName() default {};

    @AliasFor(annotation = SpringBootApplication.class)
    String[] scanBasePackages() default {};

    @AliasFor(annotation = SpringBootApplication.class)
    boolean proxyBeanMethods() default true;

    @AliasFor(annotation = ImportRuntimeHints.class, attribute = "value")
    Class<? extends RuntimeHintsRegistrar>[] runtimeHints() default {};

    @AliasFor(annotation = Import.class, attribute = "value")
    Class<?>[] beanClasses() default {};
}
