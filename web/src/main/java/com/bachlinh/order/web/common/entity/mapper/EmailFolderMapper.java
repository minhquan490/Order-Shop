package com.bachlinh.order.web.common.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailFolders;

import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class EmailFolderMapper extends AbstractEntityMapper<EmailFolders> {

    @Override
    protected void assignMultiTable(EmailFolders target, Collection<BaseEntity<?>> results) {

        for (BaseEntity<?> result : results) {
            Email email = (Email) result;
            email.setFolder(target);
        }

        Set<Email> emailSet = new LinkedHashSet<>(results.stream().map(Email.class::cast).toList());
        target.setEmails(emailSet);
    }

    @Override
    protected void assignSingleTable(EmailFolders target, BaseEntity<?> mapped) {
        Customer customer = (Customer) mapped;
        target.setOwner(customer);
    }

    @Override
    protected String getTableName() {
        return EmailFolders.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        EmailFolders result = getEntityFactory().getEntity(EmailFolders.class);
        result.setEmails(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Email.class);
            Set<BaseEntity<?>> emailSet = new LinkedHashSet<>();

            mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, emailSet, "EMAILS");
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(EmailFolders target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "EMAIL_FOLDER.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_FOLDER.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_FOLDER.TIME_CREATED" -> {
                target.setTimeCreated((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_FOLDER.EMAIL_CLEAR_POLICY" -> {
                target.setEmailClearPolicy((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_FOLDER.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_FOLDER.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_FOLDER.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_FOLDER.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
