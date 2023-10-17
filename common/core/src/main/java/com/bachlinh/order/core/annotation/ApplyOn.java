package com.bachlinh.order.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ApplyOn {

    Class<?> entity() default Void.class;

    int order() default Integer.MAX_VALUE;
}
