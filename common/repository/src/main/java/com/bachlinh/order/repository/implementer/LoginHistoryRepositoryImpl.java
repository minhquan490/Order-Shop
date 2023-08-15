package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.LoginHistory_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.LoginHistoryRepository;
import com.bachlinh.order.repository.query.CriteriaPredicateParser;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class LoginHistoryRepositoryImpl extends AbstractRepository<LoginHistory, Integer> implements LoginHistoryRepository {

    @DependenciesInitialize
    @ActiveReflection
    public LoginHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(LoginHistory.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public LoginHistory saveHistory(LoginHistory loginHistory) {
        return save(loginHistory);
    }

    @Override
    public Collection<LoginHistory> getHistories(Customer owner) {
        var ownerWhere = Where.builder().attribute(LoginHistory_.CUSTOMER).value(owner).operator(Operator.EQ).build();
        Specification<LoginHistory> spec = Specification.where((root, query, criteriaBuilder) -> {
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(ownerWhere);
            return extractor.parse();
        });
        return findAll(spec);
    }

    @Override
    @PersistenceContext
    protected void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
