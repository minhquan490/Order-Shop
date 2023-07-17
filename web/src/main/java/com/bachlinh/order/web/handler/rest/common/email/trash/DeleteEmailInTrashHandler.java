package com.bachlinh.order.web.handler.rest.common.email.trash;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.DeleteEmailInTrashForm;
import com.bachlinh.order.web.service.business.EmailInTrashService;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class DeleteEmailInTrashHandler extends AbstractController<Map<String, Object>, DeleteEmailInTrashForm> {
    private String url;
    private EmailInTrashService emailInTrashService;

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<DeleteEmailInTrashForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailInTrashService.removeEmailsFromTrash(Arrays.asList(request.data().getEmailId()), customer);
        var resp = new HashMap<String, Object>(2);
        resp.put("status", HttpStatus.ACCEPTED.value());
        resp.put("messages", new String[]{"Delete successfully"});
        return resp;
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
            url = getEnvironment().getProperty("shop.url.content.email-trash.remove.email");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
