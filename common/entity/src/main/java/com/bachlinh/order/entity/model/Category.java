package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Label;
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
import jakarta.persistence.UniqueConstraint;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "CATEGORY", indexes = @Index(name = "idx_category_name", columnList = "NAME", unique = true))
@Label("CTR-")
@ActiveReflection
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
    private Set<Product> products = new HashSet<>();

    @ActiveReflection
    protected Category() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of category must be string");
        }
        this.id = (String) id;
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
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name, name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Set<Product> getProducts() {
        return this.products;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Category other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        return Objects.equals(this$name, other$name);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Category;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        return result;
    }
}
