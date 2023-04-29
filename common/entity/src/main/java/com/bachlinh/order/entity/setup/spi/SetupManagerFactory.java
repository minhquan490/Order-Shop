package com.bachlinh.order.entity.setup.spi;

import org.springframework.context.ApplicationContext;

/**
 * The factory for create {@link SetupManager} with given options obtain from the builder.
 *
 * @author Hoang Minh Quan.
 */
public interface SetupManagerFactory {

    /**
     * Build the manager with options obtain from the builder.
     *
     * @return Built {@code SetupManager}.
     */
    SetupManager buildManager();

    /**
     * The builder use for build {@link SetupManager}
     */
    interface Builder {

        /**
         * Setting up the {@link ApplicationContext} for build {@code SetupManagerFactory}, must be not null.
         */
        Builder applicationContext(ApplicationContext context);

        SetupManagerFactory build();
    }
}
