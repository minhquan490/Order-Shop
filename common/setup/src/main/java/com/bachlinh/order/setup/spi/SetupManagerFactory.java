package com.bachlinh.order.setup.spi;

import com.bachlinh.order.entity.SetupManager;
import com.bachlinh.order.core.container.ContainerWrapper;

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

        Builder container(ContainerWrapper wrapper);

        Builder profile(String profile);

        SetupManagerFactory build();
    }
}
