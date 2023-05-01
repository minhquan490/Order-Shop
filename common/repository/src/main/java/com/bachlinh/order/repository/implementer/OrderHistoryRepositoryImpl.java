package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.OrderHistoryRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
class OrderHistoryRepositoryImpl extends AbstractRepository<OrderHistory, Integer> implements OrderHistoryRepository {

    @Autowired
    OrderHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(OrderHistory.class, containerResolver.getDependenciesResolver());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public void saveOrderHistory(OrderHistory orderHistory) {
        save(orderHistory);
    }

    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    @Override
    public void updateOrderHistory(OrderHistory orderHistory) {
        saveOrderHistory(orderHistory);
    }

    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    @Override
    public void deleteOrderHistory(OrderHistory orderHistory) {
        delete(orderHistory);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
