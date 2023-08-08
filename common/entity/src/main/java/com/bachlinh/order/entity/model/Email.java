package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableFullTextSearch;
import com.bachlinh.order.annotation.FullTextField;
import com.bachlinh.order.annotation.Label;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_CUSTOMER_ID", updatable = false)
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
        if (this.folder != null && this.folder.getId().equals(folder.getId())) {
            trackUpdatedField("FOLDER_ID", this.folder.getId());
        }
        this.folder = folder;
    }

    @ActiveReflection
    public void setEmailTrash(EmailTrash emailTrash) {
        if (this.emailTrash != null && !this.emailTrash.getId().equals(emailTrash.getId())) {
            trackUpdatedField("EMAIL_TRASH_ID", this.emailTrash.getId().toString());
        }
        this.emailTrash = emailTrash;
    }
}