package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.CustomerAccessHistory_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Collection;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@RepositoryComponent
@ActiveReflection
public class CustomerAccessHistoryRepositoryImpl extends AbstractRepository<CustomerAccessHistory, Integer> implements CustomerAccessHistoryRepository {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @DependenciesInitialize
    @ActiveReflection
    public CustomerAccessHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(CustomerAccessHistory.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public CustomerAccessHistory saveCustomerHistory(CustomerAccessHistory customerAccessHistory) {
        return this.save(customerAccessHistory);
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = READ_COMMITTED)
    public void saveAllCustomerAccessHistory(Collection<CustomerAccessHistory> customerAccessHistories) {
        saveAll(customerAccessHistories);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean deleteCustomerHistory(CustomerAccessHistory customerAccessHistory) {
        if (customerAccessHistory == null) {
            return false;
        }
        if (existsById(customerAccessHistory.getId())) {
            delete(customerAccessHistory);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean deleteAll(Collection<CustomerAccessHistory> histories) {
        try {
            this.deleteAllInBatch(histories);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Collection<CustomerAccessHistory> getHistories(Customer customer) {
        Specification<CustomerAccessHistory> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(CustomerAccessHistory_.customer), customer));
        return findAll(spec);
    }

    @Override
    public Collection<CustomerAccessHistory> getHistoriesExpireNow(Date now) {
        Specification<CustomerAccessHistory> spec = Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(CustomerAccessHistory_.removeTime), now)));
        return findAll(spec);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
