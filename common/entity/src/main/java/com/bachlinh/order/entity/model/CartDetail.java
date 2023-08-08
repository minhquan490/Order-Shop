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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.NonNull;

@Entity
@Table(name = "CART_DETAIL", indexes = @Index(name = "idx_cart_cartDetail", columnList = "CART_ID"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class CartDetail extends AbstractEntity<Integer> {

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
    public void setId(@NonNull Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of cart must be int");
    }

    @ActiveReflection
    public void setAmount(@NonNull Integer amount) {
        if (this.amount != null && !this.amount.equals(amount)) {
            trackUpdatedField("AMOUNT", this.amount.toString());
        }
        this.amount = amount;
    }

    @ActiveReflection
    public void setProduct(@NonNull Product product) {
        if (this.product != null && !this.product.getId().equals(product.getId())) {
            trackUpdatedField("PRODUCT_ID", this.product.getId());
        }
        this.product = product;
    }

    @ActiveReflection
    public void setCart(@NonNull Cart cart) {
        if (this.cart != null && !this.cart.getId().equals(cart.getId())) {
            trackUpdatedField("CART_ID", this.cart.getId());
        }
        this.cart = cart;
    }
}
