package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
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
import java.util.Objects;
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

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof EmailTrash other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$emails = this.getEmails();
        final Object other$emails = other.getEmails();
        return Objects.equals(this$emails, other$emails);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EmailTrash;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $emails = this.getEmails();
        result = result * PRIME + ($emails == null ? 43 : $emails.hashCode());
        return result;
    }
}
