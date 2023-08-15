package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CustomerInfoChangerHistoryRepository;
import com.bachlinh.order.repository.query.CriteriaPredicateParser;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
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
    public Collection<CustomerInfoChangeHistory> getHistoriesInYear() {
        Specification<CustomerInfoChangeHistory> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(CustomerInfoChangeHistory_.timeUpdate), Timestamp.from(Instant.now())));
        return findAll(spec);
    }

    @Override
    public Collection<CustomerInfoChangeHistory> getHistoriesChangeOfCustomer(Customer customer) {
        var customerWhere = Where.builder().attribute(CustomerInfoChangeHistory_.CUSTOMER).value(customer).build();
        Specification<CustomerInfoChangeHistory> spec = Specification.where((root, query, criteriaBuilder) -> {
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(customerWhere);
            return extractor.parse();
        });
        return findAll(spec);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
    public void deleteHistories(Collection<CustomerInfoChangeHistory> histories) {
        var savePointManager = getEntityFactory().getTransactionManager().getSavePointManager();
        savePointManager.createSavePoint("histories");
        try {
            deleteAllById(histories.stream().map(CustomerInfoChangeHistory::getId).toList());
        } catch (Exception e) {
            savePointManager.rollbackToSavePoint("histories");
        } finally {
            savePointManager.release();
        }
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
