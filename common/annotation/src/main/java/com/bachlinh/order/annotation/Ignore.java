package com.bachlinh.order.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Ignore metadata, the scanner will skip class annotated by this annotation.
 *
 * @author Hoang Minh Quan
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Ignore {
}
