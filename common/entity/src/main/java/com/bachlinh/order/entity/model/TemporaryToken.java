package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
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
import java.util.Objects;

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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof TemporaryToken other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$value = this.getValue();
        final Object other$value = other.getValue();
        if (!Objects.equals(this$value, other$value)) return false;
        final Object this$expiryTime = this.getExpiryTime();
        final Object other$expiryTime = other.getExpiryTime();
        return Objects.equals(this$expiryTime, other$expiryTime);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof TemporaryToken;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $value = this.getValue();
        result = result * PRIME + ($value == null ? 43 : $value.hashCode());
        final Object $expiryTime = this.getExpiryTime();
        result = result * PRIME + ($expiryTime == null ? 43 : $expiryTime.hashCode());
        return result;
    }
}
