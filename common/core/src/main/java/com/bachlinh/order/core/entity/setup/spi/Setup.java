package com.bachlinh.order.core.entity.setup.spi;

/**
 * The sealed interface for setting everything before application is ready.
 * Setup execution will run in start up phase before application ready for handle request.
 *
 * @author Hoang Minh Quan
 */
public sealed interface Setup permits AbstractSetup {

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
}
