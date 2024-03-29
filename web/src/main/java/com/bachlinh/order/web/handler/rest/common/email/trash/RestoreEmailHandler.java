package com.bachlinh.order.web.handler.rest.common.email.trash;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.RestoreEmailForm;
import com.bachlinh.order.web.dto.resp.EmailTrashResp;
import com.bachlinh.order.web.service.business.EmailInTrashService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;

@ActiveReflection
@RouteProvider(name = "restoreEmailHandler")
public class RestoreEmailHandler extends AbstractController<EmailTrashResp, RestoreEmailForm> {
    private String url;
    private EmailInTrashService emailInTrashService;

    private RestoreEmailHandler() {
        super();
    }

    @Override
    public AbstractController<EmailTrashResp, RestoreEmailForm> newInstance() {
        return new RestoreEmailHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected EmailTrashResp internalHandler(Payload<RestoreEmailForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailInTrashService.restoreEmailFromTrash(Arrays.asList(request.data().getEmailIds()), customer);
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
            url = getEnvironment().getProperty("shop.url.content.email-trash.restore.email");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.PATCH;
    }
}
