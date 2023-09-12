package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Collection;
import java.util.Objects;

@Label("ADR-")
@Entity
@Table(name = "ADDRESS", indexes = @Index(name = "idx_address_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@EqualsAndHashCode(callSuper = true)
@Getter
public class Address extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", nullable = false, columnDefinition = "varchar(32)", updatable = false, unique = true)
    private String id;

    @Column(name = "VALUE", nullable = false, columnDefinition = "nvarchar(266)")
    private String value;

    @Column(name = "CITY", nullable = false, columnDefinition = "nvarchar(266)")
    private String city;

    @Column(name = "COUNTRY", columnDefinition = "nvarchar(266)")
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Address entity must be string");
        }
        this.id = (String) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setValue(String value) {
        if (this.value != null && !Objects.requireNonNull(value).equals(this.value)) {
            trackUpdatedField("VALUE", this.value, value);
        }
        this.value = value;
    }

    @ActiveReflection
    public void setCity(String city) {
        if (this.city != null && !Objects.requireNonNull(city).equals(this.city)) {
            trackUpdatedField("CITY", this.city, value);
        }
        this.city = city;
    }

    @ActiveReflection
    public void setCountry(String country) {
        if (this.country != null && !Objects.requireNonNull(country).equals(this.country)) {
            trackUpdatedField("COUNTRY", this.country, value);
        }
        this.country = country;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        if (this.customer != null && !this.customer.getId().equals(customer.getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId(), customer.getId());
        }
        this.customer = customer;
    }
}
