package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.RefreshToken;
import com.bachlinh.order.entity.model.RefreshToken_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlJoin;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.repository.RefreshTokenRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class RefreshTokenRepositoryImpl extends AbstractRepository<String, RefreshToken> implements RefreshTokenRepository, RepositoryBase {

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
        Where tokenValueWhere = Where.builder().attribute(RefreshToken_.REFRESH_TOKEN_VALUE).value(token).operation(Operation.EQ).build();
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
        Select timeCreatedSelect = Select.builder().column(RefreshToken_.TIME_CREATED).build();
        Select timeExpiredSelect = Select.builder().column(RefreshToken_.TIME_EXPIRED).build();
        Select valueSelect = Select.builder().column(RefreshToken_.REFRESH_TOKEN_VALUE).build();
        Where customerWhere = Where.builder().attribute(RefreshToken_.CUSTOMER).value(customer).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(RefreshToken.class);
        sqlSelect.select(timeCreatedSelect)
                .select(timeExpiredSelect)
                .select(valueSelect);
        return getRefreshToken(sqlSelect.where(customerWhere));
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
    public void deleteRefreshToken(RefreshToken refreshToken) {
        if (refreshToken == null) {
            return;
        }
        delete(refreshToken);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new RefreshTokenRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{RefreshTokenRepository.class};
    }

    @Nullable
    private RefreshToken getRefreshToken(SqlWhere where) {
        String sql = where.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(where.getQueryBindings());
        return getSingleResult(sql, attributes, RefreshToken.class);
    }
}
