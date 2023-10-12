package com.bachlinh.order.web.handler.rest.admin.index;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.AnalyzeCustomerNewInMonthResp;
import com.bachlinh.order.web.service.business.CustomerAnalyzeService;

@ActiveReflection
@RouteProvider(name = "customerNewInMonthHandler")
public class CustomerNewInMonthHandler extends AbstractController<AnalyzeCustomerNewInMonthResp, Void> {
    private CustomerAnalyzeService customerAnalyzeService;
    private String url;

    private CustomerNewInMonthHandler() {
        super();
    }

    @Override
    public AbstractController<AnalyzeCustomerNewInMonthResp, Void> newInstance() {
        return new CustomerNewInMonthHandler();
    }

    @Override
    @ActiveReflection
    protected AnalyzeCustomerNewInMonthResp internalHandler(Payload<Void> request) {
        return customerAnalyzeService.analyzeCustomerNewInMonth();
    }

    @Override
    protected void inject() {
        if (customerAnalyzeService == null) {
            customerAnalyzeService = resolveService(CustomerAnalyzeService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.customer.customer-in-month-analyze");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
