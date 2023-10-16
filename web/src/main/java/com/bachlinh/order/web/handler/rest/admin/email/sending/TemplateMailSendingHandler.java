package com.bachlinh.order.web.handler.rest.admin.email.sending;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.email.sending.TemplateMailSendingForm;
import com.bachlinh.order.web.service.business.EmailTemplateSendingService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

@RouteProvider(name = "templateMailSendingHandler")
@ActiveReflection
@EnableCsrf
public class TemplateMailSendingHandler extends AbstractController<Map<String, Object>, TemplateMailSendingForm> {
    private String url;
    private EmailTemplateSendingService emailTemplateSendingService;

    private TemplateMailSendingHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, TemplateMailSendingForm> newInstance() {
        return new TemplateMailSendingHandler();
    }

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<TemplateMailSendingForm> request) {
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        emailTemplateSendingService.processTemplateAndSend(request.data(), customer, getEnvironment());
        return createDefaultResponse(HttpStatus.OK.value(), new String[]{"Sending email complete"});
    }

    @Override
    protected void inject() {
        if (emailTemplateSendingService == null) {
            emailTemplateSendingService = resolveService(EmailTemplateSendingService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email-template.sending");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
