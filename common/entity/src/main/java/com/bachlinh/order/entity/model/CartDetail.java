package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Validator;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CART_DETAIL", indexes = @Index(name = "idx_cart_cartDetail", columnList = "CART_ID"))
@ActiveReflection
@Validator(validators = "com.bachlinh.order.validate.validator.internal.CartDetailValidator")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
public class CartDetail extends AbstractEntity {

    @Id
    @Column(name = "ID", columnDefinition = "int", nullable = false, updatable = false)
    private Integer id;

    @Column(name = "AMOUNT", columnDefinition = "int", nullable = false)
    private Integer amount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CART_ID", nullable = false, updatable = false)
    private Cart cart;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of cart must be int");
    }

    @ActiveReflection
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @ActiveReflection
    public void setProduct(Product product) {
        this.product = product;
    }

    @ActiveReflection
    public void setCart(Cart cart) {
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartDetail that)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getAmount(), that.getAmount()) && Objects.equal(getProduct(), that.getProduct()) && Objects.equal(getCart(), that.getCart());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getAmount(), getProduct(), getCart());
    }
}
