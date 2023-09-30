package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.container.DependenciesResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory;
import com.bachlinh.order.entity.model.CustomerInfoChangeHistory_;
import com.bachlinh.order.entity.repository.AbstractRepository;
import com.bachlinh.order.entity.repository.RepositoryBase;
import com.bachlinh.order.entity.repository.query.Operation;
import com.bachlinh.order.entity.repository.query.OrderBy;
import com.bachlinh.order.entity.repository.query.Select;
import com.bachlinh.order.entity.repository.query.SqlBuilder;
import com.bachlinh.order.entity.repository.query.SqlSelect;
import com.bachlinh.order.entity.repository.query.SqlWhere;
import com.bachlinh.order.entity.repository.query.Where;
import com.bachlinh.order.entity.utils.QueryUtils;
import com.bachlinh.order.repository.CustomerInfoChangeHistoryRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class CustomerInfoChangeHistoryRepositoryImpl extends AbstractRepository<String, CustomerInfoChangeHistory> implements CustomerInfoChangeHistoryRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public CustomerInfoChangeHistoryRepositoryImpl(DependenciesResolver dependenciesResolver) {
        super(CustomerInfoChangeHistory.class, dependenciesResolver);
    }

    @Override
    public void saveHistories(Collection<CustomerInfoChangeHistory> histories) {
        saveAll(histories);
    }

    @Override
    public Collection<CustomerInfoChangeHistory> getHistoriesInYear() {
        Where timeUpdateWhere = Where.builder().attribute(CustomerInfoChangeHistory_.TIME_UPDATE).value(Timestamp.from(Instant.now())).operation(Operation.GE).build();
        return getAccessHistories(timeUpdateWhere, -1);
    }

    @Override
    public Collection<CustomerInfoChangeHistory> getHistoriesChangeOfCustomer(Customer customer, long limit) {
        var customerWhere = Where.builder().attribute(CustomerInfoChangeHistory_.CUSTOMER).value(customer).operation(Operation.EQ).build();
        return getAccessHistories(customerWhere, limit);
    }

    @Override
    public Collection<CustomerInfoChangeHistory> getHistoriesChangeOfCustomer(String customerId, long page, long pageSize) {
        Where customerWhere = Where.builder().attribute(CustomerInfoChangeHistory_.CUSTOMER).value(customerId).operation(Operation.EQ).build();
        OrderBy timeUpdateOrderBy = OrderBy.builder().column(CustomerInfoChangeHistory_.TIME_UPDATE).type(OrderBy.Type.DESC).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(customerWhere);
        sqlWhere.orderBy(timeUpdateOrderBy).limit(pageSize).offset(QueryUtils.calculateOffset(page, pageSize));

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    public Long countChangeHistories(String customerId) {
        Where ownerWhere = Where.builder().attribute(CustomerInfoChangeHistory_.CUSTOMER).value(customerId).operation(Operation.EQ).build();
        return count(ownerWhere);
    }

    @Override
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
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new CustomerInfoChangeHistoryRepositoryImpl(containerResolver.getDependenciesResolver());
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{CustomerInfoChangeHistoryRepository.class};
    }

    private Collection<CustomerInfoChangeHistory> getAccessHistories(Where where, long limit) {
        Select customerInfoChangeHistoryIdSelect = Select.builder().column(CustomerInfoChangeHistory_.ID).build();
        Select customerInfoChangeHistoryOldValueSelect = Select.builder().column(CustomerInfoChangeHistory_.OLD_VALUE).build();
        Select customerInfoChangeHistoryFieldNameSelect = Select.builder().column(CustomerInfoChangeHistory_.FIELD_NAME).build();
        Select customerInfoChangeHistoryTimeUpdateSelect = Select.builder().column(CustomerInfoChangeHistory_.TIME_UPDATE).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(CustomerInfoChangeHistory.class);
        sqlSelect.select(customerInfoChangeHistoryIdSelect)
                .select(customerInfoChangeHistoryOldValueSelect)
                .select(customerInfoChangeHistoryFieldNameSelect)
                .select(customerInfoChangeHistoryTimeUpdateSelect);
        SqlWhere sqlWhere = sqlSelect.where(where);
        if (limit > 0) {
            OrderBy idOrderBy = OrderBy.builder().column(CustomerInfoChangeHistory_.ID).type(OrderBy.Type.DESC).build();
            sqlWhere.orderBy(idOrderBy);
            sqlWhere.limit(limit);
        }
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, CustomerInfoChangeHistory.class);
    }
}
