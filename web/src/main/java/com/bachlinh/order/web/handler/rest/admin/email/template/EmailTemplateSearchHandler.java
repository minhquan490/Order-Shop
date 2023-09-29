package com.bachlinh.order.web.handler.rest.admin.email.template;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateSearchForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;
import com.bachlinh.order.web.service.business.EmailTemplateSearchService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@RouteProvider(name = "emailTemplateSearchHandler")
@ActiveReflection
public class EmailTemplateSearchHandler extends AbstractController<Collection<EmailTemplateInfoResp>, EmailTemplateSearchForm> {
    private String url;
    private EmailTemplateSearchService emailTemplateSearchService;

    private EmailTemplateSearchHandler() {
    }

    @Override
    public AbstractController<Collection<EmailTemplateInfoResp>, EmailTemplateSearchForm> newInstance() {
        return new EmailTemplateSearchHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<EmailTemplateInfoResp> internalHandler(Payload<EmailTemplateSearchForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailTemplateSearchService.search(request.data(), customer);
    }

    @Override
    protected void inject() {
        if (emailTemplateSearchService == null) {
            emailTemplateSearchService = resolveService(EmailTemplateSearchService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email-template.search");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
