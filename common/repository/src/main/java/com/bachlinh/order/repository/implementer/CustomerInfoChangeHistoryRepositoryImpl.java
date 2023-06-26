package com.bachlinh.order.repository.implementer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CustomerInfoChangerHistoryRepository;
import com.bachlinh.order.service.container.DependenciesResolver;

import java.util.Collection;

@RepositoryComponent
@ActiveReflection
public class CustomerInfoChangeHistoryRepositoryImpl extends AbstractRepository<CustomerInfoChangeHistory, String> implements CustomerInfoChangerHistoryRepository {

    @DependenciesInitialize
    @ActiveReflection
    public CustomerInfoChangeHistoryRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(CustomerInfoChangeHistory.class, dependenciesResolver);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public void saveHistory(CustomerInfoChangeHistory history) {
        save(history);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public void saveHistories(Collection<CustomerInfoChangeHistory> histories) {
        saveAll(histories);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
