package com.bachlinh.order.entity.model;

import com.google.common.base.Objects;
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
import com.bachlinh.order.core.annotation.Formula;
import com.bachlinh.order.repository.formula.CommonFieldSelectFormula;
import com.bachlinh.order.repository.formula.IdFieldFormula;

import java.sql.Timestamp;
import java.util.Collection;

@Entity
@Table(
        name = "DIRECT_MESSAGE",
        indexes = {
                @Index(name = "idx_direct_message_from_customer", columnList = "FROM_CUSTOMER_ID"),
                @Index(name = "idx_direct_message_to_customer", columnList = "TO_CUSTOMER_ID")
        }
)
@ActiveReflection
@Formula(processors = {CommonFieldSelectFormula.class, IdFieldFormula.class})
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DirectMessage that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equal(getId(), that.getId()) && Objects.equal(getContent(), that.getContent()) && Objects.equal(getTimeSent(), that.getTimeSent()) && Objects.equal(getFromCustomer(), that.getFromCustomer()) && Objects.equal(getToCustomer(), that.getToCustomer());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), getId(), getContent(), getTimeSent(), getFromCustomer(), getToCustomer());
    }
}