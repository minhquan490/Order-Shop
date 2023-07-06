package com.bachlinh.order.web.handler.rest.admin.email.template.folder;

import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.template.folder.EmailTemplateFolderCreateForm;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateFolderService;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class EmailTemplateFolderCreateHandler extends AbstractController<EmailTemplateFolderInfoResp, EmailTemplateFolderCreateForm> {
    private EmailTemplateFolderService emailTemplateFolderService;
    private String url;

    @Override
    @ActiveReflection
    protected EmailTemplateFolderInfoResp internalHandler(Payload<EmailTemplateFolderCreateForm> request) {
        var customer = ((Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return emailTemplateFolderService.createEmailTemplateFolder(request.data(), customer);
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
            url = getEnvironment().getProperty("shop.url.admin.email-template-folder.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
