package com.bachlinh.order.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation use on method for pre-validate before saving entity
 *
 * @author Hoang Minh Quan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Validated {

    /**
     * Position of target in method parameter begins at 0
     */
    int targetIndex();
}