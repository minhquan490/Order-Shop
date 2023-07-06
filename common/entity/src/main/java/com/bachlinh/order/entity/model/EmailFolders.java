package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
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
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Trigger;
import com.bachlinh.order.annotation.Validator;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Label("EFR-")
@Entity
@Table(name = "EMAIL_FOLDER", indexes = {
        @Index(name = "idx_email_folder_owner", columnList = "OWNER_ID"),
        @Index(name = "idx_email_folder_name", columnList = "NAME")
})
@Validator(validators = "com.bachlinh.order.validator.internal.EmailFoldersValidator")
@ActiveReflection
@Trigger(triggers = {"com.bachlinh.order.trigger.internal.EmailFolderIndexTrigger"})
public class EmailFolders extends AbstractEntity {

    @Id
    @Column(name = "ID", updatable = false, unique = true, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", columnDefinition = "nvarchar(300)")
    private String name;

    @Column(name = "TIME_CREATED")
    private Timestamp timeCreated;

    @Column(name = "EMAIL_CLEAR_POLICY")
    private Integer emailClearPolicy = -1;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", nullable = false, updatable = false)
    private Customer owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "folder")
    private Set<Email> emails = new HashSet<>();

    @ActiveReflection
    EmailFolders() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmailFolders that = (EmailFolders) o;
        return Objects.equal(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
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

    @ActiveReflection
    public void setName(String name) {
        this.name = name;
    }

    @ActiveReflection
    public void setTimeCreated(Timestamp timeCreated) {
        this.timeCreated = timeCreated;
    }

    @ActiveReflection
    public void setEmailClearPolicy(Integer emailClearPolicy) {
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
}