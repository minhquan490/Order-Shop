package com.bachlinh.order.web.service.business;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.environment.Environment;
import com.bachlinh.order.web.dto.form.admin.email.sending.TemplateMailSendingForm;

public interface EmailTemplateSendingService {
    void processTemplateAndSend(TemplateMailSendingForm form, Customer templateOwner, Environment environment);
}
