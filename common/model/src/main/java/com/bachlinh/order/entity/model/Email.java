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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

import org.springframework.http.MediaType;

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
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
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
    private Boolean read;

    @Column(name = "WAS_SENT", columnDefinition = "bit", nullable = false)
    private Boolean sent;

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

    @ActiveReflection
    protected Email() {
    }

    public boolean isRead() {
        return getRead();
    }

    public boolean isSent() {
        return getSent();
    }

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
    public <U extends BaseEntity<String>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setContent(String content) {
        if (this.content != null && !this.content.equals(content)) {
            trackUpdatedField("CONTENT", this.content, content);
        }
        this.content = content;
    }

    @ActiveReflection
    public void setReceivedTime(Timestamp receivedTime) {
        if (this.receivedTime != null && !this.receivedTime.equals(receivedTime)) {
            trackUpdatedField("RECEIVED_TIME", this.receivedTime, receivedTime);
        }
        this.receivedTime = receivedTime;
    }

    @ActiveReflection
    public void setTimeSent(Timestamp timeSent) {
        if (this.timeSent != null && !this.timeSent.equals(timeSent)) {
            trackUpdatedField("SENT_TIME", this.timeSent, timeSent);
        }
        this.timeSent = timeSent;
    }

    @ActiveReflection
    public void setTitle(String title) {
        if (this.title != null && !this.title.equals(title)) {
            trackUpdatedField("TITLE", this.title, title);
        }
        this.title = title;
    }

    @ActiveReflection
    public void setRead(Boolean read) {
        if (this.read != null && !this.read.equals(read)) {
            trackUpdatedField("WAS_READ", this.read, read);
        }
        this.read = read;
    }

    @ActiveReflection
    public void setSent(Boolean sent) {
        if (this.sent != null && !this.sent.equals(sent)) {
            trackUpdatedField("SENT", this.sent, sent);
        }
        this.sent = sent;
    }

    @ActiveReflection
    public void setMediaType(String mediaType) {
        if (this.mediaType != null && !this.mediaType.equals(mediaType)) {
            trackUpdatedField("MEDIA_TYPE", this.mediaType, mediaType);
        }
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
        if (this.folder != null && Objects.requireNonNull(this.folder.getId()).equals(folder.getId())) {
            trackUpdatedField("FOLDER_ID", this.folder.getId(), folder.getId());
        }
        this.folder = folder;
    }

    @ActiveReflection
    public void setEmailTrash(EmailTrash emailTrash) {
        if (this.emailTrash != null && this.emailTrash.getId() != null && !Objects.requireNonNull(this.emailTrash.getId()).equals(emailTrash.getId())) {
            trackUpdatedField("EMAIL_TRASH_ID", this.emailTrash.getId(), emailTrash.getId());
        }
        this.emailTrash = emailTrash;
    }

    public String getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public Timestamp getReceivedTime() {
        return this.receivedTime;
    }

    public Timestamp getTimeSent() {
        return this.timeSent;
    }

    public String getTitle() {
        return this.title;
    }

    public Boolean getRead() {
        return this.read;
    }

    public Boolean getSent() {
        return this.sent;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public Customer getFromCustomer() {
        return this.fromCustomer;
    }

    public Customer getToCustomer() {
        return this.toCustomer;
    }

    public EmailFolders getFolder() {
        return this.folder;
    }

    public EmailTrash getEmailTrash() {
        return this.emailTrash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email email)) return false;
        if (!super.equals(o)) return false;
        return com.google.common.base.Objects.equal(getId(), email.getId()) && com.google.common.base.Objects.equal(getContent(), email.getContent()) && com.google.common.base.Objects.equal(getReceivedTime(), email.getReceivedTime()) && com.google.common.base.Objects.equal(getTimeSent(), email.getTimeSent()) && com.google.common.base.Objects.equal(getTitle(), email.getTitle()) && com.google.common.base.Objects.equal(isRead(), email.isRead()) && com.google.common.base.Objects.equal(isSent(), email.isSent()) && com.google.common.base.Objects.equal(getMediaType(), email.getMediaType()) && com.google.common.base.Objects.equal(getFromCustomer(), email.getFromCustomer()) && com.google.common.base.Objects.equal(getToCustomer(), email.getToCustomer()) && com.google.common.base.Objects.equal(getFolder(), email.getFolder()) && com.google.common.base.Objects.equal(getEmailTrash(), email.getEmailTrash());
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(super.hashCode(), getId(), getContent(), getReceivedTime(), getTimeSent(), getTitle(), isRead(), isSent(), getMediaType(), getFromCustomer(), getToCustomer(), getFolder(), getEmailTrash());
    }
}