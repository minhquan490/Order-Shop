package com.bachlinh.order.web.handler.rest.admin.email.template;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateCreateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateService;
import org.springframework.security.core.context.SecurityContextHolder;

@RouteProvider(name = "emailTemplateCreateHandler")
@ActiveReflection
@EnableCsrf
@Permit(roles = {Role.ADMIN, Role.SEO, Role.MARKETING})
public class EmailTemplateCreateHandler extends AbstractController<EmailTemplateInfoResp, EmailTemplateCreateForm> {
    private String url;
    private EmailTemplateService emailTemplateService;

    private EmailTemplateCreateHandler() {
        super();
    }

    @Override
    public AbstractController<EmailTemplateInfoResp, EmailTemplateCreateForm> newInstance() {
        return new EmailTemplateCreateHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected EmailTemplateInfoResp internalHandler(Payload<EmailTemplateCreateForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailTemplateService.saveEmailTemplate(request.data(), customer);
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
            url = getEnvironment().getProperty("shop.url.admin.email-template.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
