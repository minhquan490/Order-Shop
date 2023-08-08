package com.bachlinh.order.web.handler.rest.admin.email.template;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.EmailTemplateInfoResp;
import com.bachlinh.order.web.service.common.EmailTemplateService;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

@RouteProvider(name = "emailTemplateInfoHandler")
@ActiveReflection
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class EmailTemplateInfoHandler extends AbstractController<EmailTemplateInfoResp, Void> {
    private EmailTemplateService emailTemplateService;
    private String url;

    @Override
    @ActiveReflection
    protected EmailTemplateInfoResp internalHandler(Payload<Void> request) {
        var id = getNativeRequest().getUrlQueryParam().getFirst("id");
        if (!StringUtils.hasText(id)) {
            throw new ResourceNotFoundException("Not found", getPath());
        }
        var customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return emailTemplateService.getEmailTemplateById(id, customer);
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (emailTemplateService == null) {
            emailTemplateService = resolver.resolveDependencies(EmailTemplateService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.email-template.info");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
