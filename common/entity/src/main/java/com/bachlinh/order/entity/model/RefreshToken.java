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
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;

import java.sql.Timestamp;

@Entity
@Table(name = "REFRESH_TOKEN", indexes = @Index(name = "idx_token_value", columnList = "VALUE"))
@Label("RFT-")
@ActiveReflection
public class RefreshToken extends AbstractEntity {

    @Id
    @Column(name = "ID", unique = true, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "TIME_CREATED", nullable = false)
    private Timestamp timeCreated;

    @Column(name = "TIME_EXPIRED", nullable = false)
    private Timestamp timeExpired;

    @Column(name = "VALUE", nullable = false, unique = true, updatable = false, length = 100)
    private String refreshTokenValue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", unique = true, nullable = false, updatable = false)
    private Customer customer;

    @ActiveReflection
    RefreshToken() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RefreshToken that)) return false;
        return Objects.equal(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
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

    @ActiveReflection
    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    @ActiveReflection
    public void setTimeExpired(Timestamp timeExpired) {
        this.timeExpired = timeExpired;
    }

    @ActiveReflection
    public void setRefreshTokenValue(String refreshTokenValue) {
        this.refreshTokenValue = refreshTokenValue;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
