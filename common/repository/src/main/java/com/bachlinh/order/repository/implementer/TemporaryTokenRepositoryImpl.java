package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.TemporaryToken;
import com.bachlinh.order.entity.model.TemporaryToken_;
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
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.TemporaryTokenRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.lang.Nullable;

import java.util.Map;

@ActiveReflection
@RepositoryComponent
public class TemporaryTokenRepositoryImpl extends AbstractRepository<Integer, TemporaryToken> implements TemporaryTokenRepository, RepositoryBase {

    @ActiveReflection
    public TemporaryTokenRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(TemporaryToken.class, dependenciesResolver);
    }

    @Override
    public void saveTemporaryToken(TemporaryToken token) {
        this.save(token);
    }

    @Override
    public void deleteTemporaryToken(TemporaryToken token) {
        if (token == null) {
            return;
        }
        delete(token);
    }

    @Override
    public TemporaryToken getTemporaryToken(String tokenValue) {
        Select idSelect = Select.builder().column(TemporaryToken_.ID).build();
        Select valueSelect = Select.builder().column(TemporaryToken_.VALUE).build();
        Select expiryTimeSelect = Select.builder().column(TemporaryToken_.EXPIRY_TIME).build();
        Select customerPassword = Select.builder().column(Customer_.PASSWORD).build();
        Select customerActivated = Select.builder().column(Customer_.ACTIVATED).build();
        Where tokenValueWhere = Where.builder().attribute(TemporaryToken_.VALUE).value(tokenValue).build();
        Join customerJoin = Join.builder().attribute(TemporaryToken_.ASSIGN_CUSTOMER).type(JoinType.INNER).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(TemporaryToken.class);
        sqlSelect.select(idSelect)
                .select(valueSelect)
                .select(expiryTimeSelect)
                .select(customerPassword, Customer.class)
                .select(customerActivated, Customer.class);
        SqlJoin sqlJoin = sqlSelect.join(customerJoin);
        return getTemporaryToken(sqlJoin.where(tokenValueWhere));
    }

    @Override
    public TemporaryToken getTemporaryToken(Customer owner) {
        Join customerJoin = Join.builder().attribute(TemporaryToken_.ASSIGN_CUSTOMER).type(JoinType.INNER).build();
        Where ownerWhere = Where.builder().attribute(Customer_.ID).value(owner).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(TemporaryToken.class);
        SqlJoin sqlJoin = sqlSelect.join(customerJoin);
        SqlWhere sqlWhere = sqlJoin.where(ownerWhere, Customer.class);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getSingleResult(query, attributes, getDomainClass());
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new TemporaryTokenRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{TemporaryTokenRepository.class};
    }

    @Nullable
    private TemporaryToken getTemporaryToken(SqlWhere where) {
        String query = where.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(where.getQueryBindings());
        return getSingleResult(query, attributes, TemporaryToken.class);
    }
}
