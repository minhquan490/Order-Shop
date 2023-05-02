package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RepositoryComponent
@ActiveReflection
public class LoginHistoryRepositoryImpl extends AbstractRepository<LoginHistory, Integer> implements LoginHistoryRepository {

    @DependenciesInitialize
    @ActiveReflection
    public LoginHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(LoginHistory.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public LoginHistory saveHistory(LoginHistory loginHistory) {
        return save(loginHistory);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
