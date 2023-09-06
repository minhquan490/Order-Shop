package com.bachlinh.order.web.handler.rest.customer.info;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.entity.model.Customer;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.MyInfoResp;
import com.bachlinh.order.web.service.common.CustomerService;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@RouteProvider(name = "myInfoHandler")
@ActiveReflection
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MyInfoHandler extends AbstractController<MyInfoResp, Object> {
    private String url;
    private CustomerService customerService;

    @Override
    public AbstractController<MyInfoResp, Object> newInstance() {
        return new MyInfoHandler();
    }

    @Override
    @ActiveReflection
    protected MyInfoResp internalHandler(Payload<Object> request) {
        Customer customer = (Customer) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customerService.getMyInfo(customer.getId());
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (customerService == null) {
            customerService = resolver.resolveDependencies(CustomerService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.customer.my-info");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
