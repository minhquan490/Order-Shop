package com.bachlinh.order.repository;

import com.bachlinh.order.entity.model.EmailTemplateFolder;

import java.util.Collection;

public interface EmailTemplateFolderRepository {
    EmailTemplateFolder saveTemplateFolder(EmailTemplateFolder folder);

    EmailTemplateFolder updateTemplateFolder(EmailTemplateFolder folder);

    void deleteTemplateFolder(EmailTemplateFolder folder);

    boolean isEmailTemplateFolderNameExisted(String emailTemplateFolderName);

    boolean isEmailTemplateFolderIdExisted(String id);

    EmailTemplateFolder getEmailTemplateFolder(String id);

    EmailTemplateFolder getEmailTemplateFolderHasCustomer(String id);

    Collection<EmailTemplateFolder> getEmailTemplateFolders(String customerId);
}
