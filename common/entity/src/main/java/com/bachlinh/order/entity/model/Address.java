package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

@Label("ADR-")
@Entity
@Table(name = "ADDRESS")
@Validator(validators = "com.bachlinh.order.core.entity.validator.internal.AddressValidator")
@ActiveReflection
public class Address extends AbstractEntity {

    @Id
    @Column(name = "ID", nullable = false, columnDefinition = "varchar(32)", updatable = false)
    private String id;

    @Column(name = "VALUE", nullable = false, columnDefinition = "nvarchar")
    private String value;

    @Column(name = "CITY", nullable = false, columnDefinition = "nvarchar")
    private String city;

    @Column(name = "COUNTRY", columnDefinition = "nvarchar")
    private String country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;

    @ActiveReflection
    Address() {
    }

    @Override
    @ActiveReflection
    public Object getId() {
        return id;
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Address entity must be string");
        }
        this.id = (String) id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return Objects.equal(getId(), address.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @ActiveReflection
    public String getValue() {
        return this.value;
    }

    @ActiveReflection
    public String getCity() {
        return this.city;
    }

    @ActiveReflection
    public String getCountry() {
        return this.country;
    }

    @ActiveReflection
    public Customer getCustomer() {
        return this.customer;
    }

    @ActiveReflection
    public void setValue(String value) {
        this.value = value;
    }

    @ActiveReflection
    public void setCity(String city) {
        this.city = city;
    }

    @ActiveReflection
    public void setCountry(String country) {
        this.country = country;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
