package com.bachlinh.order.web.handler.rest.admin.index;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.OrderInDateResp;
import com.bachlinh.order.web.service.business.OrderInDateService;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor_ = @ActiveReflection)
public class TableOrderNewInDateHandler extends AbstractController<Page<OrderInDateResp>, Void> {
    private OrderInDateService orderInDateService;
    private String url;

    @Override
    protected Page<OrderInDateResp> internalHandler(Payload<Void> request) {
        return orderInDateService.getOrdersInDate();
    }

    @Override
    protected void inject() {
        var resolver = getContainerResolver().getDependenciesResolver();
        if (orderInDateService == null) {
            orderInDateService = resolver.resolveDependencies(OrderInDateService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.order.in-date");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
