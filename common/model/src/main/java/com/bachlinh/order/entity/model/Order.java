package com.bachlinh.order.entity.model;

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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;

@Entity
@Table(name = "ORDERS", indexes = @Index(name = "idx_order_customer", columnList = "CUSTOMER_ID"))
@Label("ODR-")
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
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
    private OrderStatus orderStatus;

    @OneToOne(mappedBy = "order")
    @Fetch(FetchMode.JOIN)
    private OrderHistory orderHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order", orphanRemoval = true)
    private Collection<OrderDetail> orderDetails = new HashSet<>();

    @ActiveReflection
    protected Order() {
    }

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
            trackUpdatedField("ORDER_TIME", this.timeOrder, timeOrder);
        }
        this.timeOrder = timeOrder;
    }

    @ActiveReflection
    public void setOrderStatus(OrderStatus orderStatus) {
        if (this.orderStatus != null && !Objects.requireNonNull(this.orderStatus.getId()).equals(orderStatus.getId())) {
            trackUpdatedField("ORDER_STATUS_ID", this.orderStatus.getId(), orderStatus.getId());
        }
        this.orderStatus = orderStatus;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        if (this.customer != null && !Objects.requireNonNull(this.customer.getId()).equals(customer.getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId(), customer.getId());
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
            trackUpdatedField("BANK_TRANSACTION_CODE", this.bankTransactionCode, bankTransactionCode);
        }
        this.bankTransactionCode = bankTransactionCode;
    }

    @ActiveReflection
    public void setOrderHistory(OrderHistory orderHistory) {
        this.orderHistory = orderHistory;
    }

    public String getId() {
        return this.id;
    }

    public Timestamp getTimeOrder() {
        return this.timeOrder;
    }

    public String getBankTransactionCode() {
        return this.bankTransactionCode;
    }

    public OrderStatus getOrderStatus() {
        return this.orderStatus;
    }

    public OrderHistory getOrderHistory() {
        return this.orderHistory;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Collection<OrderDetail> getOrderDetails() {
        return this.orderDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        if (!super.equals(o)) return false;
        return com.google.common.base.Objects.equal(getId(), order.getId()) && com.google.common.base.Objects.equal(getTimeOrder(), order.getTimeOrder()) && com.google.common.base.Objects.equal(getBankTransactionCode(), order.getBankTransactionCode()) && com.google.common.base.Objects.equal(getOrderStatus(), order.getOrderStatus()) && com.google.common.base.Objects.equal(getOrderHistory(), order.getOrderHistory()) && com.google.common.base.Objects.equal(getCustomer(), order.getCustomer()) && com.google.common.base.Objects.equal(getOrderDetails(), order.getOrderDetails());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getTimeOrder(), getBankTransactionCode(), getOrderStatus(), getOrderHistory(), getCustomer(), getOrderDetails());
    }
}