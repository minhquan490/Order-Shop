package com.bachlinh.order.web.repository.spi;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.entity.model.EmailTemplateFolder;
import com.bachlinh.order.repository.NativeQueryRepository;

import java.util.Collection;

public interface EmailTemplateFolderRepository extends NativeQueryRepository {
    EmailTemplateFolder saveTemplateFolder(EmailTemplateFolder folder);

    EmailTemplateFolder updateTemplateFolder(EmailTemplateFolder folder);

    void deleteTemplateFolder(EmailTemplateFolder folder);

    boolean isEmailTemplateFolderNameExisted(String emailTemplateFolderName);

    boolean isEmailTemplateFolderIdExisted(String id);

    EmailTemplateFolder getEmailTemplateFolderById(String id, Customer owner);

    EmailTemplateFolder getEmailTemplateFolderByName(String name, Customer owner);

    EmailTemplateFolder getEmailTemplateFolderHasCustomer(String id);

    EmailTemplateFolder getEmailTemplateFolderForUpdate(String id);

    Collection<EmailTemplateFolder> getEmailTemplateFolders(String customerId);
}
