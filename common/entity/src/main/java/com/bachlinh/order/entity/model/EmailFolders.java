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

@Label("EFR-")
@Entity
@Table(name = "EMAIL_FOLDER", indexes = {
        @Index(name = "idx_email_folder_owner", columnList = "OWNER_ID"),
        @Index(name = "idx_email_folder_name", columnList = "NAME")
})
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Customer owner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "folder")
    @EqualsAndHashCode.Exclude
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
        if (this.owner != null && !this.owner.getId().equals(owner.getId())) {
            trackUpdatedField("OWNER_ID", this.owner.getId(), owner.getId());
        }
        this.owner = owner;
    }

    @ActiveReflection
    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    public static EntityMapper<EmailFolders> getMapper() {
        return new EmailFoldersMapper();
    }

    private static class EmailFoldersMapper implements EntityMapper<EmailFolders> {

        @Override
        public EmailFolders map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new EmailFolders().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public EmailFolders map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            EmailFolders result = new EmailFolders();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("EMAIL_FOLDER")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                if (mapper.canMap(resultSet)) {
                    var customer = mapper.map(resultSet);
                    result.setOwner(customer);
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Email.getMapper();
                Set<Email> emailSet = new LinkedHashSet<>();
                while (!resultSet.isEmpty()) {
                    hook = resultSet.peek();
                    if (hook.columnName().split("\\.")[0].equals("EMAILS")) {
                        var email = mapper.map(resultSet);
                        email.setFolder(result);
                        emailSet.add(email);
                    } else {
                        break;
                    }
                }
                result.setEmails(emailSet);
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("EMAIL_FOLDER");
            });
        }

        private void setData(EmailFolders target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "EMAIL_FOLDER.ID" -> target.setId(mappingObject.value());
                case "EMAIL_FOLDER.NAME" -> target.setName((String) mappingObject.value());
                case "EMAIL_FOLDER.TIME_CREATED" -> target.setTimeCreated((Timestamp) mappingObject.value());
                case "EMAIL_FOLDER.EMAIL_CLEAR_POLICY" -> target.setEmailClearPolicy((Integer) mappingObject.value());
                case "EMAIL_FOLDER.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "EMAIL_FOLDER.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "EMAIL_FOLDER.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "EMAIL_FOLDER.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}