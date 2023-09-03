package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import jakarta.persistence.Tuple;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
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
@Table(name = "CATEGORY", indexes = @Index(name = "idx_category_name", columnList = "NAME", unique = true))
@Label("CTR-")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "category")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode(callSuper = true)
public class Category extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", unique = true, nullable = false, columnDefinition = "nvarchar(60)")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "PRODUCT_CATEGORY",
            joinColumns = @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID"),
            inverseJoinColumns = @JoinColumn(name = "PRODUCT_ID", referencedColumnName = "ID"),
            indexes = @Index(name = "idx_product_category", columnList = "CATEGORY_ID, PRODUCT_ID"),
            uniqueConstraints = @UniqueConstraint(name = "udx_product_category_product", columnNames = "CATEGORY_ID")
    )
    @EqualsAndHashCode.Exclude
    private Set<Product> products = new HashSet<>();

    @Override
    @ActiveReflection
    public void setId(@NonNull Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of category must be string");
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
            Deque<Category> results = new LinkedList<>();
            Category first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (Category) entity;
                } else {
                    Category casted = (Category) entity;
                    if (casted.getProducts().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getProducts().addAll(casted.getProducts());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    @ActiveReflection
    public void setName(@NonNull String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name, name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public static EntityMapper<Category> getMapper() {
        return new CategoryMapper();
    }

    private static class CategoryMapper implements EntityMapper<Category> {

        @Override
        public Category map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Category().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Category map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Category result = new Category();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("CATEGORY")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Product.getMapper();
                Set<Product> productSet = new LinkedHashSet<>();
                while (!resultSet.isEmpty()) {
                    hook = resultSet.peek();
                    if (hook.columnName().split("\\.")[0].equals("PRODUCT")) {
                        var product = mapper.map(resultSet);
                        product.getCategories().add(result);
                        productSet.add(product);
                    }
                }
                result.setProducts(productSet);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("CATEGORY");
            });
        }

        private void setData(Category target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "CATEGORY.ID" -> target.setId(mappingObject.value());
                case "CATEGORY.NAME" -> target.setName((String) mappingObject.value());
                case "CATEGORY.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "CATEGORY.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "CATEGORY.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "CATEGORY.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
