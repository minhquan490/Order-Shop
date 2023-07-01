package com.bachlinh.order.web.handler.rest.admin.order;

import lombok.NoArgsConstructor;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.form.admin.order.OrderChangeStatusForm;
import com.bachlinh.order.web.service.business.OrderChangeStatusService;

import java.util.HashMap;
import java.util.Map;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class OrderChangeStatusHandler extends AbstractController<Map<String, Object>, OrderChangeStatusForm> {
    private String url;
    private OrderChangeStatusService statusService;

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<OrderChangeStatusForm> request) {
        statusService.updateOrderStatus(request.data());
        Map<String, Object> resp = new HashMap<>(1);
        resp.put("messages", new String[]{"Update success"});
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
