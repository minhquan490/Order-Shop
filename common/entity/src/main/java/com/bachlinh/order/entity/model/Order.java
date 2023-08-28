package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

@Entity
@Table(name = "ORDERS", indexes = @Index(name = "idx_order_customer", columnList = "CUSTOMER_ID"))
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "order")
@Label("ODR-")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class Order extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "ORDER_TIME", nullable = false)
    private Timestamp timeOrder;

    @Column(name = "BANK_TRANSACTION_CODE")
    private String bankTransactionCode;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_STATUS_ID", unique = true, nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private OrderStatus orderStatus;

    @OneToOne(mappedBy = "order")
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private OrderHistory orderHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Collection<OrderDetail> orderDetails = new HashSet<>();

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of order must be string");
        }
        this.id = (String) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<Order> results = new LinkedList<>();
            Order first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Order) entity;
                } else {
                    Order casted = (Order) entity;
                    if (casted.getOrderDetails().isEmpty()) {
                        results.add(first);
                    } else {
                        first.getOrderDetails().addAll(casted.getOrderDetails());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    public boolean isDeposited() {
        return getBankTransactionCode().isEmpty();
    }

    @ActiveReflection
    public void setTimeOrder(Timestamp timeOrder) {
        if (this.timeOrder != null && !this.timeOrder.equals(timeOrder)) {
            trackUpdatedField("ORDER_TIME", this.timeOrder.toString());
        }
        this.timeOrder = timeOrder;
    }

    @ActiveReflection
    public void setOrderStatus(OrderStatus orderStatus) {
        if (this.orderStatus != null && !Objects.requireNonNull(this.orderStatus.getId()).equals(orderStatus.getId())) {
            trackUpdatedField("ORDER_STATUS_ID", Objects.requireNonNull(this.orderStatus.getId()).toString());
        }
        this.orderStatus = orderStatus;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        if (this.customer != null && !this.customer.getId().equals(customer.getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId());
        }
        this.customer = customer;
    }

    @ActiveReflection
    public void setOrderDetails(Collection<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @ActiveReflection
    public void setBankTransactionCode(String bankTransactionCode) {
        if (this.bankTransactionCode != null && !this.bankTransactionCode.equals(bankTransactionCode)) {
            trackUpdatedField("BANK_TRANSACTION_CODE", this.bankTransactionCode);
        }
        this.bankTransactionCode = bankTransactionCode;
    }

    @ActiveReflection
    public void setOrderHistory(OrderHistory orderHistory) {
        this.orderHistory = orderHistory;
    }

    public static EntityMapper<Order> getMapper() {
        return new OrderMapper();
    }

    private static class OrderMapper implements EntityMapper<Order> {

        @Override
        public Order map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Order().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Order map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Order result = new Order();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("ORDERS")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = OrderStatus.getMapper();
                if (mapper.canMap(resultSet)) {
                    var orderStatus = mapper.map(resultSet);
                    orderStatus.setOrder(result);
                    result.setOrderStatus(orderStatus);
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = OrderHistory.getMapper();
                if (mapper.canMap(resultSet)) {
                    var orderHistory = mapper.map(resultSet);
                    orderHistory.setOrder(result);
                    result.setOrderHistory(orderHistory);
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                if (mapper.canMap(resultSet)) {
                    var customer = mapper.map(resultSet);
                    customer.getOrders().add(result);
                    result.setCustomer(customer);
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = OrderDetail.getMapper();
                Set<OrderDetail> orderDetailSet = new LinkedHashSet<>();
                while (!resultSet.isEmpty()) {
                    hook = resultSet.peek();
                    if (hook.columnName().split("\\.")[0].equals("ORDER_DETAIL")) {
                        var orderDetails = mapper.map(resultSet);
                        orderDetails.setOrder(result);
                        orderDetailSet.add(orderDetails);
                    } else {
                        break;
                    }
                }
                result.setOrderDetails(orderDetailSet);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("ORDERS");
            });
        }

        private void setData(Order target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "ORDERS.ID" -> target.setId(mappingObject.value());
                case "ORDERS.ORDER_TIME" -> target.setTimeOrder((Timestamp) mappingObject.value());
                case "ORDERS.BANK_TRANSACTION_CODE" -> target.setBankTransactionCode((String) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}