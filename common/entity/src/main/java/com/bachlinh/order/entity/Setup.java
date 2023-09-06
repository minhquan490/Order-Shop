package com.bachlinh.order.entity;

import com.bachlinh.order.service.container.ContainerWrapper;

/**
 * The sealed interface for setting everything before application is ready.
 * Setup execution will run in start up phase before application ready for handle request.
 *
 * @author Hoang Minh Quan
 */
public interface Setup {

    /**
     * Run operation before execute the setup.
     */
    void beforeExecute();

    /**
     * Run the setup.
     */
    void execute();

    /**
     * Run operation after setup execution completed.
     */
    void afterExecute();

    Setup newInstance(ContainerWrapper wrapper, String profile);
}
