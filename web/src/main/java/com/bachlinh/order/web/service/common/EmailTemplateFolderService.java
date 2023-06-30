package com.bachlinh.order.web.service.common;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.form.admin.EmailTemplateFolderCreateForm;
import com.bachlinh.order.web.dto.form.admin.EmailTemplateFolderDeleteForm;
import com.bachlinh.order.web.dto.form.admin.EmailTemplateFolderUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderInfoResp;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderListResp;

import java.util.Collection;

public interface EmailTemplateFolderService {
    EmailTemplateFolderInfoResp createEmailTemplateFolder(EmailTemplateFolderCreateForm form, Customer customer);

    EmailTemplateFolderInfoResp updateEmailTemplateFolder(EmailTemplateFolderUpdateForm form, Customer customer);

    void deleteEmailTemplateFolder(EmailTemplateFolderDeleteForm form, Customer customer);

    EmailTemplateFolderInfoResp getEmailTemplateFolderInfo(String templateFolderId);

    Collection<EmailTemplateFolderListResp> getEmailTemplateFolders(String customerId);
}
