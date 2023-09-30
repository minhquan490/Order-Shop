package com.bachlinh.order.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Scope {

    ControllerScope value() default ControllerScope.SINGLETON;

    public enum ControllerScope {
        SINGLETON,
        REQUEST
    }
}
