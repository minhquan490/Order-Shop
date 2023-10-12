package com.bachlinh.order.web.handler.rest.admin.customer;

import com.bachlinh.order.core.annotation.ActiveReflection;
import com.bachlinh.order.core.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.core.annotation.Permit;
import com.bachlinh.order.core.enums.Role;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.core.utils.ValidateUtils;
import com.bachlinh.order.web.dto.resp.CustomerResp;
import com.bachlinh.order.web.service.common.CustomerService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

@ActiveReflection
@RouteProvider(name = "customerListHandler")
@Permit(roles = Role.ADMIN)
public class CustomerListHandler extends AbstractController<Collection<CustomerResp>, Void> {
    private String url;
    private String defaultPageSize;
    private CustomerService customerService;

    private CustomerListHandler() {
        super();
    }

    @Override
    public AbstractController<Collection<CustomerResp>, Void> newInstance() {
        return new CustomerListHandler();
    }

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
        if (defaultPageSize == null) {
            defaultPageSize = getEnvironment().getProperty("data.default.page.size");
        }
        if (customerService == null) {
            customerService = resolveService(CustomerService.class);
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
