package com.bachlinh.order.setup;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.SetupManager;
import com.bachlinh.order.service.container.ContainerWrapper;
import com.bachlinh.order.setup.internal.SetupManagerFactoryProvider;
import com.bachlinh.order.setup.spi.SetupManagerFactory;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;

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
public class SetupExecutor implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(SetupExecutor.class);
    private final SetupManager manager;

    @ActiveReflection
    public SetupExecutor(ContainerWrapper context) {
        this.manager = buildSetupManager(context);
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
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

    private SetupManager buildSetupManager(ContainerWrapper context) {
        SetupManagerFactoryProvider provider = new SetupManagerFactoryProvider();
        SetupManagerFactory.Builder builder = provider.provideBuilder();
        SetupManagerFactory setupManagerFactory = builder.container(context).build();
        return setupManagerFactory.buildManager();
    }
}
