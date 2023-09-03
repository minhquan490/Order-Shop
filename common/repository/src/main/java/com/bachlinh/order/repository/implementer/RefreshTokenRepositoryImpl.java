package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.entity.model.RefreshToken_;
import com.bachlinh.order.repository.RefreshTokenRepository;
import com.bachlinh.order.repository.adapter.AbstractRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

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
        Select idSelect = Select.builder().column(RefreshToken_.ID).build();
        Select timeCreatedSelect = Select.builder().column(RefreshToken_.TIME_CREATED).build();
        Select timeExpiredSelect = Select.builder().column(RefreshToken_.TIME_EXPIRED).build();
        Select valueSelect = Select.builder().column(RefreshToken_.REFRESH_TOKEN_VALUE).build();
        Select customerIdSelect = Select.builder().column(Customer_.ID).build();
        Select customerUsernameSelect = Select.builder().column(Customer_.USERNAME).build();
        Join customerJoin = Join.builder().attribute(RefreshToken_.CUSTOMER).type(JoinType.INNER).build();
        Where tokenValueWhere = Where.builder().attribute(RefreshToken_.REFRESH_TOKEN_VALUE).value(token).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(RefreshToken.class);
        sqlSelect.select(idSelect)
                .select(timeCreatedSelect)
                .select(timeExpiredSelect)
                .select(valueSelect)
                .select(customerIdSelect)
                .select(customerUsernameSelect);
        SqlJoin sqlJoin = sqlSelect.join(customerJoin);
        return getRefreshToken(sqlJoin.where(tokenValueWhere));
    }

    @Override
    public RefreshToken getRefreshTokenByCustomer(Customer customer) {
        Select idSelect = Select.builder().column(RefreshToken_.ID).build();
        Select timeCreatedSelect = Select.builder().column(RefreshToken_.TIME_CREATED).build();
        Select timeExpiredSelect = Select.builder().column(RefreshToken_.TIME_EXPIRED).build();
        Select valueSelect = Select.builder().column(RefreshToken_.REFRESH_TOKEN_VALUE).build();
        Select customerIdSelect = Select.builder().column(Customer_.ID).build();
        Select customerUsernameSelect = Select.builder().column(Customer_.USERNAME).build();
        Join customerJoin = Join.builder().attribute(RefreshToken_.CUSTOMER).type(JoinType.INNER).build();
        Where customerWhere = Where.builder().attribute(RefreshToken_.CUSTOMER).value(customer).operator(Operator.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(RefreshToken.class);
        SqlJoin sqlJoin = sqlSelect.join(customerJoin);
        sqlSelect.select(idSelect)
                .select(timeCreatedSelect)
                .select(timeExpiredSelect)
                .select(valueSelect)
                .select(customerIdSelect, Customer.class)
                .select(customerUsernameSelect, Customer.class);
        return getRefreshToken(sqlJoin.where(customerWhere));
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

    @Nullable
    private RefreshToken getRefreshToken(SqlWhere where) {
        String sql = where.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(where.getQueryBindings());
        var results = executeNativeQuery(sql, attributes, RefreshToken.class);
        if (results.isEmpty()) {
            return null;
        } else {
            return results.get(0);
        }
    }
}
