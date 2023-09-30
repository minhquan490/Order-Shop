package com.bachlinh.order.core.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@RestController
@RequestMapping
public @interface HttpHandler {
    @AliasFor(annotation = RestController.class)
    String value() default "";

    @AliasFor(annotation = RequestMapping.class, attribute = "value")
    String[] paths() default {};
}
