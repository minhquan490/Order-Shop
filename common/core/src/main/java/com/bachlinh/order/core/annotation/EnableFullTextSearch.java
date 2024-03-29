package com.bachlinh.order.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply full text search feature on specify entity
 *
 * @author Hoang Minh Quan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableFullTextSearch {
    String proxyPackage() default "com.bachlinh.order.entity.proxy";
}
