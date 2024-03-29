package com.bachlinh.order.web.handler.rest.common.email.folder;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailFolderInfoResp;
import com.bachlinh.order.web.service.common.EmailFolderService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "emailFolderListHandler")
public class EmailFolderListHandler extends AbstractController<Collection<EmailFolderInfoResp>, Void> {
    private EmailFolderService emailFolderService;
    private String url;

    private EmailFolderListHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<EmailFolderInfoResp>, Void> newInstance() {
        return new EmailFolderListHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<EmailFolderInfoResp> internalHandler(Payload<Void> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailFolderService.getEmailFoldersOfCustomer(customer);
    }

    @Override
    protected void inject() {
        if (emailFolderService == null) {
            emailFolderService = resolveService(EmailFolderService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.email-folder.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
