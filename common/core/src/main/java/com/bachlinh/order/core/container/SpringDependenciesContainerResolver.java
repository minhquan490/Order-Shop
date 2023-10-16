package com.bachlinh.order.core.container;

import com.bachlinh.order.core.exception.system.common.CriticalException;

import com.google.common.base.Objects;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

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

    private static class SpringDependenciesResolver extends AbstractDependenciesResolver {
        private final ApplicationContext context;

        SpringDependenciesResolver(Object container) {
            if (container instanceof ApplicationContext c) {
                this.context = c;
            } else {
                throw new CriticalException("Only spring context is allowed");
            }
            selfAssign(this);
        }

        @Override
        public <T> T resolveDependencies(Class<T> type) {
            Object bean = queryBean(type);

            if (isSingleton(type) && bean == null) {
                bean = context.getBean(type);
                cacheBean(type, bean);
            }

            return type.cast(bean);
        }

        @Override
        public <T> T resolveDependencies(String name, Class<T> requireType) {
            Object bean = resolveDependencies(name);
            return requireType.cast(bean);
        }

        @Override
        public Object resolveDependencies(String name) {
            Object bean = queryBean(name);

            if (isSingleton(name) && bean == null) {
                bean = context.getBean(name);
                cacheBean(name, bean);
            }

            return bean;
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

        String getContainerName() {
            return getContainerType().getName();
        }

        Class<?> getContainerType() {
            return context.getClass();
        }

        ApplicationContext unwrap() {
            return context;
        }

        private boolean isSingleton(String beanName) {
            return context.isSingleton(beanName);
        }

        private boolean isSingleton(Class<?> beanType) {
            String[] names = context.getBeanNamesForType(beanType);

            if (names.length == 0) {
                return false;
            }

            return isSingleton(names[0]);
        }

        @Override
        protected void doClose() {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) context;
            if (configurableApplicationContext.isActive()) {
                configurableApplicationContext.close();
            }
        }
    }
}
