package com.bachlinh.order.web.service.common;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.form.common.EmailFolderCreateForm;
import com.bachlinh.order.web.dto.form.common.EmailFolderUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailFolderInfoResp;

import java.util.Collection;

public interface EmailFolderService {
    void createDefaultFolders(Customer owner);

    void deleteEmailFolder(String folderId);

    EmailFolderInfoResp createEmailFolder(EmailFolderCreateForm form, Customer owner);

    EmailFolderInfoResp updateEmailFolder(EmailFolderUpdateForm form, Customer owner);

    Collection<EmailFolderInfoResp> getEmailFoldersOfCustomer(Customer owner);
}
