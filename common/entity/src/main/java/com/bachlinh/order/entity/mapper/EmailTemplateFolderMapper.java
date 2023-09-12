package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.annotation.ResultMapper;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class EmailTemplateFolderMapper extends AbstractEntityMapper<EmailTemplateFolder> {
    @Override
    protected String getTableName() {
        return EmailTemplateFolder.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        MappingObject hook;
        EmailTemplateFolder result = getEntityFactory().getEntity(EmailTemplateFolder.class);
        result.setEmailTemplates(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);
        while (!resultSet.isEmpty()) {
            hook = resultSet.peek();
            if (hook.columnName().split("\\.")[0].equals("EMAIL_TEMPLATE_FOLDER")) {
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
            var mapper = getEntityMapperFactory().createMapper(EmailTemplate.class);
            Set<EmailTemplate> emailTemplateSet = new LinkedHashSet<>();
            while (!resultSet.isEmpty()) {
                hook = resultSet.peek();
                if (hook.columnName().split("\\.")[0].equals("EMAIL_TEMPLATE")) {
                    var emailTemplate = mapper.map(resultSet);
                    if (emailTemplate != null) {
                        emailTemplate.setFolder(result);
                        emailTemplateSet.add(emailTemplate);
                    }
                } else {
                    break;
                }
            }
            result.setEmailTemplates(emailTemplateSet);
        }
        EntityWrapper entityWrapper = new EntityWrapper(result);
        entityWrapper.setTouched(wrapped.get());
        return entityWrapper;
    }

    @Override
    protected void setData(EmailTemplateFolder target, MappingObject mappingObject, AtomicBoolean wrappedTouched) {
        if (mappingObject.value() == null) {
            return;
        }
        switch (mappingObject.columnName()) {
            case "EMAIL_TEMPLATE_FOLDER.ID" -> {
                target.setId(mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE_FOLDER.NAME" -> {
                target.setName((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE_FOLDER.CLEAR_EMAIL_TEMPLATE_POLICY" -> {
                target.setClearTemplatePolicy((Integer) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE_FOLDER.CREATED_BY" -> {
                target.setCreatedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE_FOLDER.MODIFIED_BY" -> {
                target.setModifiedBy((String) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE_FOLDER.CREATED_DATE" -> {
                target.setCreatedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            case "EMAIL_TEMPLATE_FOLDER.MODIFIED_DATE" -> {
                target.setModifiedDate((Timestamp) mappingObject.value());
                wrappedTouched.set(true);
            }
            default -> {/* Do nothing */}
        }
    }
}
