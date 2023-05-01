package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
class LoginHistoryRepositoryImpl extends AbstractRepository<LoginHistory, Integer> implements LoginHistoryRepository {

    @Autowired
    LoginHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
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
