package com.bachlinh.order.entity.setup.spi;

import org.springframework.context.ApplicationContext;

/**
 * Base setup of all setup object. Extends this object for define setup object.
 *
 * @author Hoang Minh Quan
 */
public abstract non-sealed class AbstractSetup implements Setup {
    private ApplicationContext applicationContext;

    protected AbstractSetup(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
