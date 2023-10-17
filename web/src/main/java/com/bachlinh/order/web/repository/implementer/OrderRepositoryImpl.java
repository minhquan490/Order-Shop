package com.bachlinh.order.web.repository.implementer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.DependenciesInitialize;
import com.bachlinh.order.core.annotation.RepositoryComponent;
import com.bachlinh.order.core.container.DependenciesContainerResolver;
import com.bachlinh.order.core.enums.OrderStatusValue;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Customer_;
import com.bachlinh.order.entity.model.Order;
import com.bachlinh.order.entity.model.OrderDetail;
import com.bachlinh.order.entity.model.OrderDetail_;
import com.bachlinh.order.entity.model.OrderStatus;
import com.bachlinh.order.entity.model.OrderStatus_;
import com.bachlinh.order.entity.model.Order_;
import com.bachlinh.order.entity.model.Product;
import com.bachlinh.order.entity.model.Product_;
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
import com.bachlinh.order.web.repository.spi.OrderRepository;

import jakarta.persistence.criteria.JoinType;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RepositoryComponent
@ActiveReflection
public class OrderRepositoryImpl extends AbstractRepository<String, Order> implements OrderRepository, RepositoryBase {

    @DependenciesInitialize
    @ActiveReflection
    public OrderRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Order.class, containerResolver.getDependenciesResolver());
    }

    @Override
    public Order saveOrder(Order order) {
        return Optional.of(this.save(order)).orElse(null);
    }

    @Override
    public Order updateOrder(Order order) {
        return saveOrder(order);
    }

    @Override
    public boolean deleteOrder(Order order) {
        if (order == null) {
            return false;
        }
        delete(order);
        return true;
    }

    @Override
    public boolean isOrderExist(String orderId) {
        return exists(orderId);
    }

    @Override
    public Order getOrder(String orderId) {
        Select idSelect = Select.builder().column(Order_.ID).build();
        Select timeOrderSelect = Select.builder().column(Order_.TIME_ORDER).build();
        Select orderStatusSelect = Select.builder().column(OrderStatus_.STATUS).build();
        Select orderDetailAmountSelect = Select.builder().column(OrderDetail_.AMOUNT).build();
        Select productIdSelect = Select.builder().column(Product_.ID).build();
        Select productNameSelect = Select.builder().column(Product_.NAME).build();
        Join orderStatusJoin = Join.builder().attribute(Order_.ORDER_STATUS).type(JoinType.INNER).build();
        Join orderDetailsJoin = Join.builder().attribute(Order_.ORDER_DETAILS).type(JoinType.INNER).build();
        Join productJoin = Join.builder().attribute(OrderDetail_.PRODUCT).type(JoinType.INNER).build();
        Where idWhere = Where.builder().attribute(Order_.ID).value(orderId).operation(Operation.EQ).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Order.class);
        sqlSelect.select(idSelect)
                .select(timeOrderSelect)
                .select(orderStatusSelect, OrderStatus.class)
                .select(orderDetailAmountSelect, OrderDetail.class)
                .select(productIdSelect, Product.class)
                .select(productNameSelect, Product.class);
        SqlJoin sqlJoin = sqlSelect.join(orderStatusJoin).join(orderDetailsJoin).join(productJoin, Product.class);
        SqlWhere sqlWhere = sqlJoin.where(idWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return getSingleResult(sql, attributes, Order.class);
    }

    @Override
    public List<Order> getNewOrdersInDate() {
        Select idSelect = Select.builder().column(Order_.ID).build();
        Select timeOrderSelect = Select.builder().column(Order_.TIME_ORDER).build();
        Select bankTransactionCodeSelect = Select.builder().column(Order_.BANK_TRANSACTION_CODE).build();
        Select orderStatusSelect = Select.builder().column(OrderStatus_.STATUS).build();
        Select customerUsernameSelect = Select.builder().column(Customer_.USERNAME).build();
        LocalDate now = LocalDate.now();
        Timestamp start = Timestamp.valueOf(LocalDateTime.of(now, LocalTime.of(0, 0, 0)));
        Timestamp end = Timestamp.valueOf(LocalDateTime.of(now, LocalTime.of(23, 59, 59)));
        Where timeOrderWhere = Where.builder().attribute(Order_.TIME_ORDER).value(new Object[]{start, end}).operation(Operation.BETWEEN).build();
        Where orderStatusWhere = Where.builder().attribute(OrderStatus_.STATUS).value(OrderStatusValue.UN_CONFIRMED.name()).operation(Operation.EQ).build();
        Join orderStatusJoin = Join.builder().attribute(Order_.ORDER_STATUS).type(JoinType.INNER).build();
        Join orderCustomerJoin = Join.builder().attribute(Order_.CUSTOMER).type(JoinType.INNER).build();
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Order.class);
        sqlSelect.select(idSelect)
                .select(timeOrderSelect)
                .select(bankTransactionCodeSelect)
                .select(orderStatusSelect, OrderStatus.class)
                .select(customerUsernameSelect, Customer.class);
        SqlJoin sqlJoin = sqlSelect.join(orderStatusJoin).join(orderCustomerJoin);
        SqlWhere sqlWhere = sqlJoin.where(timeOrderWhere).and(orderStatusWhere);
        String sql = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());
        return this.getResultList(sql, attributes, Order.class);
    }

    @Override
    public List<Order> getAll() {
        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Order.class);
        String sql = sqlSelect.getNativeQuery();
        return this.getResultList(sql, Collections.emptyMap(), Order.class);
    }

    @Override
    public Collection<Order> getOrdersOfCustomerForDelete(Customer owner) {
        Where ownerWhere = Where.builder().attribute(Order_.CUSTOMER).value(owner).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Order.class);
        SqlWhere sqlWhere = sqlSelect.where(ownerWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());


        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    public Collection<Order> getOrdersOfCustomer(String customerId, long page, long pageSize) {
        Select orderTimeSelect = Select.builder().column(Order_.TIME_ORDER).build();
        Select statusSelect = Select.builder().column(OrderStatus_.STATUS).build();

        Join orderStatusJoin = Join.builder().attribute(Order_.ORDER_STATUS).type(JoinType.INNER).build();

        Where customerWhere = Where.builder().attribute(Order_.CUSTOMER).value(customerId).operation(Operation.EQ).build();

        SqlBuilder sqlBuilder = getSqlBuilder();
        SqlSelect sqlSelect = sqlBuilder.from(Order.class);
        sqlSelect.select(orderTimeSelect).select(statusSelect, OrderStatus.class);
        SqlJoin sqlJoin = sqlSelect.join(orderStatusJoin);
        SqlWhere sqlWhere = sqlJoin.where(customerWhere);

        String query = sqlWhere.getNativeQuery();
        Map<String, Object> attributes = QueryUtils.parse(sqlWhere.getQueryBindings());

        return getResultList(query, attributes, getDomainClass());
    }

    @Override
    public void deleteOrders(Collection<Order> orders) {
        deleteAll(orders);
    }

    @Override
    public Long countOrdersOfCustomer(String customerId) {
        Where customerWhere = Where.builder().attribute(Order_.CUSTOMER).value(customerId).operation(Operation.EQ).build();
        return count(customerWhere);
    }

    @Override
    public RepositoryBase getInstance(DependenciesContainerResolver containerResolver) {
        return new OrderRepositoryImpl(containerResolver);
    }

    @Override
    public Class<?>[] getRepositoryTypes() {
        return new Class[]{OrderRepository.class};
    }
}
