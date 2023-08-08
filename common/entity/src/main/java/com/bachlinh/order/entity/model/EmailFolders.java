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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Label("EFR-")
@Entity
@Table(name = "EMAIL_FOLDER", indexes = {
        @Index(name = "idx_email_folder_owner", columnList = "OWNER_ID"),
        @Index(name = "idx_email_folder_name", columnList = "NAME")
})
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
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
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setTimeCreated(Timestamp timeCreated) {
        if (this.timeCreated != null && !this.timeCreated.equals(timeCreated)) {
            trackUpdatedField("TIME_CREATED", this.timeCreated.toString());
        }
        this.timeCreated = timeCreated;
    }

    @ActiveReflection
    public void setEmailClearPolicy(Integer emailClearPolicy) {
        if (this.emailClearPolicy != null && !this.emailClearPolicy.equals(emailClearPolicy)) {
            trackUpdatedField("EMAIL_CLEAR_POLICY", this.emailClearPolicy.toString());
        }
        this.emailClearPolicy = emailClearPolicy;
    }

    @ActiveReflection
    public void setOwner(Customer owner) {
        if (this.owner != null && !this.owner.getId().equals(owner.getId())) {
            trackUpdatedField("OWNER_ID", this.owner.getId());
        }
        this.owner = owner;
    }

    @ActiveReflection
    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }
}