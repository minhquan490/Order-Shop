package com.bachlinh.order.entity.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableFullTextSearch;
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.core.annotation.FullTextField;
import com.bachlinh.order.core.annotation.Label;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.util.Collection;
import java.util.Objects;

@Label("ETE-")
@Entity
@Table(
        name = "EMAIL_TEMPLATE",
        indexes = {
                @Index(name = "idx_email_template_owner", columnList = "OWNER_ID"),
                @Index(name = "idx_email_template_folder", columnList = "FOLDER_ID"),
                @Index(name = "idx_email_template_title", columnList = "TITLE"),
                @Index(name = "idx_email_template_name", columnList = "NAME")
        }
)
@ActiveReflection
@EnableFullTextSearch
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
public class EmailTemplate extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "NAME", columnDefinition = "nvarchar(100)")
    @FullTextField
    @ActiveReflection
    private String name;

    @Column(name = "TITLE", nullable = false)
    @FullTextField
    @ActiveReflection
    private String title;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "nvarchar(700)")
    @FullTextField
    @ActiveReflection
    private String content;

    @Column(name = "EXPIRY_POLICY", nullable = false)
    private Integer expiryPolicy = -1;

    @Column(name = "TOTAL_ARGUMENT", nullable = false)
    private Integer totalArgument;

    @Column(name = "PARAMS", nullable = false)
    private String params;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "OWNER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer owner;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    private EmailTemplateFolder folder;

    @ActiveReflection
    protected EmailTemplate() {
    }

    @ActiveReflection
    @Override
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of email template must be string");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setTitle(String title) {
        if (this.title != null && !this.title.equals(title)) {
            trackUpdatedField("TITLE", this.title, title);
        }
        this.title = title;
    }

    @ActiveReflection
    public void setContent(String content) {
        if (this.content != null && !this.content.equals(content)) {
            trackUpdatedField("CONTENT", this.content, content);
        }
        this.content = content;
    }

    @ActiveReflection
    public void setExpiryPolicy(Integer expiryPolicy) {
        if (this.expiryPolicy != null && !this.expiryPolicy.equals(expiryPolicy)) {
            trackUpdatedField("EXPIRY_POLICY", this.expiryPolicy, expiryPolicy);
        }
        this.expiryPolicy = expiryPolicy;
    }

    @ActiveReflection
    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    @ActiveReflection
    public void setFolder(EmailTemplateFolder folder) {
        if (this.folder != null && !Objects.requireNonNull(this.folder.getId()).equals(folder.getId())) {
            trackUpdatedField("FOLDER_ID", this.folder.getId(), folder.getId());
        }
        this.folder = folder;
    }

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name, name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setTotalArgument(Integer totalArgument) {
        if (this.totalArgument != null && !this.totalArgument.equals(totalArgument)) {
            trackUpdatedField("TOTAL_ARGUMENT", this.totalArgument, totalArgument);
        }
        this.totalArgument = totalArgument;
    }

    @ActiveReflection
    public void setParams(String params) {
        if (this.params != null && !this.params.equals(params)) {
            trackUpdatedField("PARAMS", this.params, params);
        }
        this.params = params;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public Integer getExpiryPolicy() {
        return this.expiryPolicy;
    }

    public Integer getTotalArgument() {
        return this.totalArgument;
    }

    public String getParams() {
        return this.params;
    }

    public Customer getOwner() {
        return this.owner;
    }

    public EmailTemplateFolder getFolder() {
        return this.folder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmailTemplate that)) return false;
        if (!super.equals(o)) return false;
        return com.google.common.base.Objects.equal(getId(), that.getId()) && com.google.common.base.Objects.equal(getName(), that.getName()) && com.google.common.base.Objects.equal(getTitle(), that.getTitle()) && com.google.common.base.Objects.equal(getContent(), that.getContent()) && com.google.common.base.Objects.equal(getExpiryPolicy(), that.getExpiryPolicy()) && com.google.common.base.Objects.equal(getTotalArgument(), that.getTotalArgument()) && com.google.common.base.Objects.equal(getParams(), that.getParams()) && com.google.common.base.Objects.equal(getOwner(), that.getOwner()) && com.google.common.base.Objects.equal(getFolder(), that.getFolder());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getName(), getTitle(), getContent(), getExpiryPolicy(), getTotalArgument(), getParams(), getOwner(), getFolder());
    }
}