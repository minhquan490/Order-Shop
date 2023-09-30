package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.util.Collection;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderStatus that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getStatus(), that.getStatus()) && Objects.equal(getOrder(), that.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getStatus(), getOrder());
    }
}