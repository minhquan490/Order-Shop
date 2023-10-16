package com.bachlinh.order.web.handler.rest.common.email.trash;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.DeleteEmailInTrashForm;
import com.bachlinh.order.web.service.business.EmailInTrashService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Map;

@ActiveReflection
@RouteProvider(name = "deleteEmailInTrashHandler")
@EnableCsrf
public class DeleteEmailInTrashHandler extends AbstractController<Map<String, Object>, DeleteEmailInTrashForm> {
    private String url;
    private EmailInTrashService emailInTrashService;

    private DeleteEmailInTrashHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, DeleteEmailInTrashForm> newInstance() {
        return new DeleteEmailInTrashHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<DeleteEmailInTrashForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailInTrashService.removeEmailsFromTrash(Arrays.asList(request.data().getEmailId()), customer);
        return createDefaultResponse(HttpStatus.ACCEPTED.value(), new String[]{"Delete successfully"});
    }

    @Override
    protected void inject() {
        if (emailInTrashService == null) {
            emailInTrashService = resolveService(EmailInTrashService.class);
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
