package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class EmailTemplateMapper extends AbstractEntityMapper<EmailTemplate> {
    @Override
    protected String getTableName() {
        return EmailTemplate.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        EmailTemplate result = getEntityFactory().getEntity(EmailTemplate.class);
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("EMAIL_TEMPLATE")) {
                hook = resultSet.poll();
                setData(result, hook, wrapped);
            } else {
                break;
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);
            if (mapper.canMap(resultSet)) {
                var owner = mapper.map(resultSet);
                if (owner != null) {
                    result.setOwner(owner);
                }
            }
        }
        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(EmailTemplateFolder.class);
            if (mapper.canMap(resultSet)) {
                var emailTemplateFolder = mapper.map(resultSet);
                if (emailTemplateFolder != null) {
                    emailTemplateFolder.getEmailTemplates().add(result);
                    result.setFolder(emailTemplateFolder);
                }
            }
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(EmailTemplate target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "EMAIL_TEMPLATE.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.TITLE" -> {
                target.setTitle((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.CONTENT" -> {
                target.setContent((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.EXPIRY_POLICY" -> {
                target.setExpiryPolicy((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.TOTAL_ARGUMENT" -> {
                target.setTotalArgument((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.PARAMS" -> {
                target.setParams((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
