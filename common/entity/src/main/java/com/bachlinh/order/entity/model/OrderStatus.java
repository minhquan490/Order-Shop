package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "ORDER_STATUS")
@ActiveReflection
public class OrderStatus extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", nullable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "STATUS", nullable = false, length = 30)
    private String status;

    @OneToOne(optional = false, mappedBy = "orderStatus", fetch = FetchType.LAZY)
    private Order order;

    @ActiveReflection
    protected OrderStatus() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of OrderStatus must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setStatus(String status) {
        if (this.status != null && !this.status.equals(status)) {
            trackUpdatedField("STATUS", this.status, status);
        }
        this.status = status;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getId() {
        return this.id;
    }

    public String getStatus() {
        return this.status;
    }

    public Order getOrder() {
        return this.order;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OrderStatus other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        return Objects.equals(this$status, other$status);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OrderStatus;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 43 : $status.hashCode());
        return result;
    }
}