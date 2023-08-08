package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;

@Entity
@Table(name = "REFRESH_TOKEN", indexes = @Index(name = "idx_token_value", columnList = "VALUE"))
@Label("RFT-")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
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
    private Customer customer;

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (!(id instanceof String)) {
            throw new PersistenceException("Id of refresh token is only string");
        }
        this.id = (String) id;
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
