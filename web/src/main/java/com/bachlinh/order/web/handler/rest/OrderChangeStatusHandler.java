package com.bachlinh.order.web.handler.rest;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.OrderChangeStatusForm;
import com.bachlinh.order.web.service.business.OrderChangeStatusService;

import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@RouteProvider
public class OrderChangeStatusHandler extends AbstractController<Map<String, Object>, OrderChangeStatusForm> {
    private String url;
    private OrderChangeStatusService statusService;

    @ActiveReflection
    public OrderChangeStatusHandler() {
    }

    @Override
    protected Map<String, Object> internalHandler(Payload<OrderChangeStatusForm> request) {
        statusService.updateOrderStatus(request.data());
        Map<String, Object> resp = new HashMap<>(1);
        resp.put("message", "Update success");
        return resp;
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (statusService == null) {
            statusService = resolver.resolveDependencies(OrderChangeStatusService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.order.change-status");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.POST;
    }
}
