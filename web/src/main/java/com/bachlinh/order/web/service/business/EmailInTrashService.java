package com.bachlinh.order.web.service.business;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.resp.EmailTrashResp;

import java.util.Collection;

public interface EmailInTrashService {

    EmailTrashResp addEmailToTrash(String emailId, Customer owner);

    EmailTrashResp addEmailsToTrash(Collection<String> emailIds, Customer owner);

    EmailTrashResp getEmailsInTrash(Customer owner);

    EmailTrashResp restoreEmailFromTrash(Collection<String> emailIds, Customer owner);

    void removeEmailsFromTrash(Collection<String> emailIds, Customer owner);
}
