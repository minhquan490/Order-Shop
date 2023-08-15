package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Entity
@Table(name = "CART", indexes = @Index(name = "idx_cart_customer", columnList = "CUSTOMER_ID"))
@Label("CRT-")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Getter
public class Cart extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, columnDefinition = "varchar(32)")
    private String id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", unique = true, nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "cart", orphanRemoval = true)
    private Collection<CartDetail> cartDetails = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "PRODUCT_CART",
            joinColumns = @JoinColumn(name = "CART_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID"),
            indexes = @Index(name = "idx_product_cart", columnList = "CART_ID")
    )
    private Collection<Product> products = new HashSet<>();

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of cart must be string");
        }
        this.id = (String) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<Cart> results = new LinkedList<>();
            Cart first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Cart) entity;
                } else {
                    Cart casted = (Cart) entity;
                    if (casted.getProducts().isEmpty() && casted.getCartDetails().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getCartDetails().addAll(casted.getCartDetails());
                        first.getProducts().addAll(casted.getProducts());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    @ActiveReflection
    public void setCustomer(@NonNull Customer customer) {
        if (this.customer != null && !this.customer.getId().equals(customer.getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId());
        }
        this.customer = customer;
    }

    @ActiveReflection
    public void setCartDetails(@NonNull Collection<CartDetail> cartDetails) {
        this.cartDetails = cartDetails;
    }

    @ActiveReflection
    public void setProducts(@NonNull Collection<Product> products) {
        this.products = products;
    }

    public static EntityMapper<Cart> getMapper() {
        return new CartMapper();
    }

    private static class CartMapper implements EntityMapper<Cart> {

        @Override
        public Cart map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Cart().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Cart map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Cart result = new Cart();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("CART")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                var customer = mapper.map(resultSet);
                customer.setCart(result);
                result.setCustomer(customer);
            }
            if (!resultSet.isEmpty()) {
                assignCartDetails(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignProducts(resultSet, result);
            }
            return result;
        }

        private void setData(Cart target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "CART.ID" -> target.setId(mappingObject.value());
                case "CART.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "CART.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "CART.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "CART.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }

        private void assignCartDetails(Queue<MappingObject> resultSet, Cart result) {
            MappingObject hook = resultSet.peek();
            var mapper = CartDetail.getMapper();
            Set<CartDetail> cartDetailSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                if (hook.columnName().startsWith("CART_DETAIL")) {
                    var cartDetail = mapper.map(resultSet);
                    cartDetail.setCart(result);
                    cartDetailSet.add(cartDetail);
                } else {
                    break;
                }
            }
            result.setCartDetails(cartDetailSet);
        }

        private void assignProducts(Queue<MappingObject> resultSet, Cart result) {
            MappingObject hook = resultSet.peek();
            var mapper = Product.getMapper();
            Set<Product> productSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                if (hook.columnName().startsWith("PRODUCT")) {
                    var product = mapper.map(resultSet);
                    product.getCarts().add(result);
                    productSet.add(product);
                } else {
                    break;
                }
            }
            result.setProducts(productSet);
        }
    }
}
