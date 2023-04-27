package com.bachlinh.order.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for search engine know field should be indexed
 *
 * @author Hoang Minh Quan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({})
public @interface FullTextField {

    /**
     * Field name of entity
     */
    String value();
}
