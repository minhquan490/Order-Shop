package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.TemporaryToken;
import com.bachlinh.order.entity.model.TemporaryToken_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.TemporaryTokenRepository;
import com.bachlinh.order.repository.query.CriteriaPredicateParser;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@ActiveReflection
@RepositoryComponent
public class TemporaryTokenRepositoryImpl extends AbstractRepository<TemporaryToken, Integer> implements TemporaryTokenRepository {

    @ActiveReflection
    public TemporaryTokenRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(TemporaryToken.class, dependenciesResolver);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void saveTemporaryToken(TemporaryToken token) {
        this.save(token);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void deleteTemporaryToken(Integer tokenId) {
        this.deleteById(tokenId);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY, isolation = Isolation.READ_COMMITTED)
    public void updateTemporaryToken(TemporaryToken token) {
        this.save(token);
    }

    @Override
    public TemporaryToken getTemporaryTokenOfCustomer(Customer customer) {
        Where customerWhere = Where.builder().attribute(TemporaryToken_.ASSIGN_CUSTOMER).value(customer).build();
        Specification<TemporaryToken> spec = Specification.where((root, query, criteriaBuilder) -> {
            var extractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            extractor.where(customerWhere);
            return extractor.parse();
        });
        return findOne(spec).orElse(null);
    }

    @Override
    public TemporaryToken getTemporaryToken(String tokenValue) {
        Where tokenValueWhere = Where.builder().attribute(TemporaryToken_.VALUE).value(tokenValue).build();
        Join customerJoin = Join.builder().attribute(TemporaryToken_.ASSIGN_CUSTOMER).type(JoinType.INNER).build();
        Specification<TemporaryToken> spec = Specification.where((root, query, criteriaBuilder) -> {
            var queryExtractor = new CriteriaPredicateParser(criteriaBuilder, query, root);
            queryExtractor.where(tokenValueWhere);
            queryExtractor.join(customerJoin);
            return queryExtractor.parse();
        });
        return findOne(spec).orElse(null);
    }

    @Override
    @ActiveReflection
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
