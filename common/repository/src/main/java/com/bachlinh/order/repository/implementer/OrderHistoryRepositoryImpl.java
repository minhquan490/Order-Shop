package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.entity.model.OrderHistory_;
import com.bachlinh.order.entity.model.Order_;
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
import com.bachlinh.order.repository.OrderHistoryRepository;
import jakarta.persistence.criteria.JoinType;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class OrderHistoryRepositoryImpl extends AbstractRepository<Integer, OrderHistory> implements OrderHistoryRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public OrderHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(OrderHistory.class, containerResolver.getDependenciesResolver());
    }

    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    @Override
    public void saveOrderHistory(OrderHistory orderHistory) {
        save(orderHistory);
    }

    @Override
    public Collection<OrderHistory> getHistoriesOfCustomer(Customer customer) {
        Select idSelect = Select.builder().column(OrderHistory_.ID).build();
        Select timeOrderSelect = Select.builder().column(OrderHistory_.ORDER_TIME).build();
        Select orderStatusSelect = Select.builder().column(OrderHistory_.ORDER_STATUS).build();
        Join orderJoin = Join.builder().attribute(OrderHistory_.ORDER).type(JoinType.INNER).build();
        Join customerJoin = Join.builder().attribute(Order_.CUSTOMER).type(JoinType.INNER).build();
        Where customerWhere = Where.builder().attribute(Customer_.ID).value(customer.getId()).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(OrderHistory.class);
        sqlSelect.select(idSelect).select(timeOrderSelect).select(orderStatusSelect);
        SqlJoin sqlJoin = sqlSelect.join(orderJoin).join(customerJoin, Customer.class);
        SqlWhere sqlWhere = sqlJoin.where(customerWhere, Customer.class);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, OrderHistory.class);
    }

    @Override
    public OrderHistory getHistoryOfOrder(Order order) {
        Where orderWhere = Where.builder().attribute(OrderHistory_.ORDER).value(order).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(getDomainClass());
        SqlWhere sqlWhere = sqlSelect.where(orderWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getSingleResult(query, attributes, getDomainClass());
    }

    @Override
    @Transactional(isolation = READ_COMMITTED, propagation = MANDATORY)
    public void deleteOrderHistory(OrderHistory orderHistory) {
        delete(orderHistory);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new OrderHistoryRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{OrderHistoryRepository.class};
    }
}
