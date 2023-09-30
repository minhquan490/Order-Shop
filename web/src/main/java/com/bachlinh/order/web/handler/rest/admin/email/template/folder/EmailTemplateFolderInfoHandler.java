package com.bachlinh.order.web.handler.rest.admin.email.template.folder;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.core.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailTemplateFolderInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateFolderService;
import org.springframework.security.core.context.SecurityContextHolder;

@RouteProvider(name = "emailTemplateFolderInfoHandler")
@ActiveReflection
public class EmailTemplateFolderInfoHandler extends AbstractController<EmailTemplateFolderInfoResp, Void> {
    private EmailTemplateFolderService emailTemplateFolderService;
    private String url;

    private EmailTemplateFolderInfoHandler() {
    }

    @Override
    public AbstractController<EmailTemplateFolderInfoResp, Void> newInstance() {
        return new EmailTemplateFolderInfoHandler();
    }

    @Override
    @ActiveReflection
    protected EmailTemplateFolderInfoResp internalHandler(Payload<Void> request) {
        var id = getNativeRequest().getUrlQueryParam().getFirst("folderId");
        if (id == null) {
            throw new BadVariableException("Missing folderId query param");
        }
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailTemplateFolderService.getEmailTemplateFolderInfo(id, customer);
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
            url = getEnvironment().getProperty("shop.url.admin.email-template-folder.info");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
