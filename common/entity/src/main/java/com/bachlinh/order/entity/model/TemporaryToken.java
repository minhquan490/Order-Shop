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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(name = "TEMPORARY_TOKEN", indexes = @Index(name = "idx_temporary_token_value", columnList = "VALUE"))
@Getter
@ActiveReflection
@Label("TPT-")
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@EqualsAndHashCode(callSuper = true)
public class TemporaryToken extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", unique = true, updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private Integer id;

    @Column(name = "VALUE", unique = true, nullable = false)
    private String value;

    @Column(name = "EXPIRY_TIME", nullable = false)
    private Timestamp expiryTime;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "temporaryToken")
    @EqualsAndHashCode.Exclude
    private Customer assignCustomer;

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
}
