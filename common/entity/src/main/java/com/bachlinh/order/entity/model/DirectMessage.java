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
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;

@Entity
@Table(
        name = "DIRECT_MESSAGE",
        indexes = {
                @Index(name = "idx_direct_message_from_customer", columnList = "FROM_CUSTOMER_ID"),
                @Index(name = "idx_direct_message_to_customer", columnList = "TO_CUSTOMER_ID")
        }
)
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class DirectMessage extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false)
    private Integer id;

    @Column(name = "CONTENT", nullable = false, columnDefinition = "nvarchar(500)")
    private String content;

    @Column(name = "SENT_TIME", nullable = false, updatable = false)
    private Timestamp timeSent;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_CUSTOMER_ID", nullable = false, updatable = false)
    private Customer fromCustomer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_CUSTOMER_ID", nullable = false, updatable = false)
    private Customer toCustomer;

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
            return;
        }
        throw new PersistenceException("Id of direct message must be integer");
    }

    @ActiveReflection
    public void setContent(@NonNull String content) {
        if (this.content != null && !this.content.equals(content)) {
            trackUpdatedField("CONTENT", this.content);
        }
        this.content = content;
    }

    @ActiveReflection
    public void setTimeSent(@NonNull Timestamp timeSent) {
        if (this.timeSent != null && !this.timeSent.equals(timeSent)) {
            trackUpdatedField("SENT_TIME", this.timeSent.toString());
        }
        this.timeSent = timeSent;
    }

    @ActiveReflection
    public void setFromCustomer(@NonNull Customer fromCustomer) {
        if (this.fromCustomer != null && !this.fromCustomer.getId().equals(fromCustomer.getId())) {
            trackUpdatedField("FROM_CUSTOMER_ID", this.fromCustomer.getId());
        }
        this.fromCustomer = fromCustomer;
    }

    @ActiveReflection
    public void setToCustomer(@NonNull Customer toCustomer) {
        if (this.toCustomer != null && !this.toCustomer.getId().equals(toCustomer.getId())) {
            trackUpdatedField("TO_CUSTOMER", this.toCustomer.getId());
        }
        this.toCustomer = toCustomer;
    }
}