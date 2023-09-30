package com.bachlinh.order.handler.service;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.core.environment.Environment;

public sealed interface ServiceBase permits AbstractService {
    DependenciesResolver getResolver();

    Environment getEnvironment();

    ServiceBase getInstance(DependenciesResolver resolver, Environment environment);

    Class<?>[] getServiceTypes();
}
