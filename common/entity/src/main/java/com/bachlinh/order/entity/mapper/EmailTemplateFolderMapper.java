package com.bachlinh.order.entity.mapper;

import com.bachlinh.order.core.annotation.ResultMapper;
import com.bachlinh.order.entity.model.BaseEntity;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@ResultMapper
public class EmailTemplateFolderMapper extends AbstractEntityMapper<EmailTemplateFolder> {

    @Override
    protected void assignMultiTable(EmailTemplateFolder target, Collection<BaseEntity<?>> results) {
        for (BaseEntity<?> result : results) {
            EmailTemplate emailTemplate = (EmailTemplate) result;
            emailTemplate.setFolder(target);
        }

        Set<EmailTemplate> templates = new LinkedHashSet<>(results.stream().map(EmailTemplate.class::cast).toList());
        target.setEmailTemplates(templates);
    }

    @Override
    protected void assignSingleTable(EmailTemplateFolder target, BaseEntity<?> mapped) {
        Customer owner = (Customer) mapped;
        target.setOwner(owner);
    }

    @Override
    protected String getTableName() {
        return EmailTemplateFolder.class.getAnnotation(Table.class).name();
    }

    @Override
    protected EntityWrapper internalMapping(Queue<MappingObject> resultSet) {
        EmailTemplateFolder result = getEntityFactory().getEntity(EmailTemplateFolder.class);
        result.setEmailTemplates(Collections.emptySet());
        AtomicBoolean wrapped = new AtomicBoolean(false);

        mapCurrentTable(resultSet, wrapped, result);

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(Customer.class);

            mapSingleTable((AbstractEntityMapper<?>) mapper, resultSet, result);
        }

        if (!resultSet.isEmpty()) {
            var mapper = getEntityMapperFactory().createMapper(EmailTemplate.class);
            Set<BaseEntity<?>> emailTemplateSet = new LinkedHashSet<>();

            mapMultiTables((AbstractEntityMapper<?>) mapper, resultSet, result, emailTemplateSet, "EMAIL_TEMPLATE");
        }
        
        return wrap(result, wrapped.get());
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
