package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.Formula;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.formula.internal.ProductEnableFormula;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

@Entity
@Table(
        name = "PRODUCT",
        indexes = {
                @Index(name = "idx_product_name", columnList = "NAME", unique = true),
                @Index(name = "idx_product_created_date", columnList = "CREATED_DATE")
        }
)
@Label("PRD-")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "product")
@EnableFullTextSearch
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
@Formula(processor = ProductEnableFormula.class)
public class Product extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", unique = true, nullable = false, columnDefinition = "nvarchar(100)")
    @FullTextField
    @ActiveReflection
    private String name;

    @Column(name = "PRICE", nullable = false)
    private Integer price;

    @Column(name = "SIZE", length = 3, nullable = false)
    @FullTextField
    @ActiveReflection
    private String size;

    @Column(name = "COLOR", columnDefinition = "nvarchar(30)", nullable = false)
    @FullTextField
    @ActiveReflection
    private String color;

    @Column(name = "TAO_BAO_URL")
    private String taobaoUrl;

    @Column(name = "DESCRIPTION", columnDefinition = "nvarchar(400)")
    private String description;

    @Column(name = "ORDER_POINT")
    private Integer orderPoint;

    @Column(name = "ENABLED", columnDefinition = "bit", nullable = false)
    private boolean enabled = true;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "product", orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private Collection<ProductMedia> medias = new HashSet<>();

    @ManyToMany(mappedBy = "products")
    @EqualsAndHashCode.Exclude
    private Collection<Category> categories = new HashSet<>();

    @ManyToMany(mappedBy = "products")
    @EqualsAndHashCode.Exclude
    private Collection<Cart> carts = new HashSet<>();

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of product must be string");
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
            Deque<Product> results = new LinkedList<>();
            Product first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Product) entity;
                } else {
                    Product casted = (Product) entity;
                    if (casted.getCategories().isEmpty() && casted.getMedias().isEmpty() && casted.getCarts().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getCategories().addAll(casted.getCategories());
                        first.getMedias().addAll(casted.getMedias());
                        first.getCarts().addAll(casted.getCarts());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setPrice(Integer price) {
        if (this.price != null && !this.price.equals(price)) {
            trackUpdatedField("PRICE", this.price.toString());
        }
        this.price = price;
    }

    @ActiveReflection
    public void setSize(String size) {
        if (this.size != null && !this.size.equals(size)) {
            trackUpdatedField("SIZE", this.size);
        }
        this.size = size;
    }

    @ActiveReflection
    public void setColor(String color) {
        if (this.color != null && !this.color.equals(color)) {
            trackUpdatedField("COLOR", this.color);
        }
        this.color = color;
    }

    @ActiveReflection
    public void setTaobaoUrl(String taobaoUrl) {
        if (this.taobaoUrl != null && !this.taobaoUrl.equals(taobaoUrl)) {
            trackUpdatedField("TAO_BAO_URL", this.taobaoUrl);
        }
        this.taobaoUrl = taobaoUrl;
    }

    @ActiveReflection
    public void setDescription(String description) {
        if (this.description != null && !this.description.equals(description)) {
            trackUpdatedField("DESCRIPTION", this.description);
        }
        this.description = description;
    }

    @ActiveReflection
    public void setOrderPoint(Integer orderPoint) {
        if (this.orderPoint != null && !this.orderPoint.equals(orderPoint)) {
            trackUpdatedField("ORDER_POINT", this.orderPoint.toString());
        }
        this.orderPoint = orderPoint;
    }

    @ActiveReflection
    public void setEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            trackUpdatedField("ENABLED", String.valueOf(enabled));
        }
        this.enabled = enabled;
    }

    @ActiveReflection
    public void setMedias(Set<ProductMedia> medias) {
        this.medias = medias;
    }

    @ActiveReflection
    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @ActiveReflection
    public void setCarts(Set<Cart> carts) {
        this.carts = carts;
    }

    public static EntityMapper<Product> getMapper() {
        return new ProductMapper();
    }

    private static class ProductMapper implements EntityMapper<Product> {

        @Override
        public Product map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Product().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Product map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Product result = new Product();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("PRODUCT")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                assignMedias(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignCategories(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignCarts(resultSet, result);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("PRODUCT");
            });
        }

        private void setData(Product target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "PRODUCT.ID" -> target.setId(mappingObject.value());
                case "PRODUCT.NAME" -> target.setName((String) mappingObject.value());
                case "PRODUCT.PRICE" -> target.setPrice((Integer) mappingObject.value());
                case "PRODUCT.SIZE" -> target.setSize((String) mappingObject.value());
                case "PRODUCT.COLOR" -> target.setColor((String) mappingObject.value());
                case "PRODUCT.TAO_BAO_URL" -> target.setTaobaoUrl((String) mappingObject.value());
                case "PRODUCT.DESCRIPTION" -> target.setDescription((String) mappingObject.value());
                case "PRODUCT.ORDER_POINT" -> target.setOrderPoint((Integer) mappingObject.value());
                case "PRODUCT.ENABLED" -> target.setEnabled((Boolean) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }

        private void assignMedias(Queue<MappingObject> resultSet, Product result) {
            var mapper = ProductMedia.getMapper();
            Set<ProductMedia> productMediaSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("PRODUCT_MEDIA")) {
                    var productMedia = mapper.map(resultSet);
                    productMedia.setProduct(result);
                    productMediaSet.add(productMedia);
                } else {
                    break;
                }
            }
            result.setMedias(productMediaSet);
        }

        private void assignCategories(Queue<MappingObject> resultSet, Product result) {
            var mapper = Category.getMapper();
            Set<Category> categorySet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("CATEGORY")) {
                    var category = mapper.map(resultSet);
                    category.getProducts().add(result);
                    categorySet.add(category);
                }
            }
            result.setCategories(categorySet);
        }

        private void assignCarts(Queue<MappingObject> resultSet, Product result) {
            var mapper = Cart.getMapper();
            Set<Cart> cartSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("CART")) {
                    var cart = mapper.map(resultSet);
                    cart.getProducts().add(result);
                    cartSet.add(cart);
                }
            }
            result.setCarts(cartSet);
        }
    }
}