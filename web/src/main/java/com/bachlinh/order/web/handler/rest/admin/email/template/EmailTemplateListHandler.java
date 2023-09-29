package com.bachlinh.order.web.handler.rest.admin.email.template;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@RouteProvider(name = "emailTemplateListHandler")
@ActiveReflection
public class EmailTemplateListHandler extends AbstractController<Collection<EmailTemplateInfoResp>, Void> {
    private String url;
    private EmailTemplateService emailTemplateService;

    private EmailTemplateListHandler() {
    }

    @Override
    public AbstractController<Collection<EmailTemplateInfoResp>, Void> newInstance() {
        return new EmailTemplateListHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<EmailTemplateInfoResp> internalHandler(Payload<Void> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailTemplateService.getEmailTemplates(customer);
    }

    @Override
    protected void inject() {
        if (emailTemplateService == null) {
            emailTemplateService = resolveService(EmailTemplateService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email-template.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
