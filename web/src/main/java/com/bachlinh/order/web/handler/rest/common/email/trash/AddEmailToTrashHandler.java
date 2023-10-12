package com.bachlinh.order.web.handler.rest.common.email.trash;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.AddEmailToTrashForm;
import com.bachlinh.order.web.dto.resp.EmailTrashResp;
import com.bachlinh.order.web.service.business.EmailInTrashService;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RouteProvider(name = "addEmailToTrashHandler")
@ActiveReflection
@EnableCsrf
public class AddEmailToTrashHandler extends AbstractController<EmailTrashResp, AddEmailToTrashForm> {
    private EmailInTrashService emailInTrashService;
    private String url;

    private AddEmailToTrashHandler() {
        super();
    }

    @Override
    public AbstractController<EmailTrashResp, AddEmailToTrashForm> newInstance() {
        return new AddEmailToTrashHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
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
        if (emailInTrashService == null) {
            emailInTrashService = resolveService(EmailInTrashService.class);
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
