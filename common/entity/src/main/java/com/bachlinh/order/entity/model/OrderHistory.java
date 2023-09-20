package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "ORDER_HISTORY")
@ActiveReflection
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OrderHistory other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$orderTime = this.getOrderTime();
        final Object other$orderTime = other.getOrderTime();
        if (!Objects.equals(this$orderTime, other$orderTime)) return false;
        final Object this$orderStatus = this.getOrderStatus();
        final Object other$orderStatus = other.getOrderStatus();
        return Objects.equals(this$orderStatus, other$orderStatus);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OrderHistory;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $orderTime = this.getOrderTime();
        result = result * PRIME + ($orderTime == null ? 43 : $orderTime.hashCode());
        final Object $orderStatus = this.getOrderStatus();
        result = result * PRIME + ($orderStatus == null ? 43 : $orderStatus.hashCode());
        return result;
    }
}