package com.bachlinh.order.core.container;

import com.bachlinh.order.exception.system.common.CriticalException;
import com.google.common.base.Objects;
import org.springframework.context.ApplicationContext;

class SpringDependenciesContainerResolver implements DependenciesContainerResolver {
    private static SpringDependenciesContainerResolver singleton;

    private final SpringDependenciesResolver resolver;

    private SpringDependenciesContainerResolver(Object container) {
        this.resolver = new SpringDependenciesResolver(container);
    }

    @Override
    public String getContainerClassName() {
        return resolver.getContainerName();
    }

    @Override
    public Class<?> getContainerType() {
        return resolver.getContainerType();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T resolveContainer() {
        return (T) resolver.unwrap();
    }

    @Override
    public DependenciesResolver getDependenciesResolver() {
        return resolver;
    }

    public static SpringDependenciesContainerResolver getInstance(Object container) {
        if (singleton == null) {
            singleton = new SpringDependenciesContainerResolver(container);
        }
        return singleton;
    }

    private static class SpringDependenciesResolver implements DependenciesResolver {
        private final ApplicationContext context;

        SpringDependenciesResolver(Object container) {
            if (container instanceof ApplicationContext c) {
                this.context = c;
            } else {
                throw new CriticalException("Only spring context is allowed");
            }
        }

        @Override
        public <T> T resolveDependencies(Class<T> type) {
            return context.getBean(type);
        }

        @Override
        public <T> T resolveDependencies(String name, Class<T> requireType) {
            return context.getBean(name, requireType);
        }

        @Override
        public Object resolveDependencies(String name) {
            return context.getBean(name);
        }

        String getContainerName() {
            return getContainerType().getName();
        }

        Class<?> getContainerType() {
            return context.getClass();
        }

        ApplicationContext unwrap() {
            return context;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpringDependenciesResolver that = (SpringDependenciesResolver) o;
            return Objects.equal(context, that.context);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(context);
        }
    }
}
