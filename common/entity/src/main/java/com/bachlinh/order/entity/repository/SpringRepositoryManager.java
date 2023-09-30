package com.bachlinh.order.entity.repository;

import com.bachlinh.order.core.alloc.Initializer;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.exception.system.common.CriticalException;
import com.bachlinh.order.core.scanner.ApplicationScanner;
import com.bachlinh.order.core.utils.UnsafeUtils;
import com.bachlinh.order.entity.transaction.spi.TransactionHolder;
import com.bachlinh.order.entity.utils.TransactionUtils;
import jakarta.persistence.EntityManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class SpringRepositoryManager implements RepositoryManager {

    private final Map<Class<?>, RepositoryBase> repositoryBaseMap = new HashMap<>();

    SpringRepositoryManager(DependenciesContainerResolver containerResolver) {
        ApplicationScanner scanner = new ApplicationScanner();
        Initializer<RepositoryBase> repositoryBaseInitializer = new RepositoryInitializer();
        scanner.findComponents()
                .stream()
                .filter(RepositoryBase.class::isAssignableFrom)
                .filter(clazz -> clazz.isAnnotationPresent(RepositoryComponent.class))
                .map(type -> repositoryBaseInitializer.getObject(type, containerResolver))
                .forEach(this::computedRepository);
    }

    @Override
    public boolean isRepositoryAvailable(Class<?> repositoryInterface) {
        return repositoryBaseMap.containsKey(repositoryInterface);
    }

    @Override
    public <T> T getRepository(Class<T> repositoryInterface) {
        if (!isRepositoryAvailable(repositoryInterface)) {
            throw new CriticalException("Repository with type [" + repositoryInterface.getName() + "] is unavailable");
        }
        return repositoryInterface.cast(repositoryBaseMap.get(repositoryInterface));
    }

    @Override
    public void assignTransaction(TransactionHolder<?> transactionHolder) {
        EntityManager entityManager = TransactionUtils.extractEntityManager(transactionHolder);
        repositoryBaseMap.forEach((aClass, repositoryBase) -> repositoryBase.setEntityManager(entityManager));
    }

    @Override
    public void releaseTransaction(TransactionHolder<?> transactionHolder) {
        repositoryBaseMap.forEach((aClass, repositoryBase) -> repositoryBase.setEntityManager(null));
        transactionHolder.cleanup(transactionHolder);
    }

    private void computedRepository(RepositoryBase repositoryBase) {
        boolean contained = Arrays.stream(repositoryBase.getRepositoryTypes()).anyMatch(repositoryBaseMap::containsKey);
        if (contained) {
            throw new CriticalException("One interface must have only one implement class");
        }
        for (Class<?> type : repositoryBase.getRepositoryTypes()) {
            repositoryBaseMap.put(type, repositoryBase);
        }
    }

    private static class RepositoryInitializer implements Initializer<RepositoryBase> {

        @Override
        public RepositoryBase getObject(Class<?> type, Object... params) {
            try {
                RepositoryBase dummy = (RepositoryBase) UnsafeUtils.allocateInstance(type);
                return dummy.getInstance((DependenciesContainerResolver) params[0]);
            } catch (InstantiationException e) {
                throw new CriticalException(e);
            }
        }
    }
}
