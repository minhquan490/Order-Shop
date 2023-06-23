package com.bachlinh.order.web.handler.rest.admin.customer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.exception.http.BadVariableException;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.common.CustomerService;

import java.util.Optional;

@ActiveReflection
@RouteProvider
public class CustomerListHandler extends AbstractController<Page<CustomerResp>, Void> {
    private String url;
    private String defaultPageSize;
    private CustomerService customerService;

    @ActiveReflection
    public CustomerListHandler() {
    }

    @Override
    protected Page<CustomerResp> internalHandler(Payload<Void> request) {
        String pageParam = Optional.ofNullable(getNativeRequest().getUrlQueryParam().getFirst("page")).orElse("0");
        String pageSizeParam = Optional.ofNullable(getNativeRequest().getUrlQueryParam().getFirst("size")).orElse(defaultPageSize);
        int page;
        int pageSize;
        try {
            page = Integer.parseInt(pageParam);
            pageSize = Integer.parseInt(pageSizeParam);
        } catch (NumberFormatException e) {
            throw new BadVariableException("Page number and page size must be number");
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        return customerService.getFullInformationOfCustomer(pageable);
    }

    @Override
    protected void inject() {
        DependenciesResolver resolver = getContainerResolver().getDependenciesResolver();
        if (defaultPageSize == null) {
            defaultPageSize = getEnvironment().getProperty("data.default.page.size");
        }
        if (customerService == null) {
            customerService = resolver.resolveDependencies(CustomerService.class);
        }
    }

    @Override
    public String getPath() {
        if (url == null) {
            url = getEnvironment().getProperty("shop.url.admin.customer.list");
        }
        return url;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }
}
