package com.bachlinh.order.entity.model;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.entity.EntityMapper;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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

@ActiveReflection
@Entity
@Table(name = "EMAIL_TRASH", indexes = @Index(name = "inx_email_trash_customer", columnList = "CUSTOMER_ID"))
@Getter
@NoArgsConstructor(onConstructor = @__(@ActiveReflection), access = AccessLevel.PROTECTED)
@DynamicUpdate
@EqualsAndHashCode(callSuper = true)
public class EmailTrash extends AbstractEntity<Integer> {

    @Id
    @Column(name = "ID", updatable = false, nullable = false, unique = true)
    private Integer id;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "emailTrash")
    private Set<Email> emails = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, unique = true, updatable = false)
    @Fetch(FetchMode.JOIN)
    @EqualsAndHashCode.Exclude
    private Customer customer;

    @ActiveReflection
    public void setEmails(Set<Email> emails) {
        this.emails = emails;
    }

    @ActiveReflection
    public void setCustomer(Customer customer) {
        if (this.customer != null && !this.customer.getId().equals(customer.getId())) {
            trackUpdatedField("CUSTOMER_ID", this.customer.getId());
        }
        this.customer = customer;
    }

    @Override
    @ActiveReflection
    public void setId(Object id) {
        if (id instanceof Integer casted) {
            this.id = casted;
        } else {
            throw new PersistenceException("Id of EmailTrash must be int");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> U map(Tuple resultSet) {
        return (U) getMapper().map(resultSet);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <U extends BaseEntity<Integer>> Collection<U> reduce(Collection<BaseEntity<?>> entities) {
        if (entities.isEmpty()) {
            return Collections.emptyList();
        } else {
            Deque<EmailTrash> results = new LinkedList<>();
            EmailTrash first = null;
            for (var entity : entities) {
                if (first == null) {
                    first = (EmailTrash) entity;
                } else {
                    EmailTrash casted = (EmailTrash) entity;
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

    public static EntityMapper<EmailTrash> getMapper() {
        return new EmailTrashMapper();
    }

    private static class EmailTrashMapper implements EntityMapper<EmailTrash> {

        @Override
        public EmailTrash map(Tuple resultSet) {
            Queue<MappingObject> mappingObjectQueue = new EmailTrash().parseTuple(resultSet);
            return this.map(mappingObjectQueue);
        }

        @Override
        public EmailTrash map(Queue<MappingObject> resultSet) {
            MappingObject hook;
            EmailTrash result = new EmailTrash();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("EMAIL_TRASH")) {
                    hook = resultSet.poll();
                    setData(result, hook);
                } else {
                    break;
                }
            }
            if (!resultSet.isEmpty()) {
                var mapper = Email.getMapper();
                Set<Email> emailSet = new LinkedHashSet<>();
                while (!resultSet.isEmpty()) {
                    hook = resultSet.peek();
                    if (hook.columnName().split("\\.")[0].equals("EMAILS")) {
                        var email = mapper.map(resultSet);
                        email.setEmailTrash(result);
                        emailSet.add(email);
                    } else {
                        break;
                    }
                }
                result.setEmails(emailSet);
            }
            if (!resultSet.isEmpty()) {
                var mapper = Customer.getMapper();
                if (mapper.canMap(resultSet)) {
                    var customer = mapper.map(resultSet);
                    customer.setEmailTrash(result);
                    result.setCustomer(customer);
                }
            }
            return result;
        }

        @Override
        public boolean canMap(Collection<MappingObject> testTarget) {
            return testTarget.stream().anyMatch(mappingObject -> {
                String name = mappingObject.columnName();
                return name.split("\\.")[0].equals("EMAIL_TRASH");
            });
        }

        private void setData(EmailTrash target, MappingObject mappingObject) {
            if (mappingObject.value() == null) {
                return;
            }
            switch (mappingObject.columnName()) {
                case "EMAIL_TRASH.ID" -> target.setId(mappingObject.value());
                case "EMAIL_TRASH.CREATED_BY" -> target.setCreatedBy((String) mappingObject.value());
                case "EMAIL_TRASH.MODIFIED_BY" -> target.setModifiedBy((String) mappingObject.value());
                case "EMAIL_TRASH.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
                case "EMAIL_TRASH.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
                default -> {/* Do nothing */}
            }
        }
    }
}
