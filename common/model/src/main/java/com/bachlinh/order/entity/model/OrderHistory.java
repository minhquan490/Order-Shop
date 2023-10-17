package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "ORDER_HISTORY")
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
public class OrderHistory extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, columnDefinition = "int")
    private Integer id;

    @Column(name = "ORDER_TIME", updatable = false, nullable = false)
    private Timestamp orderTime;

    @Column(name = "ORDER_STATUS", nullable = false, length = 30)
    private String orderStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ORDER_ID")
    @Fetch(FetchMode.JOIN)
    private Order order;

    @ActiveReflection
    protected OrderHistory() {
    }

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (id instanceof Integer) {
            this.id = (int) id;
            return;
        }
        throw new PersistenceException("Can not set id for order history, supported only integer");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setOrderStatus(String orderStatus) {
        if (this.orderStatus != null && this.orderStatus.equals(orderStatus)) {
            trackUpdatedField("ORDER_STATUS", this.orderStatus, orderStatus);
        }
        this.orderStatus = orderStatus;
    }

    @ActiveReflection
    public Order getOrder() {
        return this.order;
    }

    @ActiveReflection
    public void setOrderTime(Timestamp orderTime) {
        if (this.orderTime != null && !this.orderTime.equals(orderTime)) {
            trackUpdatedField("ORDER_TIME", this.orderTime, orderTime);
        }
        this.orderTime = orderTime;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getId() {
        return this.id;
    }

    public Timestamp getOrderTime() {
        return this.orderTime;
    }

    public String getOrderStatus() {
        return this.orderStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderHistory that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getOrderTime(), that.getOrderTime()) && Objects.equal(getOrderStatus(), that.getOrderStatus()) && Objects.equal(getOrder(), that.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getOrderTime(), getOrderStatus(), getOrder());
    }
}