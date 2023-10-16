package com.bachlinh.order.web.handler.rest.admin.email.sending;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.sending.NormalEmailSendingForm;
import com.bachlinh.order.web.dto.resp.EmailSendingResp;
import com.bachlinh.order.web.service.business.EmailSendingService;
import org.springframework.security.core.context.SecurityContextHolder;

@RouteProvider(name = "normalEmailSendingHandler")
@ActiveReflection
@EnableCsrf
public class NormalEmailSendingHandler extends AbstractController<EmailSendingResp, NormalEmailSendingForm> {
    private String url;
    private EmailSendingService emailSendingService;

    private NormalEmailSendingHandler() {
        super();
    }

    @Override
    public AbstractController<EmailSendingResp, NormalEmailSendingForm> newInstance() {
        return new NormalEmailSendingHandler();
    }

    @Override
    @ActiveReflection
    protected EmailSendingResp internalHandler(Payload<NormalEmailSendingForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailSendingService.sendNormalEmail(request.data(), customer);
    }

    @Override
    protected void inject() {
        if (emailSendingService == null) {
            emailSendingService = resolveService(EmailSendingService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email.common.sending");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
