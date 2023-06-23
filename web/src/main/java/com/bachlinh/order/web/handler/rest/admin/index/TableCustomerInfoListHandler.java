package com.bachlinh.order.web.handler.rest.admin.index;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.TableCustomerInfoResp;
import com.bachlinh.order.web.service.common.CustomerService;

import java.util.Collection;

@ActiveReflection
@RouteProvider
public class TableCustomerInfoListHandler extends AbstractController<Collection<TableCustomerInfoResp>, Void> {
    private CustomerService customerService;
    private String url;

    @Override
    protected Collection<TableCustomerInfoResp> internalHandler(Payload<Void> request) {
        return customerService.getCustomerDataTable();
    }

    @Override
    protected void inject() {
        DependenciesResolver dependenciesResolver = getContainerResolver().getDependenciesResolver();
        if (customerService == null) {
            this.customerService = dependenciesResolver.resolveDependencies(CustomerService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.index.table.customer");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
