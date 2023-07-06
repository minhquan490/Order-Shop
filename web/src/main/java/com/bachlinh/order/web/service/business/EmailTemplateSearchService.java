package com.bachlinh.order.web.service.business;

import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateSearchForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;

import java.util.Collection;

public interface EmailTemplateSearchService {
    Collection<EmailTemplateInfoResp> search(EmailTemplateSearchForm form, Customer owner);
}
