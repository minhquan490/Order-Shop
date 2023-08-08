package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

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
        if (this.timeOrder != null && !this.timeOrder.equals(timeOrder)) {
            trackUpdatedField("ORDER_TIME", this.timeOrder.toString());
        }
        this.timeOrder = timeOrder;
    }

    @ActiveReflection
    public void setOrderStatus(OrderStatus orderStatus) {
        if (this.orderStatus != null && !this.orderStatus.getId().equals(orderStatus.getId())) {
            trackUpdatedField("ORDER_STATUS_ID", this.orderStatus.getId().toString());
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
}