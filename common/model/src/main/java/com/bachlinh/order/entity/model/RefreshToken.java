package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "REFRESH_TOKEN", indexes = @Index(name = "idx_token_value", columnList = "VALUE"))
@Label("RFT-")
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
public class RefreshToken extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", unique = true, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "TIME_CREATED", nullable = false)
    private Timestamp timeCreated;

    @Column(name = "TIME_EXPIRED", nullable = false)
    private Timestamp timeExpired;

    @Column(name = "VALUE", nullable = false, length = 100)
    private String refreshTokenValue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", unique = true, nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @ActiveReflection
    protected RefreshToken() {
    }

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of refresh token is only string");
        }
        this.id = (String) id;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setTimeCreated(Timestamp timeCreated) {
        if (this.timeCreated != null && !Objects.equal(this.timeCreated, timeCreated)) {
            trackUpdatedField("TIME_CREATED", this.timeCreated, timeCreated);
        }
        this.timeCreated = timeCreated;
    }

    @ActiveReflection
    public void setTimeExpired(Timestamp timeExpired) {
        if (this.timeExpired != null && !Objects.equal(this.timeExpired, timeExpired)) {
            trackUpdatedField("TIME_EXPIRED", this.timeExpired, timeExpired);
        }
        this.timeExpired = timeExpired;
    }

    @ActiveReflection
    public void setRefreshTokenValue(String refreshTokenValue) {
        if (this.refreshTokenValue != null && !this.refreshTokenValue.equals(refreshTokenValue)) {
            trackUpdatedField("VALUE", this.refreshTokenValue, refreshTokenValue);
        }
        this.refreshTokenValue = refreshTokenValue;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getId() {
        return this.id;
    }

    public Timestamp getTimeCreated() {
        return this.timeCreated;
    }

    public Timestamp getTimeExpired() {
        return this.timeExpired;
    }

    public String getRefreshTokenValue() {
        return this.refreshTokenValue;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshToken that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getTimeCreated(), that.getTimeCreated()) && Objects.equal(getTimeExpired(), that.getTimeExpired()) && Objects.equal(getRefreshTokenValue(), that.getRefreshTokenValue()) && Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getTimeCreated(), getTimeExpired(), getRefreshTokenValue(), getCustomer());
    }
}
