package com.bachlinh.order.web.service.common;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderCreateForm;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderDeleteForm;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderInfoResp;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderListResp;

import java.util.Collection;

public interface EmailTemplateFolderService {
    EmailTemplateFolderInfoResp createEmailTemplateFolder(EmailTemplateFolderCreateForm form, Customer customer);

    EmailTemplateFolderInfoResp updateEmailTemplateFolder(EmailTemplateFolderUpdateForm form, Customer customer);

    void deleteEmailTemplateFolder(EmailTemplateFolderDeleteForm form, Customer customer);

    EmailTemplateFolderInfoResp getEmailTemplateFolderInfo(String templateFolderId, Customer owner);

    Collection<EmailTemplateFolderListResp> getEmailTemplateFolders(String customerId);
}
