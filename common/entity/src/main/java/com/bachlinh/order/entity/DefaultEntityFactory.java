package com.bachlinh.order.entity;

import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.entity.context.internal.DefaultEntityContext;
import com.bachlinh.order.entity.context.spi.EntityContext;
import com.bachlinh.order.entity.index.internal.InternalProvider;
import com.bachlinh.order.entity.index.spi.SearchManager;
import com.bachlinh.order.entity.index.spi.SearchManagerFactory;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.transaction.internal.DefaultTransactionManager;
import com.bachlinh.order.entity.transaction.spi.EntityTransactionManager;
import com.bachlinh.order.environment.Environment;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public final class DefaultEntityFactory implements EntityFactory {

    private final Map<Class<?>, EntityContext> entityContext;
    private final ApplicationContext applicationContext;
    private final EntityTransactionManager transactionManager;
    private final String activeProfile;

    private DefaultEntityFactory(Map<Class<?>, EntityContext> entityContext, ApplicationContext applicationContext, String activeProfile) {
        this.entityContext = entityContext;
        this.applicationContext = applicationContext;
        this.transactionManager = new DefaultTransactionManager();
        this.activeProfile = activeProfile;
    }

    @Override
    public <T> boolean containsEntity(Class<T> entityType) {
        return entityContext.containsKey(entityType);
    }

    @Override
    public <T> T getEntity(Class<T> entityType) {
        if (containsEntity(entityType)) {
            return entityType.cast(entityContext.get(entityType).getEntity());
        }
        return null;
    }

    @Override
    public EntityContext getEntityContext(Class<?> entityType) {
        return entityContext.get(entityType);
    }

    @Override
    public EntityTransactionManager getTransactionManager() {
        return transactionManager;
    }

    public static class DefaultEntityFactoryBuilder implements EntityFactoryBuilder {
        private ApplicationContext applicationContext;
        private String activeProfile;

        @Override
        public EntityFactoryBuilder applicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
            return this;
        }

        @Override
        public EntityFactoryBuilder profile(String profile) {
            this.activeProfile = profile;
            return this;
        }

        @Override
        public EntityFactory build() throws IOException {
            Map<Class<?>, EntityContext> entityContext = new HashMap<>();
            Collection<Class<?>> entities = new ApplicationScanner()
                    .findComponents()
                    .stream()
                    .filter(BaseEntity.class::isAssignableFrom)
                    .toList();
            SearchManager manager = buildSearchManager(entities, applicationContext);
            entities.forEach(entity -> entityContext.put(entity, new DefaultEntityContext(entity, applicationContext, manager)));
            return new DefaultEntityFactory(entityContext, applicationContext, activeProfile);
        }

        private SearchManager buildSearchManager(Collection<Class<?>> entities, ApplicationContext applicationContext) throws IOException {
            Environment environment = Environment.getInstance(activeProfile);
            SearchManagerFactory.Builder builder = InternalProvider.useDefaultSearchManagerFactoryBuilder();
            builder.indexFilePath(environment.getProperty("server.index.path"));
            builder.stopWordFilePath(environment.getProperty("server.stop-word.path"));
            List<Class<?>> entitiesForIndex = entities.stream()
                    .filter(entity -> entity.isAnnotationPresent(EnableFullTextSearch.class))
                    .toList();
            builder.entities(entitiesForIndex.toArray(new Class[0]));
            builder.indexNames(entitiesForIndex.stream()
                    .map(clazz -> clazz.getSimpleName().toLowerCase())
                    .toList()
                    .toArray(new String[0]));
            builder.threadPool(applicationContext.getBean(ThreadPoolTaskExecutor.class));
            return builder.build().obtainManager();
        }
    }
}
