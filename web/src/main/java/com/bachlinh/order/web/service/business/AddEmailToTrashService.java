package com.bachlinh.order.web.service.business;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.resp.EmailTrashResp;

import java.util.Collection;

public interface AddEmailToTrashService {

    EmailTrashResp addEmailToTrash(String emailId, Customer owner);

    EmailTrashResp addEmailsToTrash(Collection<String> emailIds, Customer owner);
}
