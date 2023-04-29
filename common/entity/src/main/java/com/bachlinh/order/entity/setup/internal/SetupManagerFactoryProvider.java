package com.bachlinh.order.entity.setup.internal;

import com.bachlinh.order.entity.setup.spi.SetupManagerFactory;
import org.springframework.context.ApplicationContext;

public final class SetupManagerFactoryProvider {
    public SetupManagerFactory.Builder provideBuilder() {
        return new SetupManagerFactoryBuilderImplementer();
    }

    private static class SetupManagerFactoryBuilderImplementer implements SetupManagerFactory.Builder {
        private ApplicationContext context;

        @Override
        public SetupManagerFactory.Builder applicationContext(ApplicationContext context) {
            this.context = context;
            return this;
        }

        @Override
        public SetupManagerFactory build() {
            return new DefaultSetupManagerFactory(context);
        }
    }
}
