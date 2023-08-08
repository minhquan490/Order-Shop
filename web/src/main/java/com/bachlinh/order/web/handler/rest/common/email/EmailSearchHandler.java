package com.bachlinh.order.web.handler.rest.common.email;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.EmailSearchForm;
import com.bachlinh.order.web.dto.resp.EmailInfoResp;
import com.bachlinh.order.web.service.business.EmailSearchingService;
import lombok.NoArgsConstructor;

import java.util.Collection;

@ActiveReflection
@RouteProvider(name = "emailSearchHandler")
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class EmailSearchHandler extends AbstractController<Collection<EmailInfoResp>, EmailSearchForm> {
    private String url;
    private EmailSearchingService emailService;

    @Override
    @ActiveReflection
    protected Collection<EmailInfoResp> internalHandler(Payload<EmailSearchForm> request) {
        return emailService.searchEmail(request.data().getQuery());
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailService == null) {
            emailService = resolver.resolveDependencies(EmailSearchingService.class);
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
