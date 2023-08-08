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

import java.util.HashSet;
import java.util.Set;

@Label("ETF-")
@Entity
@Table(name = "EMAIL_TEMPLATE_FOLDER", indexes = @Index(name = "idx_email_template_folder_owner", columnList = "OWNER_ID"))
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PROTECTED, onConstructor = @__(@ActiveReflection))
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class EmailTemplateFolder extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, unique = true, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", columnDefinition = "nvarchar(300)", unique = true)
    private String name;

    @Column(name = "CLEAR_EMAIL_TEMPLATE_POLICY")
    private Integer clearTemplatePolicy = -1;

    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "OWNER_ID", nullable = false, updatable = false)
    private Customer owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "folder")
    private Set<EmailTemplate> emailTemplates = new HashSet<>();

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of email template folder must be string");
    }

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setClearTemplatePolicy(Integer clearTemplatePolicy) {
        if (this.clearTemplatePolicy != null && !this.clearTemplatePolicy.equals(clearTemplatePolicy)) {
            trackUpdatedField("CLEAR_EMAIL_TEMPLATE_POLICY", this.clearTemplatePolicy.toString());
        }
        this.clearTemplatePolicy = clearTemplatePolicy;
    }

    @ActiveReflection
    public void setOwner(Customer owner) {
        if (this.owner != null && !this.owner.getId().equals(owner.getId())) {
            trackUpdatedField("OWNER_ID", this.owner.getId());
        }
        this.owner = owner;
    }

    @ActiveReflection
    public void setEmailTemplates(Set<EmailTemplate> emailTemplates) {
        this.emailTemplates = emailTemplates;
    }
}