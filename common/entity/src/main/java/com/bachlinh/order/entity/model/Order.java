package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Trigger;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "ORDERS", indexes = @Index(name = "idx_order_customer", columnList = "CUSTOMER_ID"))
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "order")
@Label("ODR-")
@Validator(validators = "com.bachlinh.order.validate.validator.internal.OrderValidator")
@Trigger(triggers = {"com.bachlinh.order.trigger.internal.OrderHistoryTrigger", "com.bachlinh.order.trigger.internal.NewOrderPushingTrigger"})
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
public class Order extends AbstractEntity {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "ORDER_TIME", nullable = false)
    private Timestamp timeOrder;

    @Column(name = "BANK_TRANSACTION_CODE")
    private String bankTransactionCode;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORDER_STATUS_ID", unique = true, nullable = false, updatable = false)
    private OrderStatus orderStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false)
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "order", orphanRemoval = true)
    private Collection<OrderDetail> orderDetails = new HashSet<>();

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of order must be string");
        }
        this.id = (String) id;
    }

    public boolean isDeposited() {
        return getBankTransactionCode().isEmpty();
    }

    @ActiveReflection
    public void setTimeOrder(Timestamp timeOrder) {
        this.timeOrder = timeOrder;
    }

    @ActiveReflection
    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @ActiveReflection
    public void setOrderDetails(Collection<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @ActiveReflection
    public void setBankTransactionCode(String bankTransactionCode) {
        this.bankTransactionCode = bankTransactionCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order order)) return false;
        return Objects.equal(getId(), order.getId()) && Objects.equal(getTimeOrder(), order.getTimeOrder()) && Objects.equal(getBankTransactionCode(), order.getBankTransactionCode()) && Objects.equal(getOrderStatus(), order.getOrderStatus()) && Objects.equal(getCustomer(), order.getCustomer()) && Objects.equal(getOrderDetails(), order.getOrderDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getTimeOrder(), getBankTransactionCode(), getOrderStatus(), getCustomer(), getOrderDetails());
    }
}