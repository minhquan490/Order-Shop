package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.annotation.Trigger;
import com.bachlinh.order.annotation.Validator;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Label("EFR-")
@Entity
@Table(name = "EMAIL_FOLDER", indexes = {
        @Index(name = "idx_email_folder_owner", columnList = "OWNER_ID"),
        @Index(name = "idx_email_folder_name", columnList = "NAME")
})
@Validator(validators = "com.bachlinh.order.validate.validator.internal.EmailFoldersValidator")
@ActiveReflection
@Trigger(triggers = {"com.bachlinh.order.trigger.internal.EmailFolderIndexTrigger"})
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
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

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of email folder must be string");
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailFolders that)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getName(), that.getName()) && Objects.equal(getTimeCreated(), that.getTimeCreated()) && Objects.equal(getEmailClearPolicy(), that.getEmailClearPolicy()) && Objects.equal(getOwner(), that.getOwner()) && Objects.equal(getEmails(), that.getEmails());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getName(), getTimeCreated(), getEmailClearPolicy(), getOwner(), getEmails());
    }
}