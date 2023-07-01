package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplate;

public interface EmailTemplateRepository {
    EmailTemplate saveEmailTemplate(EmailTemplate emailTemplate);

    EmailTemplate updateEmailTemplate(EmailTemplate emailTemplate);

    boolean isEmailTemplateTitleExisted(String title);

    EmailTemplate getEmailTemplateByName(String templateName, Customer owner);

    EmailTemplate getEmailTemplateById(String id, Customer owner);
}