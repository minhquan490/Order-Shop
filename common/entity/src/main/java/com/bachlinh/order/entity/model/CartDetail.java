package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
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
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "PRODUCT_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "CART_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
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

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
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
        if (this.product != null && !Objects.requireNonNull(this.product.getId()).equals(product.getId())) {
            trackUpdatedField("PRODUCT_ID", this.product.getId());
        }
        this.product = product;
    }

    @ActiveReflection
    public void setCart(@NonNull Cart cart) {
        if (this.cart != null && !Objects.requireNonNull(this.cart.getId()).equals(cart.getId())) {
            trackUpdatedField("CART_ID", this.cart.getId());
        }
        this.cart = cart;
    }

    public static EntityMapper<CartDetail> getMapper() {
        return new CartDetailMapper();
    }

    private static class CartDetailMapper implements EntityMapper<CartDetail> {

        @Override
        public CartDetail map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new CartDetail().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public CartDetail map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            CartDetail result = new CartDetail();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("CART_DETAIL")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Product.getMapper();
                if (mapper.canMap(resultSet)) {
                    var product = mapper.map(resultSet);
                    result.setProduct(product);
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Cart.getMapper();
                if (mapper.canMap(resultSet)) {
                    var cart = mapper.map(resultSet);
                    cart.getCartDetails().add(result);
                    result.setCart(cart);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("CART_DETAIL");
            });
        }

        private void setData(CartDetail target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "CART_DETAIL.ID" -> target.setId(mappingObject.value());
                case "CART_DETAIL.AMOUNT" -> target.setAmount((Integer) mappingObject.value());
                case "CART_DETAIL.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "CART_DETAIL.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "CART_DETAIL.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "CART_DETAIL.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
