package com.bachlinh.order.web.handler.rest.common.email;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.EmailSearchForm;
import com.bachlinh.order.web.dto.resp.EmailInfoResp;
import com.bachlinh.order.web.service.business.EmailSearchingService;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "emailSearchHandler")
public class EmailSearchHandler extends AbstractController<Collection<EmailInfoResp>, EmailSearchForm> {
    private String url;
    private EmailSearchingService emailService;

    private EmailSearchHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<EmailInfoResp>, EmailSearchForm> newInstance() {
        return new EmailSearchHandler();
    }

    @Override
    @ActiveReflection
    protected Collection<EmailInfoResp> internalHandler(Payload<EmailSearchForm> request) {
        return emailService.searchEmail(request.data().getQuery());
    }

    @Override
    protected void inject() {
        if (emailService == null) {
            emailService = resolveService(EmailSearchingService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.content.email.search");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
