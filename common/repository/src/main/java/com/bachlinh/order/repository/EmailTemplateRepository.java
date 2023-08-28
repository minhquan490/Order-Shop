package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;

import java.util.Collection;

public interface EmailTemplateRepository extends NativeQueryRepository {
    EmailTemplate saveEmailTemplate(EmailTemplate emailTemplate);

    EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate);

    EmailTemplate getEmailTemplateById(String id, Customer owner);

    EmailTemplate getDefaultEmailTemplate(String name);

    Collection<EmailTemplate> getEmailTemplates(Customer owner);

    Collection<EmailTemplate> getEmailTemplates(Collection<String> ids, Customer owner);

    boolean isEmailTemplateExisted(String id, Customer owner);

    void deleteEmailTemplate(EmailTemplate emailTemplate);
}