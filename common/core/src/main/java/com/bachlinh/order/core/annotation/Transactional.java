package com.bachlinh.order.core.annotation;

import com.bachlinh.order.core.enums.Isolation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Transactional {
    Isolation isolation() default Isolation.DEFAULT;

    int timeOut() default -1;
}
