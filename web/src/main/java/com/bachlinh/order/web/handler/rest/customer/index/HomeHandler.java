package com.bachlinh.order.web.handler.rest.customer.index;

import com.bachlinh.order.annotation.ActiveReflection;
import com.bachlinh.order.annotation.RouteProvider;
import com.bachlinh.order.core.enums.RequestMethod;
import com.bachlinh.order.core.http.Payload;
import com.bachlinh.order.handler.controller.AbstractController;
import com.bachlinh.order.web.dto.resp.ProductResp;
import com.bachlinh.order.web.service.common.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RouteProvider(name = "homeHandler")
@ActiveReflection
public class HomeHandler extends AbstractController<Map<String, Object>, Object> {
    private String homeUrl;
    private ProductService productService;

    private HomeHandler() {
    }

    @Override
    public AbstractController<Map<String, Object>, Object> newInstance() {
        return new HomeHandler();
    }

    @Override
    @ActiveReflection
    protected Map<String, Object> internalHandler(Payload<Object> request) {
        return home();
    }

    @Override
    protected void inject() {
        if (productService == null) {
            productService = resolveService(ProductService.class);
        }
    }

    @Override
    public String getPath() {
        if (homeUrl == null) {
            homeUrl = getEnvironment().getProperty("shop.url.home");
        }
        return homeUrl;
    }

    @Override
    public RequestMethod getRequestMethod() {
        return RequestMethod.GET;
    }

    private Map<String, Object> home() {
        Page<ProductResp> productDtos = productService.productList(Pageable.ofSize(10));
        Set<String> categories = new HashSet<>();
        productDtos.stream()
                .map(ProductResp::categories)
                .distinct()
                .map(Arrays::asList)
                .collect(Collectors.toSet())
                .forEach(categories::addAll);
        Map<String, Object> resp = new HashMap<>();
        resp.put("products", productDtos);
        resp.put("categories", categories);
        return resp;
    }
}
