package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Table(name = "ORDER_HISTORY")
@ActiveReflection
@Validator(validators = "com.bachlinh.order.validate.validator.internal.OrderHistoryValidator")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
public class OrderHistory extends AbstractEntity {

    @Id
    @Column(name = "ID", updatable = false, columnDefinition = "int")
    private Integer id;

    @Column(name = "ORDER_TIME", updatable = false, nullable = false)
    private Timestamp orderTime;

    @Column(name = "ORDER_STATUS", nullable = false, length = 30)
    private String orderStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @MapsId
    private Order order;

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (id instanceof Integer) {
            this.id = (int) id;
            return;
        }
        throw new PersistenceException("Can not set id for order history, supported only integer");
    }

    @ActiveReflection
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @ActiveReflection
    public Order getOrder() {
        return this.order;
    }

    @ActiveReflection
    public void setOrderTime(Timestamp orderTime) {
        this.orderTime = orderTime;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderHistory that)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getOrderTime(), that.getOrderTime()) && Objects.equal(getOrderStatus(), that.getOrderStatus()) && Objects.equal(getOrder(), that.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getOrderTime(), getOrderStatus(), getOrder());
    }
}