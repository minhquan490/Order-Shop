package com.bachlinh.order.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.core.enums.ExecuteEvent;
import com.bachlinh.order.core.excecute.AbstractExecutor;
import com.bachlinh.order.entity.SetupManager;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.setup.internal.SetupManagerFactoryProvider;
import com.bachlinh.order.setup.spi.SetupManagerFactory;

/**
 * Executor for execute all setup available in classpath. This class is listener
 * listen whatever the spring container is refreshed. When container refresh, this executor
 * will run all setup. Beware when using setup module, time of start up phase will increase
 * when this module is used. To avoid long start up time when develop system, please use environment
 * setting properties for skip it and inject to your setup class.
 *
 * @author Hoang Minh Quan.
 */
@ActiveReflection
public class SetupExecutor extends AbstractExecutor<Void> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private SetupManager manager;
    private String profile;

    @ActiveReflection
    public SetupExecutor(DependenciesContainerResolver containerResolver, String profile) {
        super(containerResolver, profile);
        this.profile = profile;
    }

    private SetupManager buildSetupManager(ContainerWrapper context) {
        SetupManagerFactoryProvider provider = new SetupManagerFactoryProvider();
        SetupManagerFactory.Builder builder = provider.provideBuilder();
        SetupManagerFactory setupManagerFactory = builder.container(context).profile(profile).build();
        return setupManagerFactory.buildManager();
    }

    @Override
    protected void inject() {
        if (manager == null) {
            this.manager = buildSetupManager(ContainerWrapper.wrap(unwrapActualContainer()));
        }
    }

    @Override
    protected void doExecute(Void bootObject) {
        try {
            log.info("Start custom setup");
            manager.run();
            log.info("Finish setup");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Can not setup application", e);
        } finally {
            manager.close();
        }
    }

    @Override
    public ExecuteEvent runOn() {
        return ExecuteEvent.ON_REFRESH;
    }

    @Override
    public Class<Void> getBootObjectType() {
        return Void.class;
    }
}
