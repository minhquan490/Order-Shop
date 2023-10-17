package com.bachlinh.order.web.common.entity;

import jakarta.persistence.Table;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.context.EntityIdProvider;
import com.bachlinh.order.repository.RepositoryManager;
import com.bachlinh.order.web.repository.spi.AbstractRepository;

public class DefaultEntityIdProvider implements EntityIdProvider {

    private final DependenciesResolver resolver;
    private final Class<?> entityType;
    private final Class<?> idType;

    public DefaultEntityIdProvider(DependenciesResolver resolver, Class<?> entityType, Class<?> idType) {
        this.resolver = resolver;
        this.entityType = entityType;
        this.idType = idType;
    }
    @Override
    public int getLastId() throws ClassNotFoundException {
        String packageName = AbstractRepository.class.getPackageName();
        String repositoryPattern = packageName + ".{0}Repository";
        String repositoryName = MessageFormat.format(repositoryPattern, entityType.getSimpleName());
        Class<?> repositoryClass = Class.forName(repositoryName);
        RepositoryManager repositoryManager = resolver.resolveDependencies(RepositoryManager.class);
        AbstractRepository<?, ?> repository = (AbstractRepository<?, ?>) repositoryManager.getRepository(repositoryClass);
        String sql = MessageFormat.format("SELECT MAX(ID) FROM {0}", entityType.getAnnotation(Table.class).name());
        List<?> result = repository.getResultList(sql, Collections.emptyMap(), idType);
        if (result.isEmpty()) {
            return 0;
        }
        if (result.get(0) instanceof String idString) {
            String suffixId = idString.split("-")[1];
            return Integer.parseInt(suffixId);
        }
        if (result.get(0) instanceof Integer idInt) {
            return idInt;
        }
        return 0;
    }
}
