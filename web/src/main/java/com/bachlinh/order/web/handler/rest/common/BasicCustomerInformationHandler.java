package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.CustomerBasicInformationResp;
import com.bachlinh.order.web.service.common.CustomerService;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__(@ActiveReflection))
public class BasicCustomerInformationHandler extends AbstractController<CustomerBasicInformationResp, Void> {

    private CustomerService customerService;
    private String url;

    @Override
    @ActiveReflection
    protected CustomerBasicInformationResp internalHandler(Payload<Void> request) {
        String accessToken = getNativeRequest().getUrlQueryParam().getFirst("token");
        if (!StringUtils.hasText(accessToken)) {
            throw new ResourceNotFoundException("Not found", getPath());
        }
        return customerService.basicCustomerInfo(accessToken, getNativeResponse());
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (customerService == null) {
            customerService = resolver.resolveDependencies(CustomerService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.basic-info");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}