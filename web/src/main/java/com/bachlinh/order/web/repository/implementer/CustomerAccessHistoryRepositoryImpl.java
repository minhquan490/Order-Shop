package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.CustomerAccessHistory_;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.OrderBy;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.web.repository.spi.CustomerAccessHistoryRepository;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class CustomerAccessHistoryRepositoryImpl extends AbstractRepository<Integer, CustomerAccessHistory> implements CustomerAccessHistoryRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public CustomerAccessHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(CustomerAccessHistory.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public void saveCustomerHistory(CustomerAccessHistory customerAccessHistory) {
        this.save(customerAccessHistory);
    }

    @Override
    public void saveAllCustomerAccessHistory(Collection<CustomerAccessHistory> customerAccessHistories) {
        saveAll(customerAccessHistories);
    }

    @Override
    public void deleteAll(Collection<CustomerAccessHistory> histories) {
        super.deleteAll(histories);
    }

    @Override
    public Collection<CustomerAccessHistory> getHistoriesExpireNow(Date now) {
        Where expiryWhere = Where.builder().attribute(CustomerAccessHistory_.REMOVE_TIME).value(now).operation(Operation.EQ).build();
        return getCustomerAccessHistories(expiryWhere);
    }

    @Override
    public Collection<CustomerAccessHistory> getHistoriesOfCustomer(Customer customer) {
        Where ownerWhere = Where.builder().attribute(CustomerAccessHistory_.CUSTOMER).value(customer).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(ownerWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    public Collection<CustomerAccessHistory> getHistoriesOfCustomer(Customer customer, long page, long pageSize) {
        Where ownerWhere = Where.builder().attribute(CustomerAccessHistory_.CUSTOMER).value(customer).operation(Operation.EQ).build();
        OrderBy requestTimeOrderBy = OrderBy.builder().column(CustomerAccessHistory_.REQUEST_TIME).type(OrderBy.Type.DESC).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(ownerWhere);
        sqlWhere.limit(pageSize);
        sqlWhere.offset(page);
        sqlWhere.orderBy(requestTimeOrderBy);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    public Long countAccessHistoriesOfCustomer(String customerId) {
        Where ownerWhere = Where.builder().attribute(CustomerAccessHistory_.CUSTOMER).value(customerId).operation(Operation.EQ).build();
        return count(ownerWhere);
    }

    private Collection<CustomerAccessHistory> getCustomerAccessHistories(Where where) {
        Select customerAccessHistoryIdSelect = Select.builder().column(CustomerAccessHistory_.ID).build();
        Select customerAccessHistoryPathRequestSelect = Select.builder().column(CustomerAccessHistory_.PATH_REQUEST).build();
        Select customerAccessHistoryRequestTypeSelect = Select.builder().column(CustomerAccessHistory_.REQUEST_TYPE).build();
        Select customerAccessHistoryRequestTimeSelect = Select.builder().column(CustomerAccessHistory_.REQUEST_TIME).build();
        Select customerAccessHistoryRequestContentSelect = Select.builder().column(CustomerAccessHistory_.REQUEST_CONTENT).build();
        Select customerAccessHistoryCustomerIpSelect = Select.builder().column(CustomerAccessHistory_.CUSTOMER_IP).build();
        Select customerAccessHistoryRemoveTimeSelect = Select.builder().column(CustomerAccessHistory_.REMOVE_TIME).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(CustomerAccessHistory.class);
        sqlSelect.select(customerAccessHistoryIdSelect)
                .select(customerAccessHistoryPathRequestSelect)
                .select(customerAccessHistoryRequestTypeSelect)
                .select(customerAccessHistoryRequestTimeSelect)
                .select(customerAccessHistoryRequestContentSelect)
                .select(customerAccessHistoryCustomerIpSelect)
                .select(customerAccessHistoryRemoveTimeSelect);
        SqlWhere sqlWhere = sqlSelect.where(where);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, CustomerAccessHistory.class);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new CustomerAccessHistoryRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{CustomerAccessHistoryRepository.class};
    }
}
