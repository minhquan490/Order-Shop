package com.bachlinh.order.web.handler.rest.admin.email.sending;

import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.sending.NormalEmailSendingForm;
import com.bachlinh.order.web.dto.resp.EmailSendingResp;
import com.bachlinh.order.web.service.business.EmailSendingService;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class NormalEmailSendingHandler extends AbstractController<EmailSendingResp, NormalEmailSendingForm> {
    private String url;
    private EmailSendingService emailSendingService;

    @Override
    protected EmailSendingResp internalHandler(Payload<NormalEmailSendingForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailSendingService.sendNormalEmail(request.data(), customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailSendingService == null) {
            emailSendingService = resolver.resolveDependencies(EmailSendingService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email.normal.sending");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
