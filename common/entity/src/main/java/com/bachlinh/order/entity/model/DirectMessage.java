package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.lang.NonNull;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

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
@EqualsAndHashCode(callSuper = true)
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
    @EqualsAndHashCode.Exclude
    private Customer fromCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TO_CUSTOMER_ID", nullable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
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

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        return entities.stream().map(entity -> (U) entity).toList();
    }

    @ActiveReflection
    public void setContent(@NonNull String content) {
        if (this.content != null && !this.content.equals(content)) {
            trackUpdatedField("CONTENT", this.content, content);
        }
        this.content = content;
    }

    @ActiveReflection
    public void setTimeSent(@NonNull Timestamp timeSent) {
        if (this.timeSent != null && !this.timeSent.equals(timeSent)) {
            trackUpdatedField("SENT_TIME", this.timeSent, timeSent);
        }
        this.timeSent = timeSent;
    }

    @ActiveReflection
    public void setFromCustomer(@NonNull Customer fromCustomer) {
        if (this.fromCustomer != null && !this.fromCustomer.getId().equals(fromCustomer.getId())) {
            trackUpdatedField("FROM_CUSTOMER_ID", this.fromCustomer.getId(), fromCustomer.getId());
        }
        this.fromCustomer = fromCustomer;
    }

    @ActiveReflection
    public void setToCustomer(@NonNull Customer toCustomer) {
        if (this.toCustomer != null && !this.toCustomer.getId().equals(toCustomer.getId())) {
            trackUpdatedField("TO_CUSTOMER", this.toCustomer.getId(), toCustomer.getId());
        }
        this.toCustomer = toCustomer;
    }

    public static EntityMapper<DirectMessage> getMapper() {
        return new DirectMessageMapper();
    }

    private static class DirectMessageMapper implements EntityMapper<DirectMessage> {

        @Override
        public DirectMessage map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new DirectMessage().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public DirectMessage map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            DirectMessage result = new DirectMessage();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("DIRECT_MESSAGE")) {
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
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("DIRECT_MESSAGE");
            });
        }

        private void setData(DirectMessage target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "DIRECT_MESSAGE.ID" -> target.setId(mappingObject.value());
                case "DIRECT_MESSAGE.CONTENT" -> target.setContent((String) mappingObject.value());
                case "DIRECT_MESSAGE.SENT_TIME" -> target.setTimeSent((Timestamp) mappingObject.value());
                case "DIRECT_MESSAGE.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "DIRECT_MESSAGE.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "DIRECT_MESSAGE.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "DIRECT_MESSAGE.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }

        private void assignToCustomer(Queue<MappingObject> resultSet, DirectMessage result) {
            String toCustomerKey = "TO_CUSTOMER";
            Queue<MappingObject> cloned = new LinkedList<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals(toCustomerKey)) {
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

        private void assignFromCustomer(Queue<MappingObject> resultSet, DirectMessage result) {
            Queue<MappingObject> cloned = new LinkedList<>();
            while (!resultSet.isEmpty()) {
                MappingObject hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("FROM_CUSTOMER")) {
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
    }
}