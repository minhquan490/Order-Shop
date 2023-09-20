package com.bachlinh.order.web.common.listener;

import com.bachlinh.order.core.concurrent.ThreadPoolManager;
import com.bachlinh.order.core.enums.ExecuteEvent;
import com.bachlinh.order.core.excecute.AbstractExecutor;
import com.bachlinh.order.core.excecute.BootWrapper;
import com.bachlinh.order.core.excecute.Executor;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.exception.system.common.CriticalException;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import com.bachlinh.order.security.helper.RequestAccessHistoriesHolder;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.UnsafeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public final class WebApplicationEventListener implements ApplicationListener<ApplicationEvent> {

    private static final String STARTED_EVENT = "org.springframework.context.event.ContextStartedEvent";
    private static final String READY_EVENT = "org.springframework.boot.context.event.ApplicationReadyEvent";
    private static final String REFRESH_EVENT = "org.springframework.context.event.ContextRefreshedEvent";
    private static final String CLOSE_EVENT = "org.springframework.context.event.ContextClosedEvent";
    private static final String STOP_EVENT = "org.springframework.context.event.ContextStoppedEvent";

    private final Collection<Executor<?>> eventExecutors = new LinkedList<>();
    private final DependenciesResolver resolver;
    private final Logger log;

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
        this.log = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        String eventName = event.getClass().getName();
        switch (eventName) {
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
            case STOP_EVENT -> {
                var repository = resolver.resolveDependencies(CustomerAccessHistoryRepository.class);
                var threadPoolManager = resolver.resolveDependencies(ThreadPoolManager.class);
                threadPoolManager.execute(() -> RequestAccessHistoriesHolder.flushAllHistories(repository));
            }
            case CLOSE_EVENT -> log.info("Close application");
            default -> {/* Do nothing */}
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
        AbstractExecutor<?> abstractExecutor;
        try {
            abstractExecutor = (AbstractExecutor<?>) UnsafeUtils.allocateInstance(initiator);
        } catch (InstantiationException e) {
            throw new CriticalException(e);
        }
        return abstractExecutor.newInstance(containerResolver, profile);
    }
}
