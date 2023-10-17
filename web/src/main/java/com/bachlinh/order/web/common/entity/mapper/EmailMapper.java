package com.bachlinh.order.web.common.entity.mapper;

import jakarta.persistence.Table;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.EntityMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.Email;
import com.bachlinh.order.entity.model.EmailFolders;
import com.bachlinh.order.entity.model.EmailTrash;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class EmailMapper extends AbstractEntityMapper<Email> {

    @Override
    protected void assignMultiTable(Email target, Collection<BaseEntity<?>> results) {
        // Do nothing
    }

    @Override
    protected void assignSingleTable(Email target, BaseEntity<?> mapped) {
        // Do nothing
    }

    @Override
    protected String getTableName() {
        return Email.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        Email result = getEntityFactory().getEntity(Email.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            assignFromCustomer(resultSet, result);
        }
        if (!resultSet.isEmpty()) {
            assignToCustomer(resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(EmailFolders.class);
            if (mapper.canMap(resultSet)) {
                var emailFolders = mapper.map(resultSet);
                if (emailFolders != null) {
                    emailFolders.getEmails().add(result);
                }
            }
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(EmailTrash.class);
            if (mapper.canMap(resultSet)) {
                var emailTrash = mapper.map(resultSet);
                if (emailTrash != null) {
                    emailTrash.getEmails().add(result);
                    result.setEmailTrash(emailTrash);
                }
            }
        }

        return wrap(result, wrapped.get());
    }

    @Override
    protected void setData(Email target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "EMAILS.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.CONTENT" -> {
                target.setContent((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.RECEIVED_TIME" -> {
                target.setReceivedTime((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.SENT_TIME" -> {
                target.setTimeSent((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.TITLE" -> {
                target.setTitle((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.WAS_READ" -> {
                target.setRead((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.WAS_SENT" -> {
                target.setSent((Boolean) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.MEDIA_TYPE" -> {
                target.setMediaType((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAILS.CREATED_DATE" -> target.setCreatedDate((Timestamp) mappingObject.value());
            case "EMAILS.MODIFIED_DATE" -> target.setModifiedDate((Timestamp) mappingObject.value());
            default -> {/* Do nothing */}
        }
    }

    private void assignFromCustomer(Queue<MappingObject> resultSet, Email result) {
        var fromCustomer = mapCustomer(resultSet, "FROM_CUSTOMER");
        if (fromCustomer != null) {
            result.setFromCustomer(fromCustomer);
        }
    }

    private void assignToCustomer(Queue<MappingObject> resultSet, Email result) {
        Customer toCustomer = mapCustomer(resultSet, "TO_CUSTOMER");

        if (toCustomer != null) {
            result.setToCustomer(toCustomer);
        }
    }

    private Customer mapCustomer(Queue<EntityMapper.MappingObject> resultSet, String key) {
        Queue<EntityMapper.MappingObject> cloned = new LinkedList<>();
        while (!resultSet.isEmpty()) {
            EntityMapper.MappingObject hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals(key)) {
                hook = resultSet.poll();
                cloned.add(new EntityMapper.MappingObject(hook.columnName().replace(key, "CUSTOMER"), hook.value()));
            } else {
                break;
            }
        }
        var mapper = getEntityMapperFactory().createMapper(Customer.class);
        return mapper.map(cloned);
    }
}
