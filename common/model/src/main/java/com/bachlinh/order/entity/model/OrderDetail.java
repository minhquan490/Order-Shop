package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
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

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.util.Collection;

@Entity
@Table(name = "ORDER_DETAIL", indexes = @Index(name = "idx_order", columnList = "ORDER_ID"))
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderDetail that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getAmount(), that.getAmount()) && Objects.equal(getProduct(), that.getProduct()) && Objects.equal(getOrder(), that.getOrder());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getAmount(), getProduct(), getOrder());
    }
}
