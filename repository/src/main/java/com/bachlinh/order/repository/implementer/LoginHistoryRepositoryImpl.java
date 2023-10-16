package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.LoginHistory;
import com.bachlinh.order.entity.model.LoginHistory_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Join;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.OrderBy;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlJoin;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.LoginHistoryRepository;
import jakarta.persistence.criteria.JoinType;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class LoginHistoryRepositoryImpl extends AbstractRepository<Integer, LoginHistory> implements LoginHistoryRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public LoginHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(LoginHistory.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public void saveHistory(LoginHistory loginHistory) {
        save(loginHistory);
    }

    @Override
    public Collection<LoginHistory> getHistories(Customer owner, long limit) {
        Select idSelect = Select.builder().column(LoginHistory_.ID).build();
        Select lastLoginTimeSelect = Select.builder().column(LoginHistory_.LAST_LOGIN_TIME).build();
        Select loginIpSelect = Select.builder().column(LoginHistory_.LOGIN_IP).build();
        Select successSelect = Select.builder().column(LoginHistory_.SUCCESS).build();
        Join customerJoin = Join.builder().attribute(LoginHistory_.CUSTOMER).type(JoinType.INNER).build();
        OrderBy lastLoginTimeOrderBy = OrderBy.builder().column(LoginHistory_.LAST_LOGIN_TIME).type(OrderBy.Type.DESC).build();
        var ownerWhere = Where.builder().attribute(Customer_.ID).value(owner.getId()).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(LoginHistory.class);
        sqlSelect.select(idSelect)
                .select(lastLoginTimeSelect)
                .select(loginIpSelect)
                .select(successSelect);
        SqlJoin sqlJoin = sqlSelect.join(customerJoin);
        SqlWhere sqlWhere = sqlJoin.where(ownerWhere, Customer.class);
        sqlWhere.orderBy(lastLoginTimeOrderBy, LoginHistory.class);
        sqlWhere.limit(limit);
        return getLoginHistories(sqlWhere);
    }

    @Override
    public Collection<LoginHistory> getHistoriesOfCustomer(String customerId, long page, long pageSize) {
        Select idSelect = Select.builder().column(LoginHistory_.ID).build();
        Select lastLoginTimeSelect = Select.builder().column(LoginHistory_.LAST_LOGIN_TIME).build();
        Select loginIpSelect = Select.builder().column(LoginHistory_.LOGIN_IP).build();
        Select successSelect = Select.builder().column(LoginHistory_.SUCCESS).build();
        Where customerWhere = Where.builder().attribute(LoginHistory_.CUSTOMER).value(customerId).operation(Operation.EQ).build();
        OrderBy lastLoginTimeOrderBy = OrderBy.builder().column(LoginHistory_.LAST_LOGIN_TIME).type(OrderBy.Type.DESC).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(LoginHistory.class);
        sqlSelect.select(idSelect)
                .select(lastLoginTimeSelect)
                .select(loginIpSelect)
                .select(successSelect);
        SqlWhere sqlWhere = sqlSelect.where(customerWhere);
        sqlWhere.orderBy(lastLoginTimeOrderBy).limit(pageSize).offset(QueryUtils.calculateOffset(page, pageSize));
        return getLoginHistories(sqlWhere);
    }

    @Override
    public Collection<LoginHistory> getHistories(Customer owner) {
        Where ownerWhere = Where.builder().attribute(LoginHistory_.CUSTOMER).value(owner).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(ownerWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    public Long countHistoriesOfCustomer(String customerId) {
        Where ownerWhere = Where.builder().attribute(LoginHistory_.CUSTOMER).value(customerId).operation(Operation.EQ).build();
        return count(ownerWhere);
    }

    @Override
    public void deleteLoginHistories(Collection<LoginHistory> loginHistories) {
        deleteAll(loginHistories);
    }

    private Collection<LoginHistory> getLoginHistories(SqlWhere sqlWhere) {
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, LoginHistory.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new LoginHistoryRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{LoginHistoryRepository.class};
    }
}
