package com.bachlinh.order.entity.setup.internal;

import com.bachlinh.order.entity.setup.spi.SetupManager;
import com.bachlinh.order.entity.setup.spi.SetupManagerFactory;
import org.springframework.context.ApplicationContext;

class DefaultSetupManagerFactory implements SetupManagerFactory {
    private final ApplicationContext applicationContext;

    DefaultSetupManagerFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public SetupManager buildManager() {
        return new DefaultSetupManager(applicationContext);
    }
}
