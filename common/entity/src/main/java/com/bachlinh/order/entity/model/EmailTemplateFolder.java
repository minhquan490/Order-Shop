package com.bachlinh.order.entity.model;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.Label;
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

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

@Label("ETF-")
@Entity
@Table(name = "EMAIL_TEMPLATE_FOLDER", indexes = @Index(name = "idx_email_template_folder_owner", columnList = "OWNER_ID"))
@ActiveReflection
public class EmailTemplateFolder extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", nullable = false, updatable = false, unique = true, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", columnDefinition = "nvarchar(300)")
    private String name;

    @Column(name = "CLEAR_EMAIL_TEMPLATE_POLICY")
    private Integer clearTemplatePolicy = -1;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "OWNER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "folder")
    private Set<EmailTemplate> emailTemplates = new HashSet<>();

    @ActiveReflection
    protected EmailTemplateFolder() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of email template folder must be string");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<EmailTemplateFolder> results = new LinkedList<>();
            EmailTemplateFolder first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (EmailTemplateFolder) entity;
                } else {
                    EmailTemplateFolder casted = (EmailTemplateFolder) entity;
                    if (casted.getEmailTemplates().isEmpty()) {
                        results.add(casted);
                    } else {
                        first.getEmailTemplates().addAll(casted.getEmailTemplates());
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
    public void setClearTemplatePolicy(Integer clearTemplatePolicy) {
        if (this.clearTemplatePolicy != null && !this.clearTemplatePolicy.equals(clearTemplatePolicy)) {
            trackUpdatedField("CLEAR_EMAIL_TEMPLATE_POLICY", this.clearTemplatePolicy, clearTemplatePolicy);
        }
        this.clearTemplatePolicy = clearTemplatePolicy;
    }

    @ActiveReflection
    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    @ActiveReflection
    public void setEmailTemplates(Set<EmailTemplate> emailTemplates) {
        this.emailTemplates = emailTemplates;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Integer getClearTemplatePolicy() {
        return this.clearTemplatePolicy;
    }

    public Customer getOwner() {
        return this.owner;
    }

    public Set<EmailTemplate> getEmailTemplates() {
        return this.emailTemplates;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof EmailTemplateFolder other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$name = this.getName();
        final Object other$name = other.getName();
        if (!Objects.equals(this$name, other$name)) return false;
        final Object this$clearTemplatePolicy = this.getClearTemplatePolicy();
        final Object other$clearTemplatePolicy = other.getClearTemplatePolicy();
        return Objects.equals(this$clearTemplatePolicy, other$clearTemplatePolicy);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EmailTemplateFolder;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final Object $clearTemplatePolicy = this.getClearTemplatePolicy();
        result = result * PRIME + ($clearTemplatePolicy == null ? 43 : $clearTemplatePolicy.hashCode());
        return result;
    }
}