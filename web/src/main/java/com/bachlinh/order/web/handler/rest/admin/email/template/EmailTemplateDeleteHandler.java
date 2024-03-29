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
import com.bachlinh.order.web.dto.form.admin.email.template.EmailTemplateDeleteForm;
import com.bachlinh.order.web.service.common.EmailTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RouteProvider(name = "emailTemplateDeleteHandler")
@ActiveReflection
@EnableCsrf
@Permit(roles = {Role.ADMIN, Role.SEO, Role.MARKETING})
public class EmailTemplateDeleteHandler extends AbstractController<Map<String, Object>, EmailTemplateDeleteForm> {
    private String url;
    private EmailTemplateService emailTemplateService;

    private EmailTemplateDeleteHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, EmailTemplateDeleteForm> newInstance() {
        return new EmailTemplateDeleteHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<EmailTemplateDeleteForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailTemplateService.deleteEmailTemplate(request.data(), customer);
        return createDefaultResponse(HttpStatus.ACCEPTED.value(), new String[]{"Delete successfully"});
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
            url = getEnvironment().getProperty("shop.url.admin.email-template.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
