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
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateDeleteForm;
import com.bachlinh.order.web.service.common.EmailTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

@RouteProvider(name = "emailTemplateDeleteHandler")
@ActiveReflection
@EnableCsrf
@Permit(roles = {Role.ADMIN, Role.SEO, Role.MARKETING})
public class EmailTemplateDeleteHandler extends AbstractController<Map<String, Object>, EmailTemplateDeleteForm> {
    private String url;
    private EmailTemplateService emailTemplateService;

    private EmailTemplateDeleteHandler() {
    }

    @Override
    public AbstractController<Map<String, Object>, EmailTemplateDeleteForm> newInstance() {
        return new EmailTemplateDeleteHandler();
    }

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<EmailTemplateDeleteForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailTemplateService.deleteEmailTemplate(request.data(), customer);
        var resp = new HashMap<String, Object>(2);
        resp.put("status", HttpStatus.ACCEPTED.value());
        resp.put("messages", new String[]{"Delete successfully"});
        return resp;
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
            url = getEnvironment().getProperty("shop.url.admin.email-template.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
