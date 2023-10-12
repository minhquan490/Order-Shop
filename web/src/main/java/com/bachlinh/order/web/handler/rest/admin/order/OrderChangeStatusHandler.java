package com.bachlinh.order.web.handler.rest.admin.order;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.EnableCsrf;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.annotation.Transactional;
import com.bachlinh.order.core.enums.Isolation;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.form.admin.order.OrderChangeStatusForm;
import com.bachlinh.order.web.service.business.OrderChangeStatusService;
import org.apache.http.HttpStatus;

import java.util.Map;

@ActiveReflection
@RouteProvider(name = "orderChangeStatusHandler")
@Permit(roles = Role.ADMIN)
@EnableCsrf
public class OrderChangeStatusHandler extends AbstractController<Map<String, Object>, OrderChangeStatusForm> {
    private String url;
    private OrderChangeStatusService statusService;

    private OrderChangeStatusHandler() {
        super();
    }

    @Override
    public AbstractController<Map<String, Object>, OrderChangeStatusForm> newInstance() {
        return new OrderChangeStatusHandler();
    }

    @Override
    @ActiveReflection
    @Transactional(isolation = Isolation.READ_COMMITTED, timeOut = 10)
    protected Map<String, Object> internalHandler(Payload<OrderChangeStatusForm> request) {
        statusService.updateOrderStatus(request.data());
        return createDefaultResponse(HttpStatus.SC_OK, new String[]{"Update success"});
    }

    @Override
    protected void inject() {
        if (statusService == null) {
            statusService = resolveService(OrderChangeStatusService.class);
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
