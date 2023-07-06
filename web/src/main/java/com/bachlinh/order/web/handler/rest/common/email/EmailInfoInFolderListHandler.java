package com.bachlinh.order.web.handler.rest.common.email;

import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailInfoInFolderListResp;
import com.bachlinh.order.web.service.common.EmailService;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class EmailInfoInFolderListHandler extends AbstractController<EmailInfoInFolderListResp, Void> {
    private EmailService emailService;
    private String url;

    @Override
    @ActiveReflection
    protected EmailInfoInFolderListResp internalHandler(Payload<Void> request) {
        var folderId = getNativeRequest().getHeaders().getFirst("folderId");
        if (folderId == null) {
            throw new ResourceNotFoundException("Not found", getPath());
        }
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailService.getEmailsOfCustomer(folderId, customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailService == null) {
            emailService = resolver.resolveDependencies(EmailService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.email.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
