package com.bachlinh.order.handler.service;

import com.bachlinh.order.annotation.ServiceComponent;
import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.exception.system.common.CriticalException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultServiceManager implements ServiceManager {

    private final DependenciesResolver resolver;
    private final Environment environment;
    private final Map<Class<?>, AbstractService> serviceMap = new HashMap<>();

    public DefaultServiceManager(DependenciesResolver resolver, Environment environment) {
        this.resolver = resolver;
        this.environment = environment;
        initServices();
    }

    @Override
    public boolean isServiceAvailable(Class<?> serviceType) {
        return serviceMap.containsKey(serviceType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceType) {
        if (!isServiceAvailable(serviceType)) {
            throw new CriticalException("No service available for type [" + serviceType.getName() + "]");
        }
        return (T) serviceMap.get(serviceType);
    }

    private void initServices() {
        ApplicationScanner scanner = new ApplicationScanner();
        Initializer<AbstractService> initializer = new ServiceInitializer();
        List<AbstractService> services = scanner.findComponents()
                .stream()
                .filter(ServiceBase.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(ServiceComponent.class))
                .map(serviceType -> initializer.getObject(serviceType, resolver, environment))
                .toList();
        for (AbstractService service : services) {
            computedService(service);
        }
    }

    private void computedService(AbstractService service) {
        boolean contained = Arrays.stream(service.getServiceTypes()).anyMatch(serviceMap::containsKey);
        if (contained) {
            throw new CriticalException("One interface must have only one implement class");
        }
        for (Class<?> key : service.getServiceTypes()) {
            serviceMap.put(key, service);
        }
    }
}
