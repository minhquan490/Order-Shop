package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.entity.model.RefreshToken_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.RefreshTokenRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.QueryExtractor;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class RefreshTokenRepositoryImpl extends AbstractRepository<RefreshToken, String> implements RefreshTokenRepository {

    @DependenciesInitialize
    @ActiveReflection
    public RefreshTokenRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(RefreshToken.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public RefreshToken getRefreshToken(String token) {
        Specification<RefreshToken> spec = Specification.where(((root, query, criteriaBuilder) -> {
            query.multiselect(root.get(RefreshToken_.REFRESH_TOKEN_VALUE), root.get(RefreshToken_.CUSTOMER), root.get(RefreshToken_.TIME_EXPIRED));
            root.join("customer", JoinType.INNER);
            return criteriaBuilder.equal(root.get(RefreshToken_.REFRESH_TOKEN_VALUE), token);
        }));
        return get(spec);
    }

    @Override
    public RefreshToken getRefreshTokenByCustomer(Customer customer) {
        Specification<RefreshToken> spec = Specification.where((root, query, criteriaBuilder) -> {
            Where customerWhere = Where.builder().attribute(RefreshToken_.CUSTOMER).value(customer).operator(Operator.EQ).build();
            var queryExtractor = new QueryExtractor(criteriaBuilder, query, root);
            queryExtractor.where(customerWhere);
            return queryExtractor.extract();
        });
        return findOne(spec).orElse(null);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public RefreshToken saveRefreshToken(RefreshToken token) {
        return this.save(token);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public RefreshToken updateRefreshToken(RefreshToken refreshToken) {
        return saveRefreshToken(refreshToken);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean deleteRefreshToken(RefreshToken refreshToken) {
        if (StringUtils.hasText((CharSequence) refreshToken.getId())) {
            long numRowDeleted = this.delete(Specification.where((root, query, builder) -> builder.equal(root.get("id"), refreshToken.getId())));
            return numRowDeleted != 0;
        } else {
            return false;
        }
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
