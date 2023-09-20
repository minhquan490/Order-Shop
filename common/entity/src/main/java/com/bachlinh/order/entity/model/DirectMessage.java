package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
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

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(
        name = "DIRECT_MESSAGE",
        indexes = {
                @Index(name = "idx_direct_message_from_customer", columnList = "FROM_CUSTOMER_ID"),
                @Index(name = "idx_direct_message_to_customer", columnList = "TO_CUSTOMER_ID")
        }
)
@ActiveReflection
public class DirectMessage extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "nvarchar(500)")
    private String content;

    @Column(name = "SENT_TIME", nullable = false, updatable = false)
    private Timestamp timeSent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer fromCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Customer toCustomer;

    @ActiveReflection
    protected DirectMessage() {
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of direct message must be integer");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
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
    public void setTimeSent(Timestamp timeSent) {
        if (this.timeSent != null && !this.timeSent.equals(timeSent)) {
            trackUpdatedField("SENT_TIME", this.timeSent, timeSent);
        }
        this.timeSent = timeSent;
    }

    @ActiveReflection
    public void setFromCustomer(Customer fromCustomer) {
        this.fromCustomer = fromCustomer;
    }

    @ActiveReflection
    public void setToCustomer(Customer toCustomer) {
        this.toCustomer = toCustomer;
    }

    public Integer getId() {
        return this.id;
    }

    public String getContent() {
        return this.content;
    }

    public Timestamp getTimeSent() {
        return this.timeSent;
    }

    public Customer getFromCustomer() {
        return this.fromCustomer;
    }

    public Customer getToCustomer() {
        return this.toCustomer;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof DirectMessage other)) return false;
        if (!other.canEqual(this)) return false;
        if (!super.equals(o)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (!Objects.equals(this$id, other$id)) return false;
        final Object this$content = this.getContent();
        final Object other$content = other.getContent();
        if (!Objects.equals(this$content, other$content)) return false;
        final Object this$timeSent = this.getTimeSent();
        final Object other$timeSent = other.getTimeSent();
        return Objects.equals(this$timeSent, other$timeSent);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof DirectMessage;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = super.hashCode();
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $content = this.getContent();
        result = result * PRIME + ($content == null ? 43 : $content.hashCode());
        final Object $timeSent = this.getTimeSent();
        result = result * PRIME + ($timeSent == null ? 43 : $timeSent.hashCode());
        return result;
    }
}