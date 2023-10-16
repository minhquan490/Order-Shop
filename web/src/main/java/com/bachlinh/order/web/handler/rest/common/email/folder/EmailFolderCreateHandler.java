package com.bachlinh.order.web.handler.rest.common.email.folder;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.EmailFolderCreateForm;
import com.bachlinh.order.web.dto.resp.EmailFolderInfoResp;
import com.bachlinh.order.web.service.common.EmailFolderService;
import org.springframework.security.core.context.SecurityContextHolder;

@RouteProvider(name = "emailFolderCreateHandler")
@ActiveReflection
@EnableCsrf
public class EmailFolderCreateHandler extends AbstractController<EmailFolderInfoResp, EmailFolderCreateForm> {
    private EmailFolderService emailFolderService;
    private String url;

    private EmailFolderCreateHandler() {
        super();
    }

    @Override
    public AbstractController<EmailFolderInfoResp, EmailFolderCreateForm> newInstance() {
        return new EmailFolderCreateHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected EmailFolderInfoResp internalHandler(Payload<EmailFolderCreateForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailFolderService.createEmailFolder(request.data(), customer);
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
            url = getEnvironment().getProperty("shop.url.content.email-folder.create");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
