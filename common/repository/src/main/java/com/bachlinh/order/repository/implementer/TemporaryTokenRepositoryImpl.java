package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.TemporaryToken;
import com.bachlinh.order.entity.model.TemporaryToken_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlJoin;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.repository.utils.QueryUtils;
import com.bachlinh.order.repository.TemporaryTokenRepository;
import com.bachlinh.order.service.container.DependenciesResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@ActiveReflection
@RepositoryComponent
public class TemporaryTokenRepositoryImpl extends AbstractRepository<Integer, TemporaryToken> implements TemporaryTokenRepository {

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
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.MANDATORY)
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
        return getTemporaryToken(sqlJoin.where(tokenValueWhere), tokenValueWhere, sqlSelect);
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
    @ActiveReflection
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }

    @Nullable
    private TemporaryToken getTemporaryToken(SqlWhere where, Where customerWhere, SqlSelect sqlSelect) {
        String query = where.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(where.getQueryBindings());
        return getSingleResult(query, attributes, TemporaryToken.class);
    }
}
