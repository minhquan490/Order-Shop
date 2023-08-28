package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
import com.bachlinh.order.entity.EntityMapper;
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
import jakarta.persistence.Tuple;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;
import java.util.Queue;

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
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
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
    @EqualsAndHashCode.Exclude
    private Customer owner;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "FOLDER_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private EmailTemplateFolder folder;

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
    public <U extends BaseEntity<String>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setTitle(String title) {
        if (this.title != null && !this.title.equals(title)) {
            trackUpdatedField("TITLE", this.title);
        }
        this.title = title;
    }

    @ActiveReflection
    public void setContent(String content) {
        if (this.content != null && !this.content.equals(content)) {
            trackUpdatedField("CONTENT", this.content);
        }
        this.content = content;
    }

    @ActiveReflection
    public void setExpiryPolicy(Integer expiryPolicy) {
        if (this.expiryPolicy != null && !this.expiryPolicy.equals(expiryPolicy)) {
            trackUpdatedField("EXPIRY_POLICY", this.expiryPolicy.toString());
        }
        this.expiryPolicy = expiryPolicy;
    }

    @ActiveReflection
    public void setOwner(Customer owner) {
        if (this.owner != null && !this.owner.getId().equals(owner.getId())) {
            trackUpdatedField("OWNER_ID", this.owner.getId());
        }
        this.owner = owner;
    }

    @ActiveReflection
    public void setFolder(EmailTemplateFolder folder) {
        if (this.folder != null && !Objects.requireNonNull(this.folder.getId()).equals(folder.getId())) {
            trackUpdatedField("FOLDER_ID", this.folder.getId());
        }
        this.folder = folder;
    }

    @ActiveReflection
    public void setName(String name) {
        if (this.name != null && !this.name.equals(name)) {
            trackUpdatedField("NAME", this.name);
        }
        this.name = name;
    }

    @ActiveReflection
    public void setTotalArgument(Integer totalArgument) {
        if (this.totalArgument != null && !this.totalArgument.equals(totalArgument)) {
            trackUpdatedField("TOTAL_ARGUMENT", this.totalArgument.toString());
        }
        this.totalArgument = totalArgument;
    }

    @ActiveReflection
    public void setParams(String params) {
        if (this.params != null && !this.params.equals(params)) {
            trackUpdatedField("PARAMS", this.params);
        }
        this.params = params;
    }

    public static EntityMapper<EmailTemplate> getMapper() {
        return new EmailTemplateMapper();
    }

    private static class EmailTemplateMapper implements EntityMapper<EmailTemplate> {

        @Override
        public EmailTemplate map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new EmailTemplate().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public EmailTemplate map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            EmailTemplate result = new EmailTemplate();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("EMAIL_TEMPLATE")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                if (mapper.canMap(resultSet)) {
                    var owner = mapper.map(resultSet);
                    result.setOwner(owner);
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = EmailTemplateFolder.getMapper();
                if (mapper.canMap(resultSet)) {
                    var emailTemplateFolder = mapper.map(resultSet);
                    emailTemplateFolder.getEmailTemplates().add(result);
                    result.setFolder(emailTemplateFolder);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("EMAIL_TEMPLATE");
            });
        }

        private void setData(EmailTemplate target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "EMAIL_TEMPLATE.ID" -> target.setId(mappingObject.value());
                case "EMAIL_TEMPLATE.NAME" -> target.setName((String) mappingObject.value());
                case "EMAIL_TEMPLATE.TITLE" -> target.setTitle((String) mappingObject.value());
                case "EMAIL_TEMPLATE.CONTENT" -> target.setContent((String) mappingObject.value());
                case "EMAIL_TEMPLATE.EXPIRY_POLICY" -> target.setExpiryPolicy((Integer) mappingObject.value());
                case "EMAIL_TEMPLATE.TOTAL_ARGUMENT" -> target.setTotalArgument((Integer) mappingObject.value());
                case "EMAIL_TEMPLATE.PARAMS" -> target.setParams((String) mappingObject.value());
                case "EMAIL_TEMPLATE.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "EMAIL_TEMPLATE.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "EMAIL_TEMPLATE.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "EMAIL_TEMPLATE.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}