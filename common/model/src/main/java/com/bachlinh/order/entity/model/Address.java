package com.bachlinh.order.entity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.util.Collection;
import java.util.Objects;

@Label("ADR-")
@Entity
@Table(name = "ADDRESS", indexes = @Index(name = "idx_address_customer", columnList = "CUSTOMER_ID"))
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
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
    private Customer customer;

    @ActiveReflection
    protected Address() {
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
        if (this.customer != null &&
                this.customer.getId() != null &&
                !Objects.requireNonNull(this.customer.getId()).equals(customer.getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId(), customer.getId());
        }
        this.customer = customer;
    }

    public String getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountry() {
        return this.country;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Address address = (Address) o;
        return com.google.common.base.Objects.equal(getId(), address.getId()) && com.google.common.base.Objects.equal(getValue(), address.getValue()) && com.google.common.base.Objects.equal(getCity(), address.getCity()) && com.google.common.base.Objects.equal(getCountry(), address.getCountry()) && com.google.common.base.Objects.equal(getCustomer(), address.getCustomer());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getValue(), getCity(), getCountry(), getCustomer());
    }
}
