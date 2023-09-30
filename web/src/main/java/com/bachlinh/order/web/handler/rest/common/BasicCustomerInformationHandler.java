package com.bachlinh.order.web.handler.rest.common;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.exception.http.ResourceNotFoundException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.common.CustomerBasicInfoForm;
import com.bachlinh.order.web.dto.resp.CustomerBasicInformationResp;
import com.bachlinh.order.web.service.common.CustomerService;
import org.springframework.util.StringUtils;

@ActiveReflection
@RouteProvider
public class BasicCustomerInformationHandler extends AbstractController<CustomerBasicInformationResp, CustomerBasicInfoForm> {

    private CustomerService customerService;
    private String url;

    private BasicCustomerInformationHandler() {
    }

    @Override
    public AbstractController<CustomerBasicInformationResp, CustomerBasicInfoForm> newInstance() {
        return new BasicCustomerInformationHandler();
    }

    @Override
    @ActiveReflection
    protected CustomerBasicInformationResp internalHandler(Payload<CustomerBasicInfoForm> request) {
        String accessToken = request.data().getToken();
        if (!StringUtils.hasText(accessToken)) {
            throw new ResourceNotFoundException("Not found", getPath());
        }
        return customerService.basicCustomerInfo(accessToken, getNativeResponse());
    }

    @Override
    protected void inject() {
        if (customerService == null) {
            customerService = resolveService(CustomerService.class);
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
        return RequestMethod.POST;
    }
}
