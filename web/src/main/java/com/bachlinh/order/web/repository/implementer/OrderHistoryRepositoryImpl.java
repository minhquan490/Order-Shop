package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderHistory;
import com.bachlinh.order.entity.model.OrderHistory_;
import com.bachlinh.order.entity.model.Order_;
import com.bachlinh.order.web.repository.spi.AbstractRepository;
import com.bachlinh.order.repository.RepositoryBase;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operation;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.web.repository.spi.OrderHistoryRepository;
import jakarta.persistence.criteria.JoinType;

import java.util.Collection;
import java.util.Map;

@RepositoryComponent
@ActiveReflection
public class OrderHistoryRepositoryImpl extends AbstractRepository<Integer, OrderHistory> implements OrderHistoryRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public OrderHistoryRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(OrderHistory.class, containerResolver.getDependenciesResolver());
    }

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
