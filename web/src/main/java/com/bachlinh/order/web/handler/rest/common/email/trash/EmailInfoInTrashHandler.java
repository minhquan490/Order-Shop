package com.bachlinh.order.web.handler.rest.common.email.trash;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailTrashResp;
import com.bachlinh.order.web.service.business.EmailInTrashService;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@ActiveReflection
@RouteProvider(name = "emailInfoInTrashHandler")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class EmailInfoInTrashHandler extends AbstractController<EmailTrashResp, Void> {
    private String url;
    private EmailInTrashService trashService;

    @Override
    @ActiveReflection
    protected EmailTrashResp internalHandler(Payload<Void> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return trashService.getEmailsInTrash(customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (trashService == null) {
            trashService = resolver.resolveDependencies(EmailInTrashService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.email-trash.emails");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
