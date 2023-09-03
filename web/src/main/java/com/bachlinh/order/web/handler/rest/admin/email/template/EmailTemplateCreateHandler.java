package com.bachlinh.order.web.handler.rest.admin.email.template;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateCreateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateService;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@RouteProvider(name = "emailTemplateCreateHandler")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
@EnableCsrf
@Permit(roles = {Role.ADMIN, Role.SEO, Role.MARKETING})
public class EmailTemplateCreateHandler extends AbstractController<EmailTemplateInfoResp, EmailTemplateCreateForm> {
    private String url;
    private EmailTemplateService emailTemplateService;

    @Override
    @ActiveReflection
    protected EmailTemplateInfoResp internalHandler(Payload<EmailTemplateCreateForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailTemplateService.saveEmailTemplate(request.data(), customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailTemplateService == null) {
            emailTemplateService = resolver.resolveDependencies(EmailTemplateService.class);
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
