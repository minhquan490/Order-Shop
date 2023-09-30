package com.bachlinh.order.setup.spi;

import com.bachlinh.order.core.container.ContainerWrapper;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.Setup;
import com.bachlinh.order.entity.repository.RepositoryManager;
import com.bachlinh.order.core.environment.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base setup of all setup object. Extends this object for define setup object.
 *
 * @author Hoang Minh Quan
 */
public abstract class AbstractSetup implements Setup {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DependenciesContainerResolver containerResolver;
    private final Environment environment;
    private final RepositoryManager repositoryManager;

    protected AbstractSetup(ContainerWrapper wrapper, String profile) {
        this.containerResolver = DependenciesContainerResolver.buildResolver(wrapper.unwrap(), profile);
        this.environment = Environment.getInstance(profile);
        this.repositoryManager = resolveDependencies(RepositoryManager.class);
    }

    protected DependenciesResolver getDependenciesResolver() {
        return containerResolver.getDependenciesResolver();
    }

    protected Environment getEnvironment() {
        return environment;
    }

    protected <T> T resolveDependencies(Class<T> dependenciesType) {
        return getDependenciesResolver().resolveDependencies(dependenciesType);
    }

    protected <T> T resolveRepository(Class<T> repositoryType) {
        return this.repositoryManager.getRepository(repositoryType);
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
    public final void execute() {
        try {
            doExecute();
        } catch (Exception e) {
            logger.error("Problem when process setup [{}]", this.getClass(), e);
        }
    }

    @Override
    public final void afterExecute() {
        doAfter();
    }
}
