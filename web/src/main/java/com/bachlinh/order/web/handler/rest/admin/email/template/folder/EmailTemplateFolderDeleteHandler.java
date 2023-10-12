package com.bachlinh.order.web.handler.rest.admin.email.template.folder;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderDeleteForm;
import com.bachlinh.order.web.service.common.EmailTemplateFolderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RouteProvider(name = "emailTemplateFolderDeleteHandler")
@ActiveReflection
@EnableCsrf
@Permit(roles = {Role.ADMIN, Role.SEO, Role.MARKETING})
public class EmailTemplateFolderDeleteHandler extends AbstractController<Map<String, Object>, EmailTemplateFolderDeleteForm> {
    private String url;
    private EmailTemplateFolderService emailTemplateFolderService;

    private EmailTemplateFolderDeleteHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, EmailTemplateFolderDeleteForm> newInstance() {
        return new EmailTemplateFolderDeleteHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<EmailTemplateFolderDeleteForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailTemplateFolderService.deleteEmailTemplateFolder(request.data(), customer);
        return createDefaultResponse(HttpStatus.ACCEPTED.value(), new String[]{"Delete successfully"});
    }

    @Override
    protected void inject() {
        if (emailTemplateFolderService == null) {
            emailTemplateFolderService = resolveService(EmailTemplateFolderService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email-template-folder.delete");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.DELETE;
    }
}
