package com.bachlinh.order.repository.implementer;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.DependenciesInitialize;
import com.bachlinh.order.annotation.RepositoryComponent;
import com.bachlinh.order.entity.enums.OrderStatusValue;
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
import com.bachlinh.order.repository.AbstractRepository;
import com.bachlinh.order.repository.OrderRepository;
import com.bachlinh.order.repository.query.Join;
import com.bachlinh.order.repository.query.Operator;
import com.bachlinh.order.repository.query.Select;
import com.bachlinh.order.repository.query.SqlBuilder;
import com.bachlinh.order.repository.query.SqlJoin;
import com.bachlinh.order.repository.query.SqlSelect;
import com.bachlinh.order.repository.query.SqlWhere;
import com.bachlinh.order.repository.query.Where;
import com.bachlinh.order.repository.utils.QueryUtils;
import com.bachlinh.order.service.container.DependenciesContainerResolver;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.JoinType;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static org.springframework.transaction.annotation.Propagation.MANDATORY;

@RepositoryComponent
@ActiveReflection
public class OrderRepositoryImpl extends AbstractRepository<String, Order> implements OrderRepository {

    @DependenciesInitialize
    @ActiveReflection
    public OrderRepositoryImpl(DependenciesContainerResolver containerResolver) {
        super(Order.class, containerResolver.getDependenciesResolver());
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Order saveOrder(Order order) {
        return Optional.of(this.save(order)).orElse(null);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
    public Order updateOrder(Order order) {
        return saveOrder(order);
    }

    @Override
    @Transactional(propagation = MANDATORY, isolation = READ_COMMITTED)
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
        Where idWhere = Where.builder().attribute(Order_.ID).value(orderId).operator(Operator.EQ).build();
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
        Where timeOrderWhere = Where.builder().attribute(Order_.TIME_ORDER).value(new Object[]{start, end}).operator(Operator.BETWEEN).build();
        Where orderStatusWhere = Where.builder().attribute(OrderStatus_.STATUS).value(OrderStatusValue.UN_CONFIRMED.name()).operator(Operator.EQ).build();
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
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        super.setEntityManager(entityManager);
    }
}
