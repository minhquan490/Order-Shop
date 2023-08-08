package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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

@Label("ADR-")
@Entity
@Table(name = "ADDRESS")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
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
    private Customer customer;

    @Override
    @ActiveReflection
    public void setId(@NonNull Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Address entity must be string");
        }
        this.id = (String) id;
    }

    @ActiveReflection
    public void setValue(@NonNull String value) {
        if (this.value != null && !value.equals(this.value)) {
            trackUpdatedField("VALUE", this.value);
        }
        this.value = value;
    }

    @ActiveReflection
    public void setCity(@NonNull String city) {
        if (this.city != null && !city.equals(this.city)) {
            trackUpdatedField("CITY", this.city);
        }
        this.city = city;
    }

    @ActiveReflection
    public void setCountry(@NonNull String country) {
        if (this.country != null && !country.equals(this.country)) {
            trackUpdatedField("COUNTRY", this.country);
        }
        this.country = country;
    }

    @ActiveReflection
    public void setCustomer(@NonNull Customer customer) {
        if (this.customer != null && !this.customer.getId().equals(customer.getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId());
        }
        this.customer = customer;
    }
}
