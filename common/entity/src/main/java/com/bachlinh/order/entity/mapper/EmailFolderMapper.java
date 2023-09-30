package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailFolders;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class EmailFolderMapper extends AbstractEntityMapper<EmailFolders> {
    @Override
    protected String getTableName() {
        return EmailFolders.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        EmailFolders result = getEntityFactory().getEntity(EmailFolders.class);
        result.setEmails(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("EMAIL_FOLDER")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);
            if (mapper.canMap(resultSet)) {
                var customer = mapper.map(resultSet);
                if (customer != null) {
                    result.setOwner(customer);
                }
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Email.class);
            Set<Email> emailSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("EMAILS")) {
                    var email = mapper.map(resultSet);
                    if (email != null) {
                        email.setFolder(result);
                        emailSet.add(email);
                    }
                } else {
                    break;
                }
            }
            result.setEmails(emailSet);
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
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
