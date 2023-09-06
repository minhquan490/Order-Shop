package com.bachlinh.order.web.handler.rest.admin.email.template.folder;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.Permit;
import com.bachlinh.order.entity.enums.Role;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderDeleteForm;
import com.bachlinh.order.web.service.common.EmailTemplateFolderService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

@RouteProvider(name = "emailTemplateFolderDeleteHandler")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EnableCsrf
@Permit(roles = {Role.ADMIN, Role.SEO, Role.MARKETING})
public class EmailTemplateFolderDeleteHandler extends AbstractController<Map<String, Object>, EmailTemplateFolderDeleteForm> {
    private String url;
    private EmailTemplateFolderService emailTemplateFolderService;

    @Override
    public AbstractController<Map<String, Object>, EmailTemplateFolderDeleteForm> newInstance() {
        return new EmailTemplateFolderDeleteHandler();
    }

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<EmailTemplateFolderDeleteForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailTemplateFolderService.deleteEmailTemplateFolder(request.data(), customer);
        var resp = new HashMap<String, Object>(2);
        resp.put("status", HttpStatus.ACCEPTED.value());
        resp.put("messages", new String[]{"Delete successfully"});
        return resp;
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailTemplateFolderService == null) {
            emailTemplateFolderService = resolver.resolveDependencies(EmailTemplateFolderService.class);
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
