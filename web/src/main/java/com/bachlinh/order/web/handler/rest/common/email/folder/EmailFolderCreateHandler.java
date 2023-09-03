package com.bachlinh.order.web.handler.rest.common.email.folder;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.EmailFolderCreateForm;
import com.bachlinh.order.web.dto.resp.EmailFolderInfoResp;
import com.bachlinh.order.web.service.common.EmailFolderService;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@RouteProvider(name = "emailFolderCreateHandler")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
@EnableCsrf
public class EmailFolderCreateHandler extends AbstractController<EmailFolderInfoResp, EmailFolderCreateForm> {
    private EmailFolderService emailFolderService;
    private String url;

    @Override
    @ActiveReflection
    protected EmailFolderInfoResp internalHandler(Payload<EmailFolderCreateForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailFolderService.createEmailFolder(request.data(), customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailFolderService == null) {
            emailFolderService = resolver.resolveDependencies(EmailFolderService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.email-folder.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
