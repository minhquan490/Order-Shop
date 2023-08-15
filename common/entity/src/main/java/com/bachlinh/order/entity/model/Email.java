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
import org.springframework.http.MediaType;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

@Label("EMR-")
@Entity
@Table(
        name = "EMAILS",
        indexes = {
                @Index(name = "idx_email_from_customer", columnList = "FROM_CUSTOMER_ID"),
                @Index(name = "idx_email_to_customer", columnList = "TO_CUSTOMER_ID"),
                @Index(name = "idx_email_folder", columnList = "FOLDER_ID")
        }
)
@ActiveReflection
@EnableFullTextSearch
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class Email extends AbstractEntity<String> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, columnDefinition = "varchar(32)")
    private String id;

    @Column(name = "CONTENT", columnDefinition = "nvarchar(max)")
    @FullTextField
    @ActiveReflection
    private String content;

    @Column(name = "RECEIVED_TIME", nullable = false, updatable = false)
    private Timestamp receivedTime;

    @Column(name = "SENT_TIME", nullable = false, updatable = false)
    private Timestamp timeSent;

    @Column(name = "TITLE", nullable = false, columnDefinition = "nvarchar(400)")
    @FullTextField
    @ActiveReflection
    private String title;

    @Column(name = "WAS_READ", columnDefinition = "bit", nullable = false)
    private boolean read = false;

    @Column(name = "WAS_SENT", columnDefinition = "bit", nullable = false)
    private boolean sent = false;

    @Column(name = "MEDIA_TYPE", nullable = false, length = 50)
    private String mediaType = MediaType.TEXT_PLAIN_VALUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_CUSTOMER_ID", updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer fromCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer toCustomer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "FOLDER_ID", nullable = false)
    @Fetch(FetchMode.JOIN)
    private EmailFolders folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL_TRASH_ID")
    @Fetch(FetchMode.JOIN)
    private EmailTrash emailTrash;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof String casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of email received must be string");
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
    public void setContent(String content) {
        if (this.content != null && !this.content.equals(content)) {
            trackUpdatedField("CONTENT", this.content);
        }
        this.content = content;
    }

    @ActiveReflection
    public void setReceivedTime(Timestamp receivedTime) {
        if (this.receivedTime != null && !this.receivedTime.equals(receivedTime)) {
            trackUpdatedField("RECEIVED_TIME", this.receivedTime.toString());
        }
        this.receivedTime = receivedTime;
    }

    @ActiveReflection
    public void setTimeSent(Timestamp timeSent) {
        if (this.timeSent != null && !this.timeSent.equals(timeSent)) {
            trackUpdatedField("SENT_TIME", this.timeSent.toString());
        }
        this.timeSent = timeSent;
    }

    @ActiveReflection
    public void setTitle(String title) {
        if (this.title != null && !this.title.equals(title)) {
            trackUpdatedField("TITLE", this.title);
        }
        this.title = title;
    }

    @ActiveReflection
    public void setRead(boolean read) {
        this.read = read;
    }

    @ActiveReflection
    public void setSent(boolean sent) {
        if (this.sent != sent) {
            trackUpdatedField("SENT", String.valueOf(this.sent));
        }
        this.sent = sent;
    }

    @ActiveReflection
    public void setMediaType(String mediaType) {
        if (this.mediaType != null && !this.mediaType.equals(mediaType)) {
            trackUpdatedField("MEDIA_TYPE", this.mediaType);
        }
        this.mediaType = mediaType;
    }

    @ActiveReflection
    public void setFromCustomer(Customer fromCustomer) {
        if (this.fromCustomer != null && !this.fromCustomer.getId().equals(fromCustomer.getId())) {
            trackUpdatedField("FROM_CUSTOMER_ID", this.fromCustomer.getId());
        }
        this.fromCustomer = fromCustomer;
    }

    @ActiveReflection
    public void setToCustomer(Customer toCustomer) {
        if (this.toCustomer != null && !this.toCustomer.getId().equals(toCustomer.getId())) {
            trackUpdatedField("TO_CUSTOMER", this.toCustomer.getId());
        }
        this.toCustomer = toCustomer;
    }

    @ActiveReflection
    public void setFolder(EmailFolders folder) {
        if (this.folder != null && Objects.requireNonNull(this.folder.getId()).equals(folder.getId())) {
            trackUpdatedField("FOLDER_ID", this.folder.getId());
        }
        this.folder = folder;
    }

    @ActiveReflection
    public void setEmailTrash(EmailTrash emailTrash) {
        if (this.emailTrash != null && this.emailTrash.getId() != null && !Objects.requireNonNull(this.emailTrash.getId()).equals(emailTrash.getId())) {
            trackUpdatedField("EMAIL_TRASH_ID", Objects.requireNonNull(this.emailTrash.getId()).toString());
        }
        this.emailTrash = emailTrash;
    }

    public static EntityMapper<Email> getMapper() {
        return new EmailMapper();
    }

    private static class EmailMapper implements EntityMapper<Email> {

        @Override
        public Email map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new Email().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public Email map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            Email result = new Email();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().startsWith("EMAILS")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                assignFromCustomer(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                assignToCustomer(resultSet, result);
            }
            if (!resultSet.isEmpty()) {
                var mapper = EmailFolders.getMapper();
                var emailFolders = mapper.map(resultSet);
                emailFolders.getEmails().add(result);
            }
            if (!resultSet.isEmpty()) {
                var mapper = EmailTrash.getMapper();
                var emailTrash = mapper.map(resultSet);
                emailTrash.getEmails().add(result);
                result.setEmailTrash(emailTrash);
            }
            return result;
        }

        private void setData(Email target, MappingObject mappingObject) {
            switch (mappingObject.columnName()) {
                case "EMAILS.ID" -> target.setId(mappingObject.value());
                case "EMAILS.CONTENT" -> target.setContent((String) mappingObject.value());
                case "EMAILS.RECEIVED_TIME" -> target.setReceivedTime((Timestamp) mappingObject.value());
                case "EMAILS.SENT_TIME" -> target.setTimeSent((Timestamp) mappingObject.value());
                case "EMAILS.TITLE" -> target.setTitle((String) mappingObject.value());
                case "EMAILS.WAS_READ" -> target.setRead((Boolean) mappingObject.value());
                case "EMAILS.WAS_SENT" -> target.setSent((Boolean) mappingObject.value());
                case "EMAILS.MEDIA_TYPE" -> target.setMediaType((String) mappingObject.value());
                case "EMAILS.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "EMAILS.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "EMAILS.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "EMAILS.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }

        private void assignFromCustomer(Queue<MappingObject> resultSet, Email result) {
            MappingObject hook = resultSet.peek();
            Queue<MappingObject> cloned = new LinkedList<>();
            while (!resultSet.isEmpty()) {
                if (hook.columnName().startsWith("FROM_CUSTOMER")) {
                    hook = resultSet.poll();
                    cloned.add(new MappingObject(hook.columnName().replace("FROM_CUSTOMER", "CUSTOMER"), hook.value()));
                } else {
                    break;
                }
            }
            var mapper = Customer.getMapper();
            var fromCustomer = mapper.map(cloned);
            result.setFromCustomer(fromCustomer);
        }

        private void assignToCustomer(Queue<MappingObject> resultSet, Email result) {
            String toCustomerKey = "TO_CUSTOMER";
            MappingObject hook = resultSet.peek();
            Queue<MappingObject> cloned = new LinkedList<>();
            while (!resultSet.isEmpty()) {
                if (hook.columnName().startsWith(toCustomerKey)) {
                    hook = resultSet.poll();
                    cloned.add(new MappingObject(hook.columnName().replace(toCustomerKey, "CUSTOMER"), hook.value()));
                } else {
                    break;
                }
            }
            var mapper = Customer.getMapper();
            var toCustomer = mapper.map(cloned);
            result.setToCustomer(toCustomer);
        }
    }
}