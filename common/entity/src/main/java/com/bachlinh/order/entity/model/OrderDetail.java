package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "ORDER_DETAIL", indexes = @Index(name = "idx_order", columnList = "ORDER_ID"))
@ActiveReflection
public class OrderDetail extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, unique = true, columnDefinition = "int")
    private Integer id;

    @Column(name = "AMOUNT", nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "ORDER_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    private Order order;

    @ActiveReflection
    protected OrderDetail() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof Integer)) {
            throw new PersistenceException("Id of OrderDetail must be int");
        }
        this.id = (Integer) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setAmount(Integer amount) {
        if (this.amount != null && !this.amount.equals(amount)) {
            trackUpdatedField("AMOUNT", this.amount, amount);
        }
        this.amount = amount;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        this.product = product;
    }

    @ActiveReflection
    public void setOrder(Order order) {
        this.order = order;
    }

    public Integer getId() {
        return this.id;
    }

    public Integer getAmount() {
        return this.amount;
    }

    public Product getProduct() {
        return this.product;
    }

    public Order getOrder() {
        return this.order;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof OrderDetail other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$amount = this.getAmount();
        final Object other$amount = other.getAmount();
        return Objects.equals(this$amount, other$amount);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof OrderDetail;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $amount = this.getAmount();
        result = result * PRIME + ($amount == null ? 43 : $amount.hashCode());
        return result;
    }
}
