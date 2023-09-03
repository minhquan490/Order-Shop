package com.bachlinh.order.web.handler.rest.common.email.trash;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.EnableCsrf;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.AddEmailToTrashForm;
import com.bachlinh.order.web.dto.resp.EmailTrashResp;
import com.bachlinh.order.web.service.business.EmailInTrashService;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RouteProvider(name = "addEmailToTrashHandler")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
@EnableCsrf
public class AddEmailToTrashHandler extends AbstractController<EmailTrashResp, AddEmailToTrashForm> {
    private EmailInTrashService emailInTrashService;
    private String url;

    @Override
    @ActiveReflection
    protected EmailTrashResp internalHandler(Payload<AddEmailToTrashForm> request) {
        var data = request.data().getEmailIds();
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (data.length == 1) {
            return emailInTrashService.addEmailToTrash(data[0], customer);
        }
        return emailInTrashService.addEmailsToTrash(List.of(data), customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailInTrashService == null) {
            emailInTrashService = resolver.resolveDependencies(EmailInTrashService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.email-trash.add");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
