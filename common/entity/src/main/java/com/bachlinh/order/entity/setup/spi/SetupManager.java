package com.bachlinh.order.entity.setup.spi;

import java.util.Collection;

/**
 * The manager for managing all setup will be run.
 *
 * @author Hoang Minh Quan.
 */
public interface SetupManager {

    /**
     * Return true if this manager was closed.
     */
    boolean isClose();

    /**
     * Load all setup available in classpath. Throw {@link ClassNotFoundException} if
     * setup class can not be loaded or not available, this should be never cause.
     *
     * @return Collection contains all setup available.
     */
    Collection<Setup> loadSetup() throws ClassNotFoundException;

    /**
     * Run all setup available in classpath.
     *
     * @throws ClassCastException If setup class can not be loaded or not available,
     *                            this should be never cause.
     */
    void run() throws ClassNotFoundException;

    /**
     * Close the manager and release all resource associate with it.
     */
    void close();
}
