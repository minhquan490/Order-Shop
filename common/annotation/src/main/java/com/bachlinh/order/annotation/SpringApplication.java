package com.bachlinh.order.annotation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.AliasFor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, WebMvcAutoConfiguration.class})
@EnableWebMvc
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableAsync(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
@EnableScheduling
@EnableCaching(proxyTargetClass = true, mode = AdviceMode.ASPECTJ)
public @interface SpringApplication {

    @AliasFor(annotation = SpringBootApplication.class)
    Class<?>[] exclude() default {DataSourceAutoConfiguration.class};

    @AliasFor(annotation = SpringBootApplication.class)
    String[] excludeName() default {};

    @AliasFor(annotation = SpringBootApplication.class)
    String[] scanBasePackages() default {"com.bachlinh.order.repository", "com.bachlinh.order.service"};

    @AliasFor(annotation = SpringBootApplication.class)
    boolean proxyBeanMethods() default true;
}
