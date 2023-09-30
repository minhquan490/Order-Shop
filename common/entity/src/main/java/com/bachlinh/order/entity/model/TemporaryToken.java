package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Label;
import com.google.common.base.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "TEMPORARY_TOKEN", indexes = @Index(name = "idx_temporary_token_value", columnList = "VALUE"))
@ActiveReflection
@Label("TPT-")
public class TemporaryToken extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private Integer id;

    @Column(name = "VALUE", unique = true, nullable = false)
    private String value;

    @Column(name = "EXPIRY_TIME", nullable = false)
    private Timestamp expiryTime;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "temporaryToken")
    private Customer assignCustomer;

    @ActiveReflection
    protected TemporaryToken() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of Temporary token must be integer");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setValue(String value) {
        if (this.value != null && !this.value.equals(value)) {
            trackUpdatedField("VALUE", this.value, value);
        }
        this.value = value;
    }

    @ActiveReflection
    public void setExpiryTime(Timestamp expiryTime) {
        if (this.expiryTime != null && !this.expiryTime.equals(expiryTime)) {
            trackUpdatedField("EXPIRY_TIME", this.expiryTime, expiryTime);
        }
        this.expiryTime = expiryTime;
    }

    @ActiveReflection
    public void setAssignCustomer(Customer assignCustomer) {
        this.assignCustomer = assignCustomer;
    }

    public Integer getId() {
        return this.id;
    }

    public String getValue() {
        return this.value;
    }

    public Timestamp getExpiryTime() {
        return this.expiryTime;
    }

    public Customer getAssignCustomer() {
        return this.assignCustomer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TemporaryToken token)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), token.getId()) && Objects.equal(getValue(), token.getValue()) && Objects.equal(getExpiryTime(), token.getExpiryTime()) && Objects.equal(getAssignCustomer(), token.getAssignCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getValue(), getExpiryTime(), getAssignCustomer());
    }
}
