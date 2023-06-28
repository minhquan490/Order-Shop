package com.bachlinh.order.web.handler.rest.admin.customer;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.service.container.DependenciesResolver;
import com.bachlinh.order.utils.ValidateUtils;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.common.CustomerService;

import java.util.Collection;
import java.util.Optional;

@ActiveReflection
@RouteProvider
@NoArgsConstructor(onConstructor = @__({@ActiveReflection}))
public class CustomerListHandler extends AbstractController<Collection<CustomerResp>, Void> {
    private String url;
    private String defaultPageSize;
    private CustomerService customerService;

    @Override
    @ActiveReflection
    protected Collection<CustomerResp> internalHandler(Payload<Void> request) {
        String pageParam = Optional.ofNullable(getNativeRequest().getUrlQueryParam().getFirst("page")).orElse("0");
        String pageSizeParam = Optional.ofNullable(getNativeRequest().getUrlQueryParam().getFirst("size")).orElse(defaultPageSize);
        int page;
        int pageSize;
        if (!ValidateUtils.isNumber(pageParam)) {
            page = 1;
        } else {
            page = Integer.parseInt(pageParam);
        }
        if (!ValidateUtils.isUrlValid(pageSizeParam)) {
            pageSize = Integer.parseInt(defaultPageSize);
        } else {
            pageSize = Integer.parseInt(pageSizeParam);
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        return customerService.getFullInformationOfCustomer(pageable).toList();
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
