package com.bachlinh.order.core.entity.setup;

import com.bachlinh.order.core.entity.setup.internal.SetupManagerFactoryProvider;
import com.bachlinh.order.core.entity.setup.spi.SetupManager;
import com.bachlinh.order.core.entity.setup.spi.SetupManagerFactory;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
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
@Log4j2
public class SetupExecutor implements ApplicationListener<ContextRefreshedEvent> {
    private final SetupManager manager;

    public SetupExecutor(ApplicationContext context) {
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

    private SetupManager buildSetupManager(ApplicationContext context) {
        SetupManagerFactoryProvider provider = new SetupManagerFactoryProvider();
        SetupManagerFactory.Builder builder = provider.provideBuilder();
        SetupManagerFactory setupManagerFactory = builder.applicationContext(context).build();
        return setupManagerFactory.buildManager();
    }
}
