package com.bachlinh.order.web.service.business;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.form.admin.email.sending.NormalEmailSendingForm;
import com.bachlinh.order.web.dto.resp.EmailSendingResp;

public interface EmailSendingService {
    EmailSendingResp sendNormalEmail(NormalEmailSendingForm form, Customer sender);
}
