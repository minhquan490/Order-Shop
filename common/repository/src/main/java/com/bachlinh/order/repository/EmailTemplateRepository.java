package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.EmailTemplate;

public interface EmailTemplateRepository {
    boolean isEmailTemplateTitleExisted(String title);

    EmailTemplate getEmailTemplate(String templateName);
}