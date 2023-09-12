package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.model.CustomerAccessHistory;
import com.bachlinh.order.entity.model.CustomerAccessHistory_;
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.CustomerAccessHistoryRepository;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Collection;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;
import static org.springframework.transaction.annotation.Propagation.REQUIRED;

@RepositoryComponent
@ActiveReflection
public class CustomerAccessHistoryRepositoryImpl extends AbstractRepository<Integer, CustomerAccessHistory> implements CustomerAccessHistoryRepository {

    @DependenciesInitialize
    @ActiveReflection
    public CustomerAccessHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(CustomerAccessHistory.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void saveCustomerHistory(CustomerAccessHistory customerAccessHistory) {
        this.save(customerAccessHistory);
    }

    @Override
    @Transactional(propagation = REQUIRED, isolation = READ_COMMITTED)
    public void saveAllCustomerAccessHistory(Collection<CustomerAccessHistory> customerAccessHistories) {
        saveAll(customerAccessHistories);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public boolean deleteCustomerHistory(CustomerAccessHistory customerAccessHistory) {
        if (customerAccessHistory == null) {
            return false;
        }
        if (exists(customerAccessHistory.getId())) {
            delete(customerAccessHistory);
            return true;
        } else {
            return false;
        }
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public void deleteAll(Collection<CustomerAccessHistory> histories) {
        super.deleteAll(histories);
    }

    @Override
    public Collection<CustomerAccessHistory> getHistoriesExpireNow(Date now) {
        Where expiryWhere = Where.builder().attribute(CustomerAccessHistory_.REMOVE_TIME).value(now).operator(Operator.EQ).build();
        return getCustomerAccessHistories(expiryWhere);
    }

    @Override
    @PersistenceContext
    @ActiveReflection
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
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

}
