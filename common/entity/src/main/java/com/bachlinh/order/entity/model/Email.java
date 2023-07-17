package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
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
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.MediaType;

import java.sql.Timestamp;

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
@Trigger(triggers = {
        "com.bachlinh.order.trigger.internal.IndexEmailContentTrigger",
        "com.bachlinh.order.trigger.internal.EmailSendingTrigger"
})
@Validator(validators = "com.bachlinh.order.validate.validator.internal.EmailValidator")
@ActiveReflection
@EnableFullTextSearch
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
public class Email extends AbstractEntity {

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
    private boolean read;

    @Column(name = "WAS_SENT", columnDefinition = "bit", nullable = false)
    private boolean sent;

    @Column(name = "MEDIA_TYPE", nullable = false, length = 50)
    private String mediaType = MediaType.TEXT_PLAIN_VALUE;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_CUSTOMER_ID", nullable = false, updatable = false)
    private Customer fromCustomer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_CUSTOMER_ID", nullable = false, updatable = false)
    private Customer toCustomer;

    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "FOLDER_ID", nullable = false)
    private EmailFolders folder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMAIL_TRASH_ID")
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

    @ActiveReflection
    public void setContent(String content) {
        this.content = content;
    }

    @ActiveReflection
    public void setReceivedTime(Timestamp receivedTime) {
        this.receivedTime = receivedTime;
    }

    @ActiveReflection
    public void setTimeSent(Timestamp timeSent) {
        this.timeSent = timeSent;
    }

    @ActiveReflection
    public void setTitle(String title) {
        this.title = title;
    }

    @ActiveReflection
    public void setRead(boolean read) {
        this.read = read;
    }

    @ActiveReflection
    public void setSent(boolean sent) {
        this.sent = sent;
    }

    @ActiveReflection
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    @ActiveReflection
    public void setFromCustomer(Customer fromCustomer) {
        this.fromCustomer = fromCustomer;
    }

    @ActiveReflection
    public void setToCustomer(Customer toCustomer) {
        this.toCustomer = toCustomer;
    }

    @ActiveReflection
    public void setFolder(EmailFolders folder) {
        this.folder = folder;
    }

    @ActiveReflection
    public void setEmailTrash(EmailTrash emailTrash) {
        this.emailTrash = emailTrash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        return isRead() == email.isRead() && isSent() == email.isSent() && Objects.equal(getId(), email.getId()) && Objects.equal(getContent(), email.getContent()) && Objects.equal(getReceivedTime(), email.getReceivedTime()) && Objects.equal(getTimeSent(), email.getTimeSent()) && Objects.equal(getTitle(), email.getTitle()) && Objects.equal(getMediaType(), email.getMediaType()) && Objects.equal(getFromCustomer(), email.getFromCustomer()) && Objects.equal(getToCustomer(), email.getToCustomer()) && Objects.equal(getFolder(), email.getFolder()) && Objects.equal(getEmailTrash(), email.getEmailTrash());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getContent(), getReceivedTime(), getTimeSent(), getTitle(), isRead(), isSent(), getMediaType(), getFromCustomer(), getToCustomer(), getFolder(), getEmailTrash());
    }
}