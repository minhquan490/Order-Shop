package com.bachlinh.order.service.container;

import com.bachlinh.order.exception.system.common.CriticalException;
import org.springframework.context.ApplicationContext;

import java.util.Objects;

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

        public boolean equals(final Object o) {
            if (o == this) return true;
            if (!(o instanceof SpringDependenciesResolver other)) return false;
            if (!other.canEqual(this)) return false;
            final Object this$context = this.context;
            final Object other$context = other.context;
            return Objects.equals(this$context, other$context);
        }

        protected boolean canEqual(final Object other) {
            return other instanceof SpringDependenciesResolver;
        }

        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $context = this.context;
            result = result * PRIME + $context.hashCode();
            return result;
        }
    }
}
