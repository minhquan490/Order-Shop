package com.bachlinh.order.web.handler.rest.common.email;

import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailInfoResp;
import com.bachlinh.order.web.service.common.EmailService;

@RouteProvider
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class EmailInfoHandler extends AbstractController<EmailInfoResp, Void> {
    private String url;
    private EmailService emailService;

    @Override
    @ActiveReflection
    protected EmailInfoResp internalHandler(Payload<Void> request) {
        var id = getNativeRequest().getUrlQueryParam().getFirst("id");
        if (!StringUtils.hasText(id)) {
            throw new ResourceNotFoundException("Not found", getPath());
        }
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailService.getEmailOfCustomer(id, customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailService == null) {
            emailService = resolver.resolveDependencies(EmailService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email.common.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
