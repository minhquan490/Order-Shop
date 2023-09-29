package com.bachlinh.order.setup.internal;

import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.setup.spi.SetupManagerFactory;

import java.util.Objects;

public final class SetupManagerFactoryProvider {
    public SetupManagerFactory.Builder provideBuilder() {
        return new SetupManagerFactoryBuilderImplementer();
    }

    private static class SetupManagerFactoryBuilderImplementer implements SetupManagerFactory.Builder {
        private ContainerWrapper wrapper;
        private String profile;

        @Override
        public SetupManagerFactory.Builder container(ContainerWrapper wrapper) {
            this.wrapper = wrapper;
            return this;
        }

        @Override
        public SetupManagerFactory.Builder profile(String profile) {
            this.profile = profile;
            return this;
        }

        @Override
        public SetupManagerFactory build() {
            Objects.requireNonNull(wrapper, "Container wrapper must not be null");
            Objects.requireNonNull(profile, "Profile must not be null");
            return new DefaultSetupManagerFactory(wrapper, profile);
        }
    }
}
