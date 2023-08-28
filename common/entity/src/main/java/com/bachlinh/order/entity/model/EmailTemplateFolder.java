package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
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
import jakarta.persistence.OneToMany;
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
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    @JoinColumn(name = "OWNER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Customer owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "folder")
    @EqualsAndHashCode.Exclude
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

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<String>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
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

    public static EntityMapper<EmailTemplateFolder> getMapper() {
        return new EmailTemplateFolderMapper();
    }

    private static class EmailTemplateFolderMapper implements EntityMapper<EmailTemplateFolder> {

        @Override
        public EmailTemplateFolder map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new EmailTemplateFolder().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public EmailTemplateFolder map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            EmailTemplateFolder result = new EmailTemplateFolder();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("EMAIL_TEMPLATE_FOLDER")) {
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
                var mapper = EmailTemplate.getMapper();
                Set<EmailTemplate> emailTemplateSet = new LinkedHashSet<>();
                while (!resultSet.isEmpty()) {
                    hook = resultSet.peek();
                    if (hook.columnName().split("\\.")[0].equals("EMAIL_TEMPLATE")) {
                        var emailTemplate = mapper.map(resultSet);
                        emailTemplate.setFolder(result);
                        emailTemplateSet.add(emailTemplate);
                    } else {
                        break;
                    }
                }
                result.setEmailTemplates(emailTemplateSet);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("EMAIL_TEMPLATE_FOLDER");
            });
        }

        private void setData(EmailTemplateFolder target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "EMAIL_TEMPLATE_FOLDER.ID" -> target.setId(mappingObject.value());
                case "EMAIL_TEMPLATE_FOLDER.NAME" -> target.setName((String) mappingObject.value());
                case "EMAIL_TEMPLATE_FOLDER.CLEAR_EMAIL_TEMPLATE_POLICY" ->
                        target.setClearTemplatePolicy((Integer) mappingObject.value());
                case "EMAIL_TEMPLATE_FOLDER.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "EMAIL_TEMPLATE_FOLDER.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "EMAIL_TEMPLATE_FOLDER.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "EMAIL_TEMPLATE_FOLDER.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}