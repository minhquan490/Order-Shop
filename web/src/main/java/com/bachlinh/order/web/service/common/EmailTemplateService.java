package com.bachlinh.order.web.service.common;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateCreateForm;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateUpdateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;

public interface EmailTemplateService {
    EmailTemplateInfoResp saveEmailTemplate(EmailTemplateCreateForm form, Customer owner);

    EmailTemplateInfoResp updateEmailTemplate(EmailTemplateUpdateForm form, Customer owner);
}
