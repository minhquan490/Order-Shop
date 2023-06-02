package com.bachlinh.order.web.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import com.bachlinh.order.core.enums.ExecuteEvent;
import com.bachlinh.order.core.excecute.BootWrapper;
import com.bachlinh.order.core.excecute.Executor;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

@Slf4j
public final class WebApplicationEventListener implements ApplicationListener<ApplicationEvent> {
    private static final String STARTED_EVENT = "org.springframework.context.event.ContextStartedEvent";
    private static final String READY_EVENT = "org.springframework.boot.context.event.ApplicationReadyEvent";
    private static final String REFRESH_EVENT = "org.springframework.context.event.ContextRefreshedEvent";

    private final Collection<Executor<?>> eventExecutors = new LinkedList<>();
    private final DependenciesResolver resolver;

    @SuppressWarnings("unchecked")
    public WebApplicationEventListener(DependenciesContainerResolver dependenciesContainerResolver, String profile) {
        ApplicationScanner scanner = new ApplicationScanner();
        eventExecutors.addAll(scanner.findComponents()
                .stream()
                .filter(Executor.class::isAssignableFrom)
                .map(clazz -> (Class<? extends Executor<?>>) clazz)
                .map(clazz -> instanceExecutor(clazz, dependenciesContainerResolver, profile))
                .filter(Objects::nonNull)
                .toList());
        this.resolver = dependenciesContainerResolver.getDependenciesResolver();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        switch (event.getClass().getName()) {
            case STARTED_EVENT -> {
                Collection<Executor<?>> startupExecutors = eventExecutors
                        .stream()
                        .filter(executor -> executor.runOn().equals(ExecuteEvent.ON_STARTUP))
                        .toList();
                onApplicationStartup(startupExecutors);
            }
            case READY_EVENT -> {
                Collection<Executor<?>> applicationReadyExecutors = eventExecutors
                        .stream()
                        .filter(executor -> executor.runOn().equals(ExecuteEvent.ON_READY))
                        .toList();
                onApplicationReady(applicationReadyExecutors);
            }
            case REFRESH_EVENT -> {
                Collection<Executor<?>> applicationRefreshExecutors = eventExecutors
                        .stream()
                        .filter(executor -> executor.runOn().equals(ExecuteEvent.ON_REFRESH))
                        .toList();
                onApplicationRefresh(applicationRefreshExecutors);
            }
            default -> {
                // Do nothing
            }
        }
    }

    private void onApplicationRefresh(Collection<Executor<?>> executors) {
        run(executors);
    }

    private void onApplicationStartup(Collection<Executor<?>> executors) {
        run(executors);
    }

    private void onApplicationReady(Collection<Executor<?>> executors) {
        run(executors);
    }

    private void run(Collection<Executor<?>> executors) {
        executors.forEach(executor -> {
            Class<?> bootType = executor.getBootObjectType();
            if (bootType.equals(Void.class)) {
                executor.execute(new BootWrapper<>(null));
            } else {
                Object param = resolver.resolveDependencies(bootType);
                executor.execute(new BootWrapper<>(param));
            }
        });
    }

    private Executor<?> instanceExecutor(Class<? extends Executor<?>> initiator, DependenciesContainerResolver containerResolver, String profile) {
        try {
            Constructor<?> constructor = initiator.getConstructor(DependenciesContainerResolver.class, String.class);
            Executor<?> executor = (Executor<?>) constructor.newInstance(containerResolver, profile);
            if (log.isDebugEnabled()) {
                log.debug("Init event executor [{}]", initiator.getName());
            }
            return executor;
        } catch (Exception e) {
            return null;
        }
    }
}
