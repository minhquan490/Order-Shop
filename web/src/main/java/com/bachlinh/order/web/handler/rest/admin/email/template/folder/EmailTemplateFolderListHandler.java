package com.bachlinh.order.web.handler.rest.admin.email.template.folder;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderListResp;
import com.bachlinh.order.web.service.common.EmailTemplateFolderService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@RouteProvider(name = "emailTemplateFolderListHandler")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailTemplateFolderListHandler extends AbstractController<Collection<EmailTemplateFolderListResp>, Void> {
    private String url;
    private EmailTemplateFolderService emailTemplateFolderService;

    @Override
    public AbstractController<Collection<EmailTemplateFolderListResp>, Void> newInstance() {
        return new EmailTemplateFolderListHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<EmailTemplateFolderListResp> internalHandler(Payload<Void> request) {
        var customerId = SecurityContextHolder.getContext().getAuthentication().getName();
        return emailTemplateFolderService.getEmailTemplateFolders(customerId);
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
            url = getEnvironment().getProperty("shop.url.admin.email-template-folder.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
