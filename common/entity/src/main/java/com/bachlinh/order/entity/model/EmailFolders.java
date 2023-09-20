package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

@Label("EFR-")
@Entity
@Table(name = "EMAIL_FOLDER", indexes = {
        @Index(name = "idx_email_folder_owner", columnList = "OWNER_ID"),
        @Index(name = "idx_email_folder_name", columnList = "NAME")
})
@ActiveReflection
public class EmailFolders extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", updatable = false, unique = true, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", columnDefinition = "nvarchar(300)")
    private String name;

    @Column(name = "TIME_CREATED")
    private Timestamp timeCreated;

    @Column(name = "EMAIL_CLEAR_POLICY")
    private Integer emailClearPolicy = -1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "folder")
    private Set<Email> emails = new HashSet<>();

    @ActiveReflection
    protected EmailFolders() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of email folder must be string");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<EmailFolders> results = new LinkedList<>();
            EmailFolders first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (EmailFolders) entity;
                } else {
                    EmailFolders casted = (EmailFolders) entity;
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

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name, name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setTimeCreated(Timestamp timeCreated) {
        if (this.timeCreated != null && !this.timeCreated.equals(timeCreated)) {
            trackUpdatedField("TIME_CREATED", this.timeCreated, timeCreated);
        }
        this.timeCreated = timeCreated;
    }

    @ActiveReflection
    public void setEmailClearPolicy(Integer emailClearPolicy) {
        if (this.emailClearPolicy != null && !this.emailClearPolicy.equals(emailClearPolicy)) {
            trackUpdatedField("EMAIL_CLEAR_POLICY", this.emailClearPolicy, emailClearPolicy);
        }
        this.emailClearPolicy = emailClearPolicy;
    }

    @ActiveReflection
    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    @ActiveReflection
    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Timestamp getTimeCreated() {
        return this.timeCreated;
    }

    public Integer getEmailClearPolicy() {
        return this.emailClearPolicy;
    }

    public Customer getOwner() {
        return this.owner;
    }

    public Set<Email> getEmails() {
        return this.emails;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof EmailFolders other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (!Objects.equals(this$name, other$name)) return false;
        final Object this$timeCreated = this.getTimeCreated();
        final Object other$timeCreated = other.getTimeCreated();
        if (!Objects.equals(this$timeCreated, other$timeCreated))
            return false;
        final Object this$emailClearPolicy = this.getEmailClearPolicy();
        final Object other$emailClearPolicy = other.getEmailClearPolicy();
        return Objects.equals(this$emailClearPolicy, other$emailClearPolicy);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EmailFolders;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $timeCreated = this.getTimeCreated();
        result = result * PRIME + ($timeCreated == null ? 43 : $timeCreated.hashCode());
        final Object $emailClearPolicy = this.getEmailClearPolicy();
        result = result * PRIME + ($emailClearPolicy == null ? 43 : $emailClearPolicy.hashCode());
        return result;
    }
}