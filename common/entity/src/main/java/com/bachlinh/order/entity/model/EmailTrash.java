package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Validator;
import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@ActiveReflection
@Entity
@Table(name = "EMAIL_TRASH")
@Getter
@Validator(validators = "com.bachlinh.order.validate.validator.internal.EmailTrashValidator")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
public class EmailTrash extends AbstractEntity {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, unique = true)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emailTrash")
    private Set<Email> emails = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, unique = true, updatable = false)
    private Customer customer;

    @ActiveReflection
    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
        } else {
            throw new PersistenceException("Id of EmailTrash must be int");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailTrash that)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getEmails(), that.getEmails()) && Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getEmails(), getCustomer());
    }
}
