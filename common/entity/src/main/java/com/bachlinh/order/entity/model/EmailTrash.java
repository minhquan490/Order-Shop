package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.google.common.base.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

@ActiveReflection
@Entity
@Table(name = "EMAIL_TRASH", indexes = @Index(name = "inx_email_trash_customer", columnList = "CUSTOMER_ID"))
public class EmailTrash extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, unique = true)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emailTrash")
    private Set<Email> emails = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, unique = true, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer customer;

    @ActiveReflection
    protected EmailTrash() {
    }

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
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<EmailTrash> results = new LinkedList<>();
            EmailTrash first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (EmailTrash) entity;
                } else {
                    EmailTrash casted = (EmailTrash) entity;
                    if (casted.getEmails().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getEmails().addAll(casted.getEmails());
                    }
                }
            }
            results.addFirst(first);
            return (Collection<U>) results;
        }
    }

    public Integer getId() {
        return this.id;
    }

    public Set<Email> getEmails() {
        return this.emails;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailTrash that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getEmails(), that.getEmails()) && Objects.equal(getCustomer(), that.getCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getEmails(), getCustomer());
    }
}
