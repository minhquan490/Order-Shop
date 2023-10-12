package com.bachlinh.order.setup.internal;

import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.setup.SetupManager;
import com.bachlinh.order.setup.spi.SetupManagerFactory;

class DefaultSetupManagerFactory implements SetupManagerFactory {
    private final ContainerWrapper wrapper;
    private final String profile;

    DefaultSetupManagerFactory(ContainerWrapper wrapper, String profile) {
        this.wrapper = wrapper;
        this.profile = profile;
    }

    @Override
    public SetupManager buildManager() {
        return new DefaultSetupManager(wrapper, profile);
    }
}
