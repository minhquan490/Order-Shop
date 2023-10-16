package com.bachlinh.order.web.handler.rest.admin.email.template;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

@RouteProvider(name = "emailTemplateInfoHandler")
@ActiveReflection
public class EmailTemplateInfoHandler extends AbstractController<EmailTemplateInfoResp, Void> {
    private EmailTemplateService emailTemplateService;
    private String url;

    private EmailTemplateInfoHandler() {
        super();
    }

    @Override
    public AbstractController<EmailTemplateInfoResp, Void> newInstance() {
        return new EmailTemplateInfoHandler();
    }

    @Override
    @ActiveReflection
    protected EmailTemplateInfoResp internalHandler(Payload<Void> request) {
        var id = getNativeRequest().getUrlQueryParam().getFirst("id");
        if (!StringUtils.hasText(id)) {
            throw new ResourceNotFoundException("Not found", getPath());
        }
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailTemplateService.getEmailTemplateById(id, customer);
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
            url = getEnvironment().getProperty("shop.url.admin.email-template.info");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
