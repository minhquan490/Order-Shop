package com.bachlinh.order.entity.model;

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
import java.util.Objects;

@Entity
@Table(name = "CART_DETAIL", indexes = @Index(name = "idx_cart_cartDetail", columnList = "CART_ID"))
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
public class CartDetail extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", columnDefinition = "int", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "AMOUNT", columnDefinition = "int", nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CART_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Cart cart;

    @ActiveReflection
    protected CartDetail() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of cart must be int");
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
        if (this.product != null && !Objects.requireNonNull(this.product.getId()).equals(Objects.requireNonNull(product).getId())) {
            trackUpdatedField("PRODUCT_ID", this.product.getId(), product.getId());
        }
        this.product = product;
    }

    @ActiveReflection
    public void setCart(Cart cart) {
        if (this.cart != null && !Objects.requireNonNull(this.cart.getId()).equals(Objects.requireNonNull(cart).getId())) {
            trackUpdatedField("CART_ID", this.cart.getId(), cart.getId());
        }
        this.cart = cart;
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

    public Cart getCart() {
        return this.cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartDetail that)) return false;
        if (!super.equals(o)) return false;
        return com.google.common.base.Objects.equal(getId(), that.getId()) && com.google.common.base.Objects.equal(getAmount(), that.getAmount()) && com.google.common.base.Objects.equal(getProduct(), that.getProduct()) && com.google.common.base.Objects.equal(getCart(), that.getCart());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getAmount(), getProduct(), getCart());
    }
}
