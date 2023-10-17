package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailTrash;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class EmailTrashMapper extends AbstractEntityMapper<EmailTrash> {

    @Override
    protected void assignMultiTable(EmailTrash target, Collection<BaseEntity<?>> results) {
        Set<Email> emailSet = new LinkedHashSet<>();

        for (BaseEntity<?> result : results) {
            Email email = (Email) result;
            email.setEmailTrash(target);
            emailSet.add(email);
        }

        target.setEmails(emailSet);
    }

    @Override
    protected void assignSingleTable(EmailTrash target, BaseEntity<?> mapped) {
        Customer customer = (Customer) mapped;
        customer.setEmailTrash(target);
        target.setCustomer(customer);
    }

    @Override
    protected String getTableName() {
        return EmailTrash.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        EmailTrash result = getEntityFactory().getEntity(EmailTrash.class);
        result.setEmails(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Email.class);
            Set<BaseEntity<?>> emailSet = new LinkedHashSet<>();

            mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, emailSet, "EMAILS");
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(EmailTrash target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "EMAIL_TRASH.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TRASH.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TRASH.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TRASH.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TRASH.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
