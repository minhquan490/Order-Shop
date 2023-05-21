package com.bachlinh.order.setup.spi;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.entity.Setup;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.service.container.DependenciesResolver;

/**
 * Base setup of all setup object. Extends this object for define setup object.
 *
 * @author Hoang Minh Quan
 */
public abstract class AbstractSetup implements Setup {
    private final DependenciesContainerResolver containerResolver;
    private final Environment environment;

    protected AbstractSetup(ContainerWrapper wrapper, String profile) {
        this.containerResolver = DependenciesContainerResolver.buildResolver(wrapper.unwrap(), profile);
        this.environment = Environment.getInstance(profile);
    }

    protected DependenciesResolver getDependenciesResolver() {
        return containerResolver.getDependenciesResolver();
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected abstract void inject();

    protected abstract void doBefore();

    protected abstract void doExecute();

    protected abstract void doAfter();

    @Override
    public final void beforeExecute() {
        inject();
        doBefore();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public final void execute() {
        doExecute();
    }

    @Override
    public final void afterExecute() {
        doAfter();
    }
}
